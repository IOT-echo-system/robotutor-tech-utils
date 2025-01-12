package com.robotutor.iot.utils.filters

import com.robotutor.iot.exceptions.AccessDeniedException
import com.robotutor.iot.utils.exceptions.IOTError
import com.robotutor.iot.utils.createMonoError
import com.robotutor.iot.utils.filters.annotations.RequirePolicy
import com.robotutor.iot.utils.gateway.PolicyGateway
import com.robotutor.loggingstarter.logOnError
import com.robotutor.loggingstarter.serializer.DefaultSerializer.serialize
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
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
                        return@flatMap validatePolicy(exchange, chain, requirePolicy.policyName)
                    }
                }
                chain.filter(exchange)
            }
    }

    private fun validatePolicy(exchange: ServerWebExchange, chain: WebFilterChain, policyName: String): Mono<Void> {
        return policyGateway.getPolicies(getTraceId(exchange))
            .collectList()
            .map { policies ->
                policies.any { it.name == policyName }
            }
            .flatMap {
                if (it) {
                    chain.filter(exchange)
                } else {
                    createMonoError<Void>(AccessDeniedException(IOTError.IOT0103))
                        .logOnError(errorCode = IOTError.IOT0103.errorCode, errorMessage = IOTError.IOT0103.message)
                        .onErrorResume {
                            val unAuthorizedException = AccessDeniedException(IOTError.IOT0103)
                            val response = exchange.response

                            response.statusCode = HttpStatus.UNAUTHORIZED
                            response.headers.contentType = MediaType.APPLICATION_JSON
                            response.writeWith(
                                Mono.just(
                                    response.bufferFactory()
                                        .wrap(serialize(unAuthorizedException.errorResponse()).toByteArray())
                                )
                            )
                        }
                }
            }
    }
}
