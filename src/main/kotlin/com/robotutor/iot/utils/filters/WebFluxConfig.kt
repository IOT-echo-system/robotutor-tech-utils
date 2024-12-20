package com.robotutor.iot.utils.filters

import org.springframework.stereotype.Component
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer

@Component
class WebConfig(
    private val boardDataResolver: BoardDataResolver,
    private val userDataResolver: UserDataResolver
) : WebFluxConfigurer {
    override fun configureArgumentResolvers(configurer: ArgumentResolverConfigurer) {
        configurer.addCustomResolver(userDataResolver)
        configurer.addCustomResolver(boardDataResolver)
    }
}
