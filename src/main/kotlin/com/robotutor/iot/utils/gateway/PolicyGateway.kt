package com.robotutor.iot.utils.gateway

import com.robotutor.iot.service.CacheService
import com.robotutor.iot.service.WebClientWrapper
import com.robotutor.iot.utils.config.AppConfig
import com.robotutor.iot.utils.gateway.views.PolicyView
import com.robotutor.loggingstarter.logOnError
import com.robotutor.loggingstarter.logOnSuccess
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

@Component
class PolicyGateway(
    private val webClientWrapper: WebClientWrapper,
    private val appConfig: AppConfig,
    private val cacheService: CacheService
) {
    fun getPolicies(traceId: String): Flux<PolicyView> {
        return cacheService.retrieves("policyGateway::$traceId", PolicyView::class.java, 60) {
            webClientWrapper.getFlux(
                baseUrl = appConfig.authServiceBaseUrl,
                path = appConfig.getPoliciesPath,
                returnType = PolicyView::class.java,
            )
                .logOnSuccess("Successfully get policies")
                .logOnError("", "Failed to get policies")
        }
    }
}
