package com.robotutor.iot.utils.gateway

import com.robotutor.iot.service.CacheService
import com.robotutor.iot.service.WebClientWrapper
import com.robotutor.iot.utils.config.PremisesConfig
import com.robotutor.iot.utils.filters.PREMISES_ID_HEADER_KEY
import com.robotutor.iot.utils.filters.getPremisesId
import com.robotutor.iot.utils.filters.getTraceId
import com.robotutor.iot.utils.gateway.views.PremisesResponseData
import com.robotutor.iot.utils.models.PremisesData
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class PremisesGateway(
    private val webClient: WebClientWrapper,
    private val premisesConfig: PremisesConfig,
    private val cacheService: CacheService
) {
    fun getPremises(premiseId: String, exchange: ServerWebExchange): Mono<PremisesData> {
        val traceId = getTraceId(exchange)
        val token = exchange.request.headers[HttpHeaders.AUTHORIZATION]?.get(0) ?: ""
        val headers = mapOf("token" to token, PREMISES_ID_HEADER_KEY to getPremisesId(exchange))
        return cacheService.retrieve("premisesGateway::$traceId", PremisesData::class.java, 60) {
            webClient.get(
                baseUrl = premisesConfig.premisesServiceBaseUrl,
                path = premisesConfig.premisesPath,
                uriVariables = mapOf("premisesId" to premiseId),
                returnType = PremisesResponseData::class.java,
                headers = headers
            )
                .map { PremisesData.from(it) }
        }
    }
}
