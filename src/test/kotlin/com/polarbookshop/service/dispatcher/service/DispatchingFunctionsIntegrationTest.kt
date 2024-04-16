package com.polarbookshop.service.dispatcher.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.polarbookshop.service.dispatcher.model.OrderAcceptedMessage
import com.polarbookshop.service.dispatcher.model.OrderDispatchedMessage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.function.context.FunctionCatalog
import org.springframework.cloud.function.context.test.FunctionalSpringBootTest
import org.springframework.messaging.support.GenericMessage
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import java.util.function.Function

@FunctionalSpringBootTest
class DispatchingFunctionsIntegrationTest(
  @Autowired
  private val catalog: FunctionCatalog
) {

  @Test
  fun `pack and label order`() {
    val packAndLabel = catalog
      .lookup<Function<OrderAcceptedMessage, Flux<GenericMessage<ByteArray>>>>("pack|label")
    val orderId = 121L

    StepVerifier.create(packAndLabel.apply(OrderAcceptedMessage(orderId)))
      .expectNextMatches { message ->
        val payload = jacksonObjectMapper().readValue(message.payload, OrderDispatchedMessage::class.java)
        payload.orderId == orderId
      }
      .verifyComplete()
  }

  @Test
  fun `pack order`() {
    val pack = catalog.lookup<Function<OrderAcceptedMessage, Long>>("pack")
    val orderId = 121L
    assertThat(pack.apply(OrderAcceptedMessage(orderId))).isEqualTo(orderId);
  }

  @Test
  fun `label order`() {
    val label = catalog.lookup<Function<Flux<Long>, Flux<OrderDispatchedMessage>>>("label")
    val orderId = Flux.just(121L)
    StepVerifier.create(label.apply(orderId))
      .expectNextMatches { dispatchedOrder ->
        dispatchedOrder.equals(OrderDispatchedMessage(121L))
      }
  }

}
