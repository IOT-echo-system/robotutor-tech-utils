package com.shiviraj.iot.loggingstarter.serializer

import com.google.gson.Gson

object DefaultSerializer {
    fun serialize(obj: Any?): String {
        return Gson().toJson(obj)
    }

    fun <T> deserialize(str: String, type: Class<T>): T {
        return Gson().fromJson(str, type)
    }
}
