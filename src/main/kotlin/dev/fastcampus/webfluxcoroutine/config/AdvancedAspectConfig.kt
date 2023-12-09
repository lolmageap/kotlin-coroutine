package dev.fastcampus.webfluxcoroutine.config

import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component
import kotlin.coroutines.Continuation

@Aspect
@Component
class AdvancedAspectConfig {
    private val logger = KotlinLogging.logger {}

    @Around(
        """
        @annotation(org.springframework.web.bind.annotation.GetMapping)
    """
    )
    fun wrapCoroutineController(joinPoint: ProceedingJoinPoint): Any? {
        logger.debug { ">> before wrapper" }

        return try {
//            withContext(MDCContext()) {}
            logger.debug { ">> method: ${joinPoint.signature}" }
            logger.debug { ">> arg: ${joinPoint.args.toList()}" }

            val continuation = joinPoint.args.last() as Continuation<*>
            val newContext = continuation.context + MDCContext()
            val newContinuation = Continuation(newContext) { continuation.resumeWith(it) }

            val newArgs = joinPoint.args.dropLast(1) + newContinuation
            joinPoint.proceed(newArgs.toTypedArray())
        } finally {
            logger.debug { ">> after wrapper" }
        }
    }

}