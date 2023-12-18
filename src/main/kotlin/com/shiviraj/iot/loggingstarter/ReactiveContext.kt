package com.shiviraj.iot.loggingstarter

import com.shiviraj.iot.loggingstarter.details.ResponseDetails
import com.shiviraj.iot.loggingstarter.details.ResponseErrorDetails
import com.shiviraj.iot.loggingstarter.utils.LogConstants
import org.springframework.http.HttpHeaders
import org.springframework.web.server.ServerWebExchange
import reactor.util.context.ContextView
import java.time.LocalDateTime
import java.time.ZoneOffset

internal object ReactiveContext {
    private const val TRACE_ID_HEADER_KEY = "x-trace-id"

    fun getTraceId(context: ContextView): String {
        return getValueFromRequestHeader(context, TRACE_ID_HEADER_KEY) ?: "missing-trace-id"
    }


    fun createResponseDetails(
        context: ContextView, message: String,
        additionalDetails: Map<String, Any> = emptyMap(),
        searchableFields: Map<String, Any?> = emptyMap(),
    ): ResponseDetails {
        if (context.hasKey(ServerWebExchange::class.java)) {
            val exchange = context.get(ServerWebExchange::class.java)
            return ResponseDetails(
                message = message,
                requestMethod = exchange.request.method,
                requestHeaders = exchange.request.headers,
                requestBody = exchange.request.body.toString(),
                uriWithParams = exchange.request.uri.toString(),
                responseHeaders = exchange.response.headers,
                responseStatusCode = exchange.response.statusCode?.value(),
                responseBody = exchange.response.bufferFactory().toString(),
                responseTime = getResponseTime(context),
                traceId = getTraceId(context),
                additionalDetails = additionalDetails,
                searchableFields = searchableFields
            )
        }
        return ResponseDetails(
            message = message,
            additionalDetails = additionalDetails,
            searchableFields = searchableFields,
            traceId = getTraceId(context),
            responseTime = getResponseTime(context),
        )
    }

    fun createResponseErrorDetails(
        errorCode: String?,
        context: ContextView,
        message: String,
        additionalDetails: Map<String, Any> = emptyMap(),
        searchableFields: Map<String, Any?> = emptyMap(),
    ): ResponseErrorDetails {
        if (context.hasKey(ServerWebExchange::class.java)) {
            val exchange = context.get(ServerWebExchange::class.java)
            return ResponseErrorDetails(
                errorCode = errorCode,
                errorMessage = message,
                requestMethod = exchange.request.method,
                requestHeaders = exchange.request.headers,
                requestBody = exchange.request.body.toString(),
                uriWithParams = exchange.request.uri.toString(),
                responseHeaders = exchange.response.headers,
                responseStatusCode = exchange.response.statusCode?.value(),
                responseBody = exchange.response.bufferFactory().toString(),
                responseTime = getResponseTime(context),
                traceId = getTraceId(context),
                additionalDetails = additionalDetails,
                searchableFields = searchableFields
            )
        }
        return ResponseErrorDetails(
            errorCode = errorCode,
            errorMessage = message,
            additionalDetails = additionalDetails,
            searchableFields = searchableFields,
            traceId = getTraceId(context),
            responseTime = getResponseTime(context),
        )
    }

    private fun getValueFromRequestHeader(context: ContextView, headerKey: String): String? {
        return when {
            context.isEmpty -> "EMPTY_CONTEXT"
            context.hasKey(ServerWebExchange::class.java) -> {
                val valueFromIncomingRequest =
                    context.get(ServerWebExchange::class.java).request.headers[headerKey]?.first()
                if (valueFromIncomingRequest == null && context.hasKey("headers")) {
                    return context.get<HttpHeaders>("headers")[headerKey]?.firstOrNull()
                } else {
                    valueFromIncomingRequest
                }
            }

            context.hasKey("headers") -> context.get<HttpHeaders>("headers")[headerKey]?.firstOrNull()
            else -> null
        }
    }
}

fun getResponseTime(context: ContextView): Long {
    return context.getOrEmpty<LocalDateTime>(LogConstants.API_CALL_START_TIME)
        .map { (LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) - it.toEpochSecond(ZoneOffset.UTC)) * 1000 }
        .orElseGet { -1 }
}
