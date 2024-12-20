package com.robotutor.iot.utils.gateway

import com.robotutor.iot.service.WebClientWrapper
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class BoardGateway(private val webClientWrapper: WebClientWrapper) {
    fun isValidBoard(
    ): Mono<Boolean> {
        return webClientWrapper.get(baseUrl = "", path = "", returnType = Boolean::class.java)
    }
}
