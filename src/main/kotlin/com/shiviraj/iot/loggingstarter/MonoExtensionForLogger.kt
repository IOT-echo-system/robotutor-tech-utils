package com.shiviraj.iot.loggingstarter

import com.fasterxml.jackson.core.JsonParseException
import com.shiviraj.iot.loggingstarter.details.LogDetails
import com.shiviraj.iot.loggingstarter.logger.Logger
import com.shiviraj.iot.loggingstarter.serializer.DefaultSerializer
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import reactor.core.publisher.Signal
import reactor.core.publisher.SignalType

fun <T> Mono<T>.logOnError(
    errorCode: String? = null,
    errorMessage: String,
    additionalDetails: Map<String, Any> = emptyMap(),
    searchableFields: Map<String, Any?> = emptyMap(),
    skipAdditionalDetails: Boolean = false,
): Mono<T> {
    return doOnEach { signal ->
        if (SignalType.ON_ERROR == signal.type) {
            val logger = Logger(this::class.java)
            val throwable = signal.throwable

            val modifiedAdditionalDetails = additionalDetails.toMutableMap()
            if (skipAdditionalDetails) {
                modifiedAdditionalDetails.clear()
            }

            if (throwable is WebClientResponseException) {
                modifiedAdditionalDetails[LogConstants.RESPONSE_BODY] = errorResponseBodyFrom(throwable)
            }

            val details = LogDetails(
                errorCode = errorCode,
                message = errorMessage,
                additionalDetails = modifiedAdditionalDetails.toMap(),
                searchableFields = searchableFields,
            )

            val exception = ThrowableWithTracingDetails(throwable = throwable, traceId = "traceId")
            logger.error(details = details, exception = exception)
        }
    }
}

private fun errorResponseBodyFrom(exception: WebClientResponseException): Any {
    val response = exception.responseBodyAsString
    return try {
        DefaultSerializer.deserialize(response, Map::class.java)
    } catch (e: Throwable) {
        response
    }
}

fun <T> Mono<T>.logOnSuccess(
    message: String,
    additionalDetails: Map<String, Any> = emptyMap(),
    searchableFields: Map<String, Any?> = emptyMap(),
    skipAdditionalDetails: Boolean = false,
    skipResponseBody: Boolean = true,
): Mono<T> {
    return doOnEach { signal ->
        if (SignalType.ON_NEXT == signal.type) {
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

            val logger = Logger(this::class.java)
            val logDetails = LogDetails(
                message = message,
                additionalDetails = modifiedAdditionalDetails.toMap(),
                searchableFields = searchableFields,
            )
            logger.info(details = logDetails)
        }
    }
}


private fun <T> getDeserializedResponseBody(signal: Signal<T>): Any {
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

object LogConstants {
    const val RESPONSE_BODY = "responseBody"
}

class ThrowableWithTracingDetails(throwable: Throwable?, traceId: String?) : Throwable(
    message = throwable?.message + "{ traceId: $traceId" + "  }",
    cause = throwable
)
