package com.robotutor.iot.utils.gateway

import com.robotutor.iot.service.CacheService
import com.robotutor.iot.service.WebClientWrapper
import com.robotutor.iot.utils.config.AppConfig
import com.robotutor.iot.utils.filters.getTraceId
import com.robotutor.iot.utils.gateway.views.AuthenticationResponseData
import com.robotutor.iot.utils.models.UserData
import com.robotutor.loggingstarter.Logger
import com.robotutor.loggingstarter.logOnError
import com.robotutor.loggingstarter.logOnSuccess
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class AuthGateway(
    private val webClient: WebClientWrapper,
    private val appConfig: AppConfig,
    private val cacheService: CacheService
) {
    val logger = Logger(this::class.java)
    fun validate(exchange: ServerWebExchange): Mono<UserData> {
        val traceId = getTraceId(exchange)
        return cacheService.retrieve("authGateway::$traceId", UserData::class.java, 60) {
            webClient.get(
                baseUrl = appConfig.authServiceBaseUrl,
                path = appConfig.validatePath,
                returnType = AuthenticationResponseData::class.java,
            )
                .map { authenticationResponseData -> UserData.from(authenticationResponseData) }
        }
            .logOnSuccess(logger, "Successfully authenticated user for $traceId")
            .logOnError(logger, "", "Failed to authenticate user for $traceId")
    }
}
