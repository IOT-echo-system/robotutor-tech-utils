package com.shiviraj.iot.loggingstarter.details

import com.shiviraj.iot.loggingstarter.ReactiveContext.getTraceId
import com.shiviraj.iot.loggingstarter.serializer.DefaultSerializer.serialize
import org.springframework.http.HttpMethod
import reactor.util.context.ContextView

data class RequestDetails(
    val message: String,
    val requestMethod: HttpMethod,
    val requestHeaders: Map<String, Any>? = null,
    val uriWithParams: String? = null,
    val requestBody: String? = null,
    val traceId: String? = null,
) {
    companion object {
        fun from(
            requestMethod: HttpMethod,
            requestHeaders: Map<String, Any>? = null,
            uriWithParams: String? = null,
            requestBody: Any? = null,
            context: ContextView
        ): RequestDetails {
            return RequestDetails(
                message = "API_REQUEST",
                requestMethod = requestMethod,
                requestHeaders = requestHeaders,
                uriWithParams = uriWithParams,
                requestBody = serialize(requestBody),
                traceId = getTraceId(context)
            )
        }
    }
}
