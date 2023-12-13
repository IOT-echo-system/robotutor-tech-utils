package com.shiviraj.iot.loggingstarter.details

class ResponseDetails(
    val responseCode: String? = null,
    val responseHeaders: Map<String, Any>? = null,
    val responseStatus: String = "",
    val responseTime: Long = -1,
    val responseBody: String? = null
)
