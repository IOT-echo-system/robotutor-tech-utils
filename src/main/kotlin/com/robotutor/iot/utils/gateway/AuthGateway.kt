package com.robotutor.iot.utils.gateway

import com.robotutor.iot.service.CacheService
import com.robotutor.iot.service.WebClientWrapper
import com.robotutor.iot.utils.config.AppConfig
import com.robotutor.iot.utils.filters.getTraceId
import com.robotutor.iot.utils.gateway.views.AuthenticationResponseData
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class AuthGateway(
    private val webClient: WebClientWrapper,
    private val appConfig: AppConfig,
    private val cacheService: CacheService
) {
    fun validate(exchange: ServerWebExchange): Mono<AuthenticationResponseData> {
        val token = exchange.request.headers[org.springframework.http.HttpHeaders.AUTHORIZATION]?.get(0) ?: ""
        val traceId = getTraceId(exchange)
        val headers = mapOf("token" to token)
        return cacheService.retrieve("authGateway::$traceId", AuthenticationResponseData::class.java, 60) {
            webClient.get(
                baseUrl = appConfig.authServiceBaseUrl,
                path = appConfig.validatePath,
                headers = headers,
                returnType = AuthenticationResponseData::class.java,
                skipLoggingResponseBody = false,
                skipLoggingAdditionalDetails = false
            )
        }
    }
}
