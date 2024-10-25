package com.robotutor.iot.utils.gateway

import com.robotutor.iot.service.WebClientWrapper
import com.robotutor.iot.utils.gateway.views.UserAuthenticationResponseData
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class AuthGateway(private val webClientWrapper: WebClientWrapper) {
    fun validate(token: String?): Mono<UserAuthenticationResponseData> {
        val header = mapOf("token" to "$token")
        return webClientWrapper.get(
            baseUrl = "",
            path = "",
            headers = header,
            returnType = UserAuthenticationResponseData::class.java
        )
    }
}
