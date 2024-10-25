package com.robotutor.iot.utils.gateway.views


data class UserAccountPoliciesResponseData(val policies: List<PolicyView>)

data class PolicyView(val name: String, val policyId: String)
