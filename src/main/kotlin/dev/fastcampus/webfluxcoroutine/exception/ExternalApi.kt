package dev.fastcampus.webfluxcoroutine.exception

import io.github.resilience4j.circuitbreaker.CallNotPermittedException
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.kotlin.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.kotlin.circuitbreaker.executeSuspendFunction
import io.github.resilience4j.kotlin.ratelimiter.RateLimiterConfig
import io.github.resilience4j.kotlin.ratelimiter.executeSuspendFunction
import io.github.resilience4j.ratelimiter.RateLimiter
import io.github.resilience4j.ratelimiter.RequestNotPermitted
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

private val logger = KotlinLogging.logger { }

@Service
class ExternalApi(
    @Value("\${api.externalUrl}")
    private val externalUrl: String,
) {

    private val client = WebClient.builder().baseUrl(externalUrl)
        .defaultHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build()

    suspend fun delay() {
        return client.get().uri("/delay").retrieve().awaitBody()
    }

    // 8081 port 로 보내야하는데 아직 8081 서버 미구현 -> TODO : 멀티모듈로 구현해보기
    suspend fun testCircuitBreaker(flag: String): String {
        logger.debug { "1. request call" }

        return try {
            rateLimiter.executeSuspendFunction {
                circuitBreaker.executeSuspendFunction {
                    logger.debug { "2. call external" }
                    client.get().uri("/external/circuit/$flag")
                        .retrieve()
                        .awaitBody()
                }
            }
        } catch (e: CallNotPermittedException) {
            "call later (block by circuit breaker)"
        } catch (e: RequestNotPermitted) {
            "call later (block by rate limiter)"
        } finally {
            logger.debug { "3. done" }
        }
    }

    /**
     * close : 회로가 닫힘 -> 정상
     * open : 회로가 열림 -> 차단
     * half-open : 반열림 -> 간 보는 것
     */
    val circuitBreaker = CircuitBreaker.of("test", CircuitBreakerConfig {
        slidingWindowSize(10)
        failureRateThreshold(20.0F)
        // open (차단) 후 몇 초 후 close (열림) 상태로 변경 : 완전 close 가 아니라 half-open
        waitDurationInOpenState(10.seconds.toJavaDuration())
        // half-open 상태 에서 허용할 요청 수
        permittedNumberOfCallsInHalfOpenState(3)
    })

    val rateLimiter = RateLimiter.of("rps-limiter", RateLimiterConfig {
        limitForPeriod(2)
        timeoutDuration(5.seconds.toJavaDuration())
        limitRefreshPeriod(10.seconds.toJavaDuration())
    })

}