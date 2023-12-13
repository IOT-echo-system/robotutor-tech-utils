package com.shiviraj.iot.loggingstarter.serializer

object DefaultSerializer {
    fun serialize(obj: Any?): String {
        return ObjectMapperCache.objectMapper.writeValueAsString(obj)
    }

    fun <T> deserialize(str: String, type: Class<T>): T {
        return ObjectMapperCache.objectMapper.readValue(str, type)
    }
}
