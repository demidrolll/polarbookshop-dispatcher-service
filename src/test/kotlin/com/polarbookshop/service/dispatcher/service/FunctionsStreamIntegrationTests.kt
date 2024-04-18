package com.polarbookshop.service.dispatcher.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.polarbookshop.service.dispatcher.model.OrderAcceptedMessage
import com.polarbookshop.service.dispatcher.model.OrderDispatchedMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.stream.binder.test.InputDestination
import org.springframework.cloud.stream.binder.test.OutputDestination
import org.springframework.integration.support.MessageBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

@SpringBootTest
class FunctionsStreamIntegrationTests(
  @Autowired
  private val input: InputDestination,
  @Autowired
  private val output: OutputDestination,
) {

  @Test
  fun `when order accepted then dispatched`() {
    val orderId = 121L
    val inputMessage = MessageBuilder.withPayload(OrderAcceptedMessage(orderId)).build()
    val expectedMessage = MessageBuilder.withPayload(OrderDispatchedMessage(orderId)).build()

    input.send(inputMessage)
    assertThat(jacksonObjectMapper().readValue(output.receive().payload, OrderDispatchedMessage::class.java))
      .isEqualTo(expectedMessage.payload)
  }
}
