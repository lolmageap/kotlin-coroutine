package dev.fastcampus.webfluxcoroutine.controller

import dev.fastcampus.webfluxcoroutine.exception.ExternalApi
import dev.fastcampus.webfluxcoroutine.service.AdvancedService
import mu.KotlinLogging
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

private val logger = KotlinLogging.logger {}

@RestController
class AdvancedController(
    private val advancedService: AdvancedService,
    private val externalApi: ExternalApi,
) {
    @GetMapping("/test/mdc")
    suspend fun testRequestTxId() {
        logger.debug { "Hello MDC TxId start" }
        advancedService.mdc()
        logger.debug { "Hello MDC TxId end" }
    }

    @GetMapping("/test/mdc2")
    fun testAnother() {
        logger.debug { "test another !!" }
    }

    @GetMapping("/test/error")
    fun error() {
        logger.debug { "request error" }
        throw RuntimeException("yahoo~~")
    }

    @GetMapping("/test/circuit")
    suspend fun testCircuitBreaker(@PathVariable flag: String?) {
        if (flag?.lowercase() == "n") {
            throw RuntimeException("fail on child")
        } else {
            "success"
        }
    }

}