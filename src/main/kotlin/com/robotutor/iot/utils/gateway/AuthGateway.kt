package com.robotutor.iot.utils.gateway

import com.robotutor.iot.service.CacheService
import com.robotutor.iot.service.WebClientWrapper
import com.robotutor.iot.utils.config.AppConfig
import com.robotutor.iot.utils.gateway.views.AuthenticationResponseData
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class AuthGateway(
    private val webClient: WebClientWrapper,
    private val appConfig: AppConfig,
    private val cacheService: CacheService
) {
    fun validate(token: String, traceId: String): Mono<AuthenticationResponseData> {
        val header = mapOf("token" to token)
        return cacheService.retrieve("authGateway::$traceId", AuthenticationResponseData::class.java, 60) {
            webClient.get(
                baseUrl = appConfig.authServiceBaseUrl,
                path = appConfig.validatePath,
                headers = header,
                returnType = AuthenticationResponseData::class.java
            )
        }
    }
}
