package com.robotutor.iot.utils.filters

import com.robotutor.iot.utils.filters.resolvers.PremisesDataResolver
import com.robotutor.iot.utils.filters.resolvers.UserDataResolver
import org.springframework.stereotype.Component
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer

@Component
class WebFluxConfig(
    private val userDataResolver: UserDataResolver,
    private val premisesDataResolver: PremisesDataResolver
) : WebFluxConfigurer {
    override fun configureArgumentResolvers(configurer: ArgumentResolverConfigurer) {
        configurer.addCustomResolver(userDataResolver)
        configurer.addCustomResolver(premisesDataResolver)
    }
}
