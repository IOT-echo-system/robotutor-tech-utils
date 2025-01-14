package com.robotutor.iot.utils.gateway

import com.robotutor.iot.service.CacheService
import com.robotutor.iot.service.WebClientWrapper
import com.robotutor.iot.utils.config.AppConfig
import com.robotutor.iot.utils.filters.getTraceId
import com.robotutor.iot.utils.gateway.views.PolicyView
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Flux

@Component
class PolicyGateway(
    private val webClientWrapper: WebClientWrapper,
    private val appConfig: AppConfig,
    private val cacheService: CacheService
) {
    fun getPolicies(exchange: ServerWebExchange): Flux<PolicyView> {
        val traceId = getTraceId(exchange)
        return cacheService.retrieves("policyGateway::$traceId", PolicyView::class.java, 60) {
            webClientWrapper.getFlux(
                baseUrl = appConfig.authServiceBaseUrl,
                path = appConfig.getPoliciesPath,
                returnType = PolicyView::class.java,
            )
        }
    }
}
