package com.robotutor.iot.utils.filters

import com.robotutor.iot.utils.config.AppConfig
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Component

@Component
class RouteValidator(private val appConfig: AppConfig) {

    fun isSecured(request: ServerHttpRequest): Boolean {
        return !appConfig.openApiEndpoints.any { request.uri.path == it }
    }
}
