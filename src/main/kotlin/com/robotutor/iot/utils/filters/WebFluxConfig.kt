package com.robotutor.iot.utils.filters

import org.springframework.stereotype.Component
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer

@Component
class WebConfig(
    private val boardDataResolver: BoardDataResolver,
    private val boardAuthenticationDataResolver: BoardAuthenticationDataResolver,
    private val authenticationDataResolver: AuthenticationDataResolver
) : WebFluxConfigurer {
    override fun configureArgumentResolvers(configurer: ArgumentResolverConfigurer) {
        configurer.addCustomResolver(authenticationDataResolver)
        configurer.addCustomResolver(boardDataResolver)
        configurer.addCustomResolver(boardAuthenticationDataResolver)
    }
}
