package com.robotutor.loggingstarter.serializer

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocalDateTimeAdapter : TypeAdapter<LocalDateTime>() {
    private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @Throws(IOException::class)
    override fun write(out: JsonWriter, value: LocalDateTime?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(formatter.format(value))
        }
    }

    @Throws(IOException::class)
    override fun read(jsonReader: JsonReader): LocalDateTime? {
        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull()
            return null
        }
        val dateString = jsonReader.nextString()
        return LocalDateTime.parse(dateString, formatter)
    }
}
