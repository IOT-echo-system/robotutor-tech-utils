package com.robotutor.loggingstarter

import org.springframework.http.HttpHeaders
import org.springframework.web.server.ServerWebExchange
import reactor.util.context.ContextView

internal object ReactiveContext {

    private const val TRACE_ID_HEADER_KEY = "x-trace-id"

    fun getTraceId(context: ContextView): String = getValueFromRequestHeader(context, TRACE_ID_HEADER_KEY)
        ?: "missing-trace-id"



    private fun getValueFromSubscriberContext(key: String, context: ContextView): String? {
        return if (context.hasKey(key)) context.get(key) else null
    }

    private fun getValueFromRequestHeader(
        context: ContextView,
        headerKey: String
    ): String? {
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
