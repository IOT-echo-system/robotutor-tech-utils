package com.robotutor.iot.utils.exceptions

import com.robotutor.iot.exceptions.ServiceError

enum class IOTError(override val errorCode: String, override val message: String) : ServiceError {
    IOT0101("IOT-0101", "Unauthorized user."),
    IOT0102("IOT-0102", "User doesn't have sufficient permission to perform this action."),
    IOT0103("IOT-0103", "Source doesn't belong to this premises."),
    IOT0104("IOT-0104", "Unable to resolve user data"),
    IOT0105("IOT-0105", "Unable to resolve premises data"),
    IOT0106("IOT-0106", "Premises source doesn't have permission to perform this action."),
}
