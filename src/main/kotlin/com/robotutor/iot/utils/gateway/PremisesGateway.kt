package com.robotutor.iot.utils.gateway

import com.robotutor.iot.service.CacheService
import com.robotutor.iot.service.WebClientWrapper
import com.robotutor.iot.utils.config.PremisesConfig
import com.robotutor.iot.utils.gateway.views.PremisesResponseData
import com.robotutor.iot.utils.models.PremisesData
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class PremisesGateway(
    private val webClient: WebClientWrapper,
    private val premisesConfig: PremisesConfig,
    private val cacheService: CacheService
) {
    fun getPremises(premiseId: String, traceId: String): Mono<PremisesData> {
        return cacheService.retrieve("premisesGateway::$traceId") {
            webClient.get(
                baseUrl = premisesConfig.premisesServiceBaseUrl,
                path = premisesConfig.premisesPath,
                uriVariables = mapOf("premisesId" to premiseId),
                returnType = PremisesResponseData::class.java
            )
                .map { PremisesData.from(it) }
        }
    }
}
