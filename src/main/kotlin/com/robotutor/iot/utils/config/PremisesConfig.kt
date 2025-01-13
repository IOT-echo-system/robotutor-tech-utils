package com.robotutor.iot.utils.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.utils")
data class PremisesConfig(
    val premisesServiceBaseUrl: String = "",
    val premisesPath: String = "/premises/{premisesId}",
    val validatedPremisesPaths: List<String> = emptyList(),
)
