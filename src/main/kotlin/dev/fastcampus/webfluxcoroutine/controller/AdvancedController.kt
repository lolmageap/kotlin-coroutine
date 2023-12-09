package dev.fastcampus.webfluxcoroutine.controller

import dev.fastcampus.webfluxcoroutine.service.AdvancedService
import mu.KotlinLogging
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

private val logger = KotlinLogging.logger {}

@RestController
class AdvancedController(
    private val advancedService: AdvancedService,
) {
    @GetMapping("/test/mdc")
    suspend fun testRequestTxId() {
        logger.debug { "Hello MDC TxId start" }
        advancedService.mdc()
        logger.debug { "Hello MDC TxId end" }
    }

}