package com.shiviraj.iot.loggingstarter.logger

import com.shiviraj.iot.loggingstarter.details.*
import com.shiviraj.iot.loggingstarter.serializer.DefaultSerializer.serialize
import org.slf4j.LoggerFactory

class Logger(className: Class<out Any>) {
    private val logger = LoggerFactory.getLogger(className)

    fun info(details: LogDetails) {
        logger.info(serialize(details))
    }

    fun error(details: LogErrorDetails, exception: Throwable) {
        logger.error(serialize(details), exception)
    }

    fun apiRequestInfo(details: RequestDetails) {
        logger.info(serialize(details))
    }

    fun apiResponseInfo(details: ResponseDetails) {
        logger.info(serialize(details))
    }

    fun apiResponseError(details: ResponseErrorDetails, exception: Throwable) {
        logger.error(serialize(details), exception)
    }
}
