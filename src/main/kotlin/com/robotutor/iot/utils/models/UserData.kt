package com.robotutor.iot.utils.models

import com.robotutor.iot.utils.gateway.views.AuthenticationResponseData

interface IAuthenticationData

data class UserData(val userId: String, val roleId: String) : IAuthenticationData {
    companion object {
        fun from(authenticationResponseData: AuthenticationResponseData): UserData {
            return UserData(
                userId = authenticationResponseData.userId,
                roleId = authenticationResponseData.roleId,
            )
        }
    }
}

data class PremisesData(val premisesId: String) : IAuthenticationData {}

data class BoardData(val boardId: String, val roleId: String) : IAuthenticationData {
}
