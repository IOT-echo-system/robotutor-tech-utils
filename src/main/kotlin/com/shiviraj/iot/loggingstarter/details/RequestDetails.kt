package com.shiviraj.iot.loggingstarter.details

import org.springframework.http.HttpMethod

class RequestDetails(
    val requestMethod: HttpMethod,
    val requestHeaders: Map<String, Any>? = null,
    var uriWithParams: String? = null,
    val requestBody: String? = null
)
