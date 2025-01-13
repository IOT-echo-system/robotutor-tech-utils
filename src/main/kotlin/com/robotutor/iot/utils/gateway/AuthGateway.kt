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
        val traceId = getTraceId(exchange)
        return cacheService.retrieve("authGateway::$traceId", AuthenticationResponseData::class.java, 60) {
            webClient.get(
                baseUrl = appConfig.authServiceBaseUrl,
                path = appConfig.validatePath,
                returnType = AuthenticationResponseData::class.java,
                skipLoggingResponseBody = false,
                skipLoggingAdditionalDetails = false
            )
        }
    }
}
