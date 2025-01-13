package com.robotutor.iot.utils.filters.resolvers

import com.robotutor.iot.exceptions.UnAuthorizedException
import com.robotutor.iot.utils.createMono
import com.robotutor.iot.utils.createMonoError
import com.robotutor.iot.utils.exceptions.IOTError
import com.robotutor.iot.utils.models.UserData
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.reactive.BindingContext
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class UserDataResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == UserData::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        bindingContext: BindingContext,
        exchange: ServerWebExchange
    ): Mono<Any> {
        return Mono.deferContextual { context ->
            val userData = context.getOrEmpty<UserData>(UserData::class.java)
            if (userData.isPresent) {
                println("----------successfully resolved user data for ${exchange.request.path.value()}-------------")
                createMono(userData.get())
            } else {
                println("----------failed to resolve user data for ${exchange.request.path.value()}-------------")
                createMonoError(UnAuthorizedException(IOTError.IOT0105))
            }
        }
    }
}
