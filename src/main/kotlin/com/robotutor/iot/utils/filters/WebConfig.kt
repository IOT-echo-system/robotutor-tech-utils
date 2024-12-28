package com.robotutor.iot.utils.filters

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping

@Configuration
class WebConfig {
    @Bean
    fun handlerMapping(): RequestMappingHandlerMapping {
        return RequestMappingHandlerMapping()
    }
}
