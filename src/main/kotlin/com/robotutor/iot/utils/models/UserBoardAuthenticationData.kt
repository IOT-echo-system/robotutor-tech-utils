package com.robotutor.iot.utils.models

import com.robotutor.iot.utils.gateway.views.AuthenticationResponseData

data class UserBoardAuthenticationData(val boardId: String, val accountId: String) : IAuthenticationData {
    companion object {
        fun from(authenticationResponseData: AuthenticationResponseData): UserAuthenticationData {
            return UserAuthenticationData(
                userId = authenticationResponseData.userId,
                accountId = authenticationResponseData.projectId,
                roleId = authenticationResponseData.roleId,
                boardId = authenticationResponseData.boardId
            )
        }
    }
}
