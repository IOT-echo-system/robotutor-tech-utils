package com.robotutor.iot.utils.gateway

import com.robotutor.iot.service.WebClientWrapper
import com.robotutor.iot.utils.config.PremisesConfig
import com.robotutor.iot.utils.gateway.views.PremisesResponseData
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class PremisesGateway(private val webClient: WebClientWrapper, private val premisesConfig: PremisesConfig) {
    fun getPremises(premiseId: String): Mono<PremisesResponseData> {
        return webClient.get(
            baseUrl = premisesConfig.premisesServiceBaseUrl,
            path = premisesConfig.premisesPath,
            uriVariables = mapOf("premisesId" to premiseId),
            returnType = PremisesResponseData::class.java
        )
    }
}