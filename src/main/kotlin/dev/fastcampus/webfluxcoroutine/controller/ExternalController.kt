package dev.fastcampus.webfluxcoroutine.controller

import dev.fastcampus.webfluxcoroutine.exception.ExternalApi
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

class ExternalController(
    private val externalApi: ExternalApi,
) {

    @GetMapping("/external/delay")
    suspend fun delay() {
        externalApi.delay()
    }

    @GetMapping("/external/circuit", "/external/circuit/", "/external/circuit/{flag}")
    fun testCircuitBreaker(@PathVariable flag: String?) {
        if (flag?.lowercase() == "n") {
            throw RuntimeException("fail on child")
        } else {
            "success"
        }
    }
}