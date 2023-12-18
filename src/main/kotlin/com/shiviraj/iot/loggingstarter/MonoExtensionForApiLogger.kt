package com.shiviraj.iot.loggingstarter

import com.shiviraj.iot.loggingstarter.ReactiveContext.createResponseDetails
import com.shiviraj.iot.loggingstarter.ReactiveContext.createResponseErrorDetails
import com.shiviraj.iot.loggingstarter.ReactiveContext.getTraceId
import com.shiviraj.iot.loggingstarter.logger.Logger
import com.shiviraj.iot.loggingstarter.utils.ThrowableWithTracingDetails
import com.shiviraj.iot.loggingstarter.utils.getAdditionalDetails
import reactor.core.publisher.Mono

fun <T> Mono<T>.logOnErrorResponse(
    errorCode: String? = null,
    errorMessage: String,
    additionalDetails: Map<String, Any> = emptyMap(),
    searchableFields: Map<String, Any?> = emptyMap(),
    skipAdditionalDetails: Boolean = false,
): Mono<T> {
    return doOnEach { signal ->
        if (signal.isOnError) {
            val logger = Logger(this::class.java)
            val throwable = signal.throwable

            val modifiedAdditionalDetails = getAdditionalDetails(additionalDetails, skipAdditionalDetails, throwable)

            val details = createResponseErrorDetails(
                context = signal.contextView,
                message = errorMessage,
                additionalDetails = modifiedAdditionalDetails,
                searchableFields = searchableFields,
                errorCode = errorCode
            )

            val exception = ThrowableWithTracingDetails(throwable = throwable, traceId = getTraceId(signal.contextView))
            logger.apiResponseError(details = details, exception = exception)
        }
    }
}

fun <T> Mono<T>.logOnSuccessResponse(
    message: String,
    additionalDetails: Map<String, Any> = emptyMap(),
    searchableFields: Map<String, Any?> = emptyMap(),
    skipAdditionalDetails: Boolean = false,
    skipResponseBody: Boolean = true,
): Mono<T> {
    return doOnEach { signal ->
        if (signal.isOnNext) {
            val modifiedAdditionalDetails = getAdditionalDetails<T>(
                additionalDetails,
                skipAdditionalDetails,
                skipResponseBody,
                signal
            )

            val logger = Logger(this::class.java)
            val responseDetails = createResponseDetails(
                message = message,
                context = signal.contextView,
                additionalDetails = modifiedAdditionalDetails,
                searchableFields = searchableFields
            )
            logger.apiResponseInfo(details = responseDetails)
        }
    }
}

