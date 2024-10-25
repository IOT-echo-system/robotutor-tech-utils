package com.robotutor.iot.utils.gateway

import com.robotutor.iot.service.WebClientWrapper
import com.robotutor.iot.utils.models.UserAuthenticationData
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class BoardGateway(private val webClientWrapper: WebClientWrapper) {
    fun isValidBoard(
        userAuthenticationData: UserAuthenticationData,
        boardId: String
    ): Mono<Boolean> {
        return webClientWrapper.get(baseUrl = "", path = "", returnType = Boolean::class.java)
    }
}
