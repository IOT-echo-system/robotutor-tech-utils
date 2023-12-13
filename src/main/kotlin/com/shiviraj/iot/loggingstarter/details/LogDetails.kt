package com.shiviraj.iot.loggingstarter.details

import org.springframework.http.HttpMethod

data class LogDetails(
    val errorCode: String? = null,
    val message: String,
    val requestMethod: HttpMethod? = null,
    val requestHeaders: Map<String, Any>? = null,
    val requestBody: String? = null,
    val uriWithParams: String? = null,
    val responseCode: String? = null,
    val responseHeaders: Map<String, Any>? = null,
    val responseStatus: String? = null,
    val responseTime: Long = -1,
    val responseBody: String? = null,
    val traceId: String? = null,
    val additionalDetails: Map<String, Any> = emptyMap(),
    val searchableFields: Map<String, Any?> = emptyMap(),
) {
    companion object {
        fun create(
            message: String,
            errorCode: String? = null,
            requestDetails: RequestDetails? = null,
            responseDetails: ResponseDetails? = null,
            traceId: String? = null,
            searchableFields: Map<String, Any> = emptyMap(),
        ): LogDetails {
            return LogDetails(
                message = message,
                errorCode = errorCode,
                requestMethod = requestDetails?.requestMethod,
                requestHeaders = requestDetails?.requestHeaders,
                uriWithParams = requestDetails?.uriWithParams,
                requestBody = requestDetails?.requestBody,
                responseCode = responseDetails?.responseCode,
                responseStatus = responseDetails?.responseStatus,
                responseTime = responseDetails?.responseTime ?: -1,
                responseHeaders = responseDetails?.responseHeaders,
                responseBody = responseDetails?.responseBody,
                traceId = traceId,
                searchableFields = searchableFields,
            )
        }
    }
}
