package com.robotutor.iot.utils.gateway.views

data class AuthenticationResponseData(
    val userId: String,
    val projectId: String,
    val roleId: String,
    val boardId: String?
)
