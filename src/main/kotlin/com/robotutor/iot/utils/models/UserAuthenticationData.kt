package com.robotutor.iot.utils.models

import com.robotutor.iot.utils.gateway.views.UserAuthenticationResponseData

interface IAuthenticationData

data class UserAuthenticationData(val userId: String, val accountId: String, val roleId: String, val boardId: String?) :
    IAuthenticationData {
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
