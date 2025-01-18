package com.robotutor.iot.utils.filters

import com.robotutor.iot.exceptions.UnAuthorizedException
import com.robotutor.iot.utils.config.AppConfig
import com.robotutor.iot.utils.createMono
import com.robotutor.iot.utils.exceptions.IOTError
import com.robotutor.iot.utils.gateway.AuthGateway
import com.robotutor.iot.utils.models.UserData
import com.robotutor.loggingstarter.LogDetails
import com.robotutor.loggingstarter.Logger
import com.robotutor.loggingstarter.models.RequestDetails
import com.robotutor.loggingstarter.models.ResponseDetails
import com.robotutor.loggingstarter.models.ServerWebExchangeDTO
import com.robotutor.loggingstarter.serializer.DefaultSerializer.serialize
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.LocalDateTime
import java.time.ZoneOffset

const val TRACE_ID_HEADER_KEY = "x-trace-id"

@Component
@Order(1)
class ApiFilter(
    private val routeValidator: RouteValidator,
    private val authGateway: AuthGateway,
    private val appConfig: AppConfig,
) : WebFilter {
    private val logger = Logger(ApiFilter::class.java)

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val startTime = LocalDateTime.now()
        val additionalDetails = mapOf("method" to exchange.request.method, "path" to exchange.request.uri.path)
        return authorize(exchange)
            .flatMap { userData ->
                chain.filter(exchange)
                    .contextWrite { it.put(UserData::class.java, userData) }
            }
            .onErrorResume {
                val unAuthorizedException = UnAuthorizedException(IOTError.IOT0101)
                val response = exchange.response

                response.statusCode = HttpStatus.UNAUTHORIZED
                response.headers.contentType = MediaType.APPLICATION_JSON
                val content = response.bufferFactory()
                    .wrap(serialize(unAuthorizedException.errorResponse()).toByteArray())
                response.writeWith(createMono(content))
            }
            .publishOn(Schedulers.boundedElastic())
            .contextWrite {
                it.put(ServerWebExchangeDTO::class.java, ServerWebExchangeDTO.from(exchange))
                    .put("startTime", startTime)
            }
            .doFinally {
                val logDetails = LogDetails.create(
                    message = "Successfully send api response",
                    traceId = getTraceId(exchange),
                    requestDetails = RequestDetails(
                        method = exchange.request.method,
                        headers = exchange.request.headers,
                        uriWithParams = exchange.request.uri.toString(),
                        body = exchange.request.body.toString()
                    ),
                    responseDetails = ResponseDetails(
                        headers = exchange.response.headers,
                        statusCode = exchange.response.statusCode.toString(),
                        time = (LocalDateTime.now()
                            .toEpochSecond(ZoneOffset.UTC) - startTime.toEpochSecond(ZoneOffset.UTC)) * 1000,
                        body = exchange.response.bufferFactory().toString()
                    ),
                    additionalDetails = additionalDetails
                )
                logger.info(logDetails)
            }
    }

    private fun authorize(exchange: ServerWebExchange): Mono<UserData> {
        return Mono.deferContextual { context ->
            try {
                createMono(context.get(UserData::class.java))
            } catch (ex: Exception) {
                authorizeUser(exchange)
            }
        }
    }

    private fun authorizeUser(exchange: ServerWebExchange): Mono<UserData> {
        val token = exchange.request.headers[HttpHeaders.AUTHORIZATION]?.get(0)

        return if (routeValidator.isSecured(exchange.request)) {
            if (token == appConfig.internalAccessToken) {
                createMono(UserData("Internal user", "role"))
            } else {
                authGateway.validate(exchange)
            }
        } else {
            createMono(UserData("unauthorized user", "role"))
        }
    }
}

fun getTraceId(exchange: ServerWebExchange): String {
    return exchange.request.headers[TRACE_ID_HEADER_KEY]?.first() ?: "missing-trace-id"
}
