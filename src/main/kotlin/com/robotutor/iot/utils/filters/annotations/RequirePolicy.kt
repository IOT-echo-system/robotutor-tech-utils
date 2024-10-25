package com.robotutor.iot.utils.filters.annotations

import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FUNCTION

@Target(FUNCTION)
@Retention(RUNTIME)
annotation class RequirePolicy(val policyName: String)

