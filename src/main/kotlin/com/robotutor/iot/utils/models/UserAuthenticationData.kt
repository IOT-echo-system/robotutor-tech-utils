package com.robotutor.iot.utils.models

import com.robotutor.iot.utils.gateway.views.AuthenticationResponseData

interface IAuthenticationData

data class UserAuthenticationData(val userId: String, val accountId: String, val roleId: String, val boardId: String?) :
    IAuthenticationData {
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

data class BoardAuthenticationData(val accountId: String, val roleId: String, val boardId: String) :
    IAuthenticationData {
    companion object {
        fun from(userAuthenticationData: UserAuthenticationData): BoardAuthenticationData {
            return BoardAuthenticationData(
                accountId = userAuthenticationData.accountId,
                roleId = userAuthenticationData.roleId,
                boardId = userAuthenticationData.boardId!!
            )
        }
    }
}
