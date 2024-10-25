package com.robotutor.iot.utils.gateway.views

data class UserAuthenticationResponseData(
    val userId: String,
    val projectId: String,
    val roleId: String,
    val boardId: String?
)
