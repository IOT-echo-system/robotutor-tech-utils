package com.robotutor.iot.utils.filters.resolvers

import com.robotutor.iot.exceptions.UnAuthorizedException
import com.robotutor.iot.utils.createMono
import com.robotutor.iot.utils.createMonoError
import com.robotutor.iot.utils.exceptions.IOTError
import com.robotutor.iot.utils.models.PremisesData
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.reactive.BindingContext
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class PremisesDataResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == PremisesData::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        bindingContext: BindingContext,
        exchange: ServerWebExchange
    ): Mono<Any> {
        return Mono.deferContextual { context ->
            val premisesData = context.getOrEmpty<PremisesData>(PremisesData::class.java)
            if (premisesData.isPresent) {
                createMono(premisesData.get())
            } else {
                createMonoError(UnAuthorizedException(IOTError.IOT0106))
            }
        }
    }
}
