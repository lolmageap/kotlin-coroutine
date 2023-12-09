package dev.fastcampus.webfluxcoroutine.config

import io.micrometer.context.ContextRegistry
import org.slf4j.MDC
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Hooks
import reactor.core.publisher.Mono
import reactor.util.context.Context
import java.util.*

@Component
@Order(1)
class MdcFilter: WebFilter {

    // reactor, publisher 를 사용할 때 설정
    init {
        Hooks.enableAutomaticContextPropagation()
        ContextRegistry.getInstance().registerThreadLocalAccessor(
            "txid",
            { MDC.get("txid") },
            { value -> MDC.put("txid", value) },
            { MDC.remove("txid") }
        )
    }

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val uuid = exchange.request.headers["x-txid"]?.firstOrNull() ?: "${UUID.randomUUID()}"
        MDC.put("txid", uuid)
        return chain.filter(exchange).contextWrite {
            Context.of("txid", uuid)
        }
    }
}