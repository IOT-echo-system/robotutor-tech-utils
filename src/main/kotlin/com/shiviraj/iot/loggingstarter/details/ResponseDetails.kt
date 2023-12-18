package com.shiviraj.iot.loggingstarter.details

import org.springframework.http.HttpMethod

data class ResponseDetails(
    val message: String,
    val requestMethod: HttpMethod? = null,
    val requestHeaders: Map<String, Any>? = null,
    val requestBody: String? = null,
    val uriWithParams: String? = null,
    val responseStatusCode: Int? = null,
    val responseHeaders: Map<String, Any>? = null,
    val responseTime: Long = -1,
    val responseBody: String? = null,
    val traceId: String? = null,
    val additionalDetails: Map<String, Any> = emptyMap(),
    val searchableFields: Map<String, Any?> = emptyMap(),
)


data class ResponseErrorDetails(
    val errorCode: String? = null,
    val errorMessage: String,
    val requestMethod: HttpMethod? = null,
    val requestHeaders: Map<String, Any>? = null,
    val requestBody: String? = null,
    val uriWithParams: String? = null,
    val responseHeaders: Map<String, Any>? = null,
    val responseStatusCode: Int? = null,
    val responseTime: Long = -1,
    val responseBody: String? = null,
    val traceId: String? = null,
    val additionalDetails: Map<String, Any> = emptyMap(),
    val searchableFields: Map<String, Any?> = emptyMap(),
)
