package com.robotutor.iot.utils.filters

import com.robotutor.iot.exceptions.IOTError
import com.robotutor.iot.exceptions.UnAuthorizedException
import com.robotutor.iot.utils.createMono
import com.robotutor.iot.utils.createMonoError
import com.robotutor.iot.utils.gateway.AuthGateway
import com.robotutor.iot.utils.models.UserAuthenticationData
import com.robotutor.loggingstarter.logOnSuccess
import com.robotutor.loggingstarter.serializer.DefaultSerializer.serialize
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.LocalDateTime

@Component
class ApiFilter(
    private val routeValidator: RouteValidator,
    private val authGateway: AuthGateway
) : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val startTime = LocalDateTime.now()
        return authorize(exchange)
            .flatMap { userAuthenticationData ->
                chain.filter(exchange)
                    .contextWrite {
                        it.put(UserAuthenticationData::class.java, userAuthenticationData)
                    }
            }
            .onErrorResume {
                val unAuthorizedException = UnAuthorizedException(IOTError.IOT0101)
                val response = exchange.response

                response.statusCode = HttpStatus.UNAUTHORIZED
                response.headers.contentType = MediaType.APPLICATION_JSON
                response.writeWith(
                    Mono.just(
                        response.bufferFactory().wrap(serialize(unAuthorizedException.errorResponse()).toByteArray())
                    )
                )
            }
            .publishOn(Schedulers.boundedElastic())
            .contextWrite { it.put(ServerWebExchange::class.java, exchange) }
            .contextWrite { it.put("startTime", startTime) }
            .doFinally {
                Mono.just("")
                    .logOnSuccess(
                        "Successfully send api response",
                        additionalDetails = mapOf(
                            "method" to exchange.request.method,
                            "path" to exchange.request.uri.path
                        )
                    )
                    .contextWrite { it.put(ServerWebExchange::class.java, exchange) }
                    .contextWrite { it.put("startTime", startTime) }
                    .subscribe()
            }
    }

    private fun authorize(exchange: ServerWebExchange): Mono<UserAuthenticationData> {
        return Mono.deferContextual { context ->
            try {
                createMono(context.get(UserAuthenticationData::class.java))
            } catch (ex: Exception) {
                authorizeUser(exchange)
            }
        }
    }

    private fun authorizeUser(exchange: ServerWebExchange): Mono<UserAuthenticationData> {
        return if (routeValidator.isSecured(exchange.request)) {
            authGateway.validate(exchange.request.headers[HttpHeaders.AUTHORIZATION]?.get(0))
                .map { userAuthenticationResponseData -> UserAuthenticationData.from(userAuthenticationResponseData) }
                .contextWrite { it.put(ServerWebExchange::class.java, exchange) }
        } else {
            createMono(UserAuthenticationData("Authorization not required", "account", "role", null))
        }
            .flatMap { userAuthenticationData ->
                if (isAllowedForAccounts(userAuthenticationData, exchange.request)) {
                    createMono(userAuthenticationData)
                } else {
                    createMonoError(UnAuthorizedException(IOTError.IOT0101))
                }
            }
    }

    private fun isAllowedForAccounts(
        userAuthenticationData: UserAuthenticationData,
        request: ServerHttpRequest
    ): Boolean {
        val areAccountAndRoleNotBlank =
            userAuthenticationData.accountId.isNotBlank() && userAuthenticationData.roleId.isNotBlank()
        return areAccountAndRoleNotBlank || routeValidator.isOpenForAccounts(request)
    }
}
