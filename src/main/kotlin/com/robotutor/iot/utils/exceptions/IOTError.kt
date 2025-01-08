package com.robotutor.iot.utils.exceptions

import com.robotutor.iot.exceptions.ServiceError

enum class IOTError(override val errorCode: String, override val message: String) : ServiceError {
    IOT0101("IOT-0101", "Unauthorized user."),
    IOT0102("IOT-0102", "BoardId is not valid."),
    IOT0103("IOT-0103", "User doesn't have sufficient permission to perform this action."),
    IOT0104("IOT-0104", "User doesn't belong to this premises."),
}
