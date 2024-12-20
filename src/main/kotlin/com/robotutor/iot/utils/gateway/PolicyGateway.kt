package com.robotutor.iot.utils.gateway

import com.robotutor.iot.service.WebClientWrapper
import com.robotutor.iot.utils.config.AppConfig
import com.robotutor.iot.utils.gateway.views.PolicyView
import com.robotutor.loggingstarter.logOnError
import com.robotutor.loggingstarter.logOnSuccess
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

@Component
class PolicyGateway(private val webClientWrapper: WebClientWrapper, private val appConfig: AppConfig) {
    fun getPolicies(roleId: String): Flux<PolicyView> {
        return webClientWrapper.getFlux(
            baseUrl = appConfig.authServiceBaseUrl,
            path = appConfig.getPoliciesPath,
            returnType = PolicyView::class.java,
            uriVariables = mapOf("roleId" to roleId)
        )
            .logOnSuccess("Successfully get policies by roleId: $roleId")
            .logOnError("", "Failed to get policies by roleId: $roleId")
    }
}
