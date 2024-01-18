package dev.fastcampus.webfluxcoroutine.config

import kotlinx.coroutines.slf4j.MDCContext
import mu.KotlinLogging
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.core.KotlinDetector
import org.springframework.stereotype.Component
import kotlin.coroutines.Continuation

@Aspect
@Component
class AspectConfig {
    private val logger = KotlinLogging.logger {}

    @Around("""
        @annotation(org.springframework.web.bind.annotation.RequestMapping) || 
        @annotation(org.springframework.web.bind.annotation.GetMapping) || 
        @annotation(org.springframework.web.bind.annotation.PostMapping) || 
        @annotation(org.springframework.web.bind.annotation.PatchMapping) || 
        @annotation(org.springframework.web.bind.annotation.PutMapping) || 
        @annotation(org.springframework.web.bind.annotation.DeleteMapping) 
    """
    )
    fun wrapCoroutineController(joinPoint: ProceedingJoinPoint): Any? {
        logger.debug { ">> before wrapper" }

        return if(joinPoint.hasSuspendFunction) {
            logger.debug { ">> in suspend function" }

            val continuation = joinPoint.args.last() as Continuation<*>
            val newContext = continuation.context + MDCContext()
            val newContinuation = Continuation(newContext) { continuation.resumeWith(it) }

            val newArgs = joinPoint.args.dropLast(1) + newContinuation
            joinPoint.proceed(newArgs.toTypedArray())
        } else {
            logger.debug { ">> in non-suspend function" }
            joinPoint.proceed()
        }
    }

    private val ProceedingJoinPoint.hasSuspendFunction: Boolean
        get() {
            val methodSignature = this.signature as MethodSignature
            return KotlinDetector.isSuspendingFunction(methodSignature.method)
        }

}