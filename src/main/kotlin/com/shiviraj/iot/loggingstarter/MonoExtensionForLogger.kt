package com.shiviraj.iot.loggingstarter

import com.shiviraj.iot.loggingstarter.ReactiveContext.getTraceId
import com.shiviraj.iot.loggingstarter.details.LogDetails
import com.shiviraj.iot.loggingstarter.details.LogErrorDetails
import com.shiviraj.iot.loggingstarter.logger.Logger
import com.shiviraj.iot.loggingstarter.utils.ThrowableWithTracingDetails
import com.shiviraj.iot.loggingstarter.utils.getAdditionalDetails
import reactor.core.publisher.Mono
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

            val modifiedAdditionalDetails = getAdditionalDetails(additionalDetails, skipAdditionalDetails, throwable)

            val details = LogErrorDetails(
                errorCode = errorCode,
                errorMessage = errorMessage,
                additionalDetails = modifiedAdditionalDetails,
                searchableFields = searchableFields,
                traceId = getTraceId(signal.contextView),
            )

            val exception = ThrowableWithTracingDetails(throwable = throwable, traceId = "traceId")
            logger.error(details = details, exception = exception)
        }
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
            val modifiedAdditionalDetails = getAdditionalDetails<T>(
                additionalDetails,
                skipAdditionalDetails,
                skipResponseBody,
                signal
            )


            val logger = Logger(this::class.java)
            val logDetails = LogDetails(
                message = message,
                additionalDetails = modifiedAdditionalDetails,
                searchableFields = searchableFields,
                traceId = getTraceId(signal.contextView),
            )
            logger.info(details = logDetails)
        }
    }
}

