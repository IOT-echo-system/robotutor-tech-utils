package com.robotutor.iot.utils.models

import com.robotutor.iot.utils.gateway.views.UserAuthenticationResponseData

data class UserBoardAuthenticationData(val boardId: String, val accountId: String) : IAuthenticationData {
    companion object {
        fun from(userAuthenticationResponseData: UserAuthenticationResponseData): UserAuthenticationData {
            return UserAuthenticationData(
                userId = userAuthenticationResponseData.userId,
                accountId = userAuthenticationResponseData.projectId,
                roleId = userAuthenticationResponseData.roleId,
                boardId = userAuthenticationResponseData.boardId
            )
        }
    }
}
