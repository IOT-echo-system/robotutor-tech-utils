package com.robotutor.iot.utils.gateway

import com.robotutor.iot.service.CacheService
import com.robotutor.iot.service.WebClientWrapper
import com.robotutor.iot.utils.config.PremisesConfig
import com.robotutor.iot.utils.filters.getPremisesId
import com.robotutor.iot.utils.filters.getTraceId
import com.robotutor.iot.utils.gateway.views.PremisesResponseData
import com.robotutor.iot.utils.models.PremisesData
import com.robotutor.loggingstarter.Logger
import com.robotutor.loggingstarter.logOnError
import com.robotutor.loggingstarter.logOnSuccess
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class PremisesGateway(
    private val webClient: WebClientWrapper,
    private val premisesConfig: PremisesConfig,
    private val cacheService: CacheService
) {
    val logger = Logger(this::class.java)
    fun getPremises(exchange: ServerWebExchange): Mono<PremisesData> {
        val traceId = getTraceId(exchange)
        val premisesId = getPremisesId(exchange)
        return cacheService.retrieve("premisesGateway::$traceId", PremisesData::class.java, 60) {
            webClient.get(
                baseUrl = premisesConfig.premisesServiceBaseUrl,
                path = premisesConfig.premisesPath,
                uriVariables = mapOf("premisesId" to premisesId),
                returnType = PremisesResponseData::class.java,
            )
                .map { PremisesData.from(it) }
        }
            .logOnSuccess(logger, "Successfully get premises for $traceId")
            .logOnError(logger, "", "Failed to get premises for $traceId")
    }
}
