package com.robotutor.loggingstarter.serializer

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.time.LocalDateTime

object ObjectMapperCache {
    val objectMapper: Gson = GsonBuilder()
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
        .create()
}
