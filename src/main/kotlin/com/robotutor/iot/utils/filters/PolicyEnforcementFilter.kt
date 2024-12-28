package com.robotutor.iot.utils.filters

import com.robotutor.iot.exceptions.AccessDeniedException
import com.robotutor.iot.exceptions.IOTError
import com.robotutor.iot.utils.createMono
import com.robotutor.iot.utils.createMonoError
import com.robotutor.iot.utils.filters.annotations.RequirePolicy
import com.robotutor.iot.utils.gateway.PolicyGateway
import com.robotutor.loggingstarter.logOnError
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
@Order(2)
class PolicyEnforcementFilter(
    private val handlerMapping: RequestMappingHandlerMapping,
    private val policyGateway: PolicyGateway
) : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        return handlerMapping.getHandler(exchange)
            .flatMap { handler ->
                if (handler is HandlerMethod) {
                    val requirePolicy = handler.getMethodAnnotation(RequirePolicy::class.java)
                    if (requirePolicy != null) {
                        return@flatMap validatePolicy(requirePolicy.policyName).flatMap { Mono.empty() }
                    }
                }
                chain.filter(exchange)
            }
    }

    private fun validatePolicy(policyName: String): Mono<String> {
        return policyGateway.getPolicies()
            .collectList()
            .map { policies ->
                policies.any { it.name == policyName }
            }
            .flatMap {
                if (it) {
                    createMono(policyName)
                } else {
                    createMonoError(AccessDeniedException(IOTError.IOT0103))
                }
            }
            .logOnError(errorCode = IOTError.IOT0103.message, errorMessage = IOTError.IOT0103.message)
    }
}
