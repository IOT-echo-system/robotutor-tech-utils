package com.robotutor.iot.utils.gateway.views


data class PoliciesResponseData(val policies: List<PolicyView>)

data class PolicyView(val name: String, val policyId: String)
