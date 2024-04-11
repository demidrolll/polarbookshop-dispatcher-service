package com.polarbookshop.service.dispatcher.service

import com.polarbookshop.service.dispatcher.model.OrderAcceptedMessage
import com.polarbookshop.service.dispatcher.model.OrderDispatchedMessage
import org.apache.logging.log4j.LogManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Flux

@Configuration
class DispatchingFunctions {

  @Bean
  fun pack(): (OrderAcceptedMessage) -> Long = { orderAcceptedMessage ->
    log.info("The order with id {} is packed.", orderAcceptedMessage.orderId)
    orderAcceptedMessage.orderId
  }

  @Bean
  fun label(): (Flux<Long>) -> Flux<OrderDispatchedMessage> = { input ->
    input.map { orderId ->
      log.info("The order with id {} is labeled.", orderId)
      OrderDispatchedMessage(orderId)
    }
  }

  companion object {
    private val log = LogManager.getLogger()
  }
}
