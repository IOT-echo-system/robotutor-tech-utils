package com.robotutor.iot.utils.utils

import kotlin.reflect.full.declaredMemberProperties

fun <T : Any> T.toMap(): Map<String, Any?> {
    return this::class.declaredMemberProperties.associate { property ->
        val value = property.getter.call(this)
        property.name to if (value != null && value::class.isData) {
            value.toMap()
        } else {
            value
        }
    }
}
