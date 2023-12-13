package com.shiviraj.iot.loggingstarter.logger

import com.shiviraj.iot.loggingstarter.serializer.DefaultSerializer.serialize
import com.shiviraj.iot.loggingstarter.details.LogDetails
import org.slf4j.LoggerFactory

class Logger(className: Class<out Any>) {
    private val logger = LoggerFactory.getLogger(className)

    fun info(details: LogDetails) {
        logger.info(serialize(details))
    }

    fun error(details: LogDetails, exception: Throwable) {
        logger.error(serialize(details), exception)
    }
}
