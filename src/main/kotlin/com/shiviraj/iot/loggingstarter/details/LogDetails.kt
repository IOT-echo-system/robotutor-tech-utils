package com.shiviraj.iot.loggingstarter.details

data class LogDetails(
    val message: String,
    val traceId: String? = null,
    val additionalDetails: Map<String, Any> = emptyMap(),
    val searchableFields: Map<String, Any?> = emptyMap(),
)

data class LogErrorDetails(
    val errorCode: String? = null,
    val errorMessage: String,
    val traceId: String? = null,
    val additionalDetails: Map<String, Any> = emptyMap(),
    val searchableFields: Map<String, Any?> = emptyMap(),
)
