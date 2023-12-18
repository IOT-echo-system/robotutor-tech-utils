package com.shiviraj.iot.loggingstarter.utils

import com.fasterxml.jackson.core.JsonParseException
import com.shiviraj.iot.loggingstarter.serializer.DefaultSerializer
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Signal

fun <T> getAdditionalDetails(
    additionalDetails: Map<String, Any>,
    skipAdditionalDetails: Boolean,
    skipResponseBody: Boolean,
    signal: Signal<T>
): Map<String, Any> {
    val modifiedAdditionalDetails = additionalDetails.toMutableMap()

    if (skipAdditionalDetails) {
        modifiedAdditionalDetails.clear()
    }

    if (!skipResponseBody) {
        if (signal.hasValue())
            modifiedAdditionalDetails[LogConstants.RESPONSE_BODY] = getDeserializedResponseBody<T>(signal)
        else
            modifiedAdditionalDetails[LogConstants.RESPONSE_BODY] = "No response body found"
    }
    return modifiedAdditionalDetails.toMap()
}

fun getAdditionalDetails(
    additionalDetails: Map<String, Any>,
    skipAdditionalDetails: Boolean,
    throwable: Throwable?
): Map<String, Any> {
    val modifiedAdditionalDetails = additionalDetails.toMutableMap()
    if (skipAdditionalDetails) {
        modifiedAdditionalDetails.clear()
    }

    if (throwable is WebClientResponseException) {
        modifiedAdditionalDetails[LogConstants.RESPONSE_BODY] = errorResponseBodyFrom(throwable)
    }
    return modifiedAdditionalDetails.toMap()
}


private fun errorResponseBodyFrom(exception: WebClientResponseException): Any {
    val response = exception.responseBodyAsString
    return try {
        DefaultSerializer.deserialize(response, Map::class.java)
    } catch (e: Throwable) {
        response
    }
}


fun <T> getDeserializedResponseBody(signal: Signal<T>): Any {
    val data = signal.get()!!
    return if (data is String) {
        try {
            DefaultSerializer.deserialize(data, Map::class.java)
        } catch (e: JsonParseException) {
            data
        }
    } else {
        data
    }
}


class ThrowableWithTracingDetails(throwable: Throwable?, traceId: String?) : Throwable(
    message = throwable?.message + "{ traceId: $traceId" + "  }",
    cause = throwable
)

object LogConstants {
    const val API_CALL_START_TIME = "startTime"
    const val RESPONSE_BODY = "responseBody"
}
