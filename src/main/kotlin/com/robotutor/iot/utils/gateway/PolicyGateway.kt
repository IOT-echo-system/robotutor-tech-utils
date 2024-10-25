package com.robotutor.iot.utils.gateway

import com.robotutor.iot.utils.gateway.views.UserAccountPoliciesResponseData
import com.robotutor.iot.utils.models.UserAuthenticationData
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class PolicyGateway() {
    fun getPolicies(userAuthenticationData: UserAuthenticationData): Mono<UserAccountPoliciesResponseData> {
        return Mono.just(UserAccountPoliciesResponseData(policies = emptyList()))
    }
}
