package com.robotutor.iot.utils.filters

import com.robotutor.iot.exceptions.AccessDeniedException
import com.robotutor.iot.exceptions.IOTError
import com.robotutor.iot.utils.createMono
import com.robotutor.iot.utils.createMonoError
import com.robotutor.iot.utils.filters.annotations.RequirePolicy
import com.robotutor.iot.utils.gateway.PolicyGateway
import com.robotutor.iot.utils.models.UserData
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.reactive.BindingContext
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class UserDataResolver(private val policyGateway: PolicyGateway) : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == UserData::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        bindingContext: BindingContext,
        exchange: ServerWebExchange
    ): Mono<Any> {
        return Mono.deferContextual { context ->
            val annotation = parameter.getMethodAnnotation(RequirePolicy::class.java)
            val userData = context.get(UserData::class.java)
            if (annotation != null) {
                policyGateway.getPolicies(userData.roleId)
                    .map { policiesResponseData ->
                        policiesResponseData.policies.any { it.name == annotation.policyName }
                    }
                    .flatMap {
                        if (it) {
                            createMono(userData)
                        } else {
                            createMonoError(AccessDeniedException(IOTError.IOT0103))
                        }
                    }
            } else {
                createMono(userData)
            }
        }
    }
}
