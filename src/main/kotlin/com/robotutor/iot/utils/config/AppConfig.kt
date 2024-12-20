package com.robotutor.iot.utils.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.utils")
data class AppConfig(
    val authServiceBaseUrl: String,
    val internalAccessToken: String,
    val openApiEndpoints: List<String> = emptyList(),
    val validatePath: String = "/validate",
    val getPoliciesPath: String = "/roles/{roleId}/policies"
)
