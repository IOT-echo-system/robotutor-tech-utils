package com.robotutor.iot.utils.models

import com.robotutor.iot.utils.gateway.views.AuthenticationResponseData
import com.robotutor.iot.utils.gateway.views.PremisesResponseData
import com.robotutor.iot.utils.gateway.views.UserWithRole

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

data class PremisesData(val premisesId: String, val name: String, val user: UserWithRole) : IAuthenticationData {
    companion object {
        fun from(premisesResponseData: PremisesResponseData): PremisesData {
            return PremisesData(
                premisesId = premisesResponseData.premisesId,
                name = premisesResponseData.name,
                user = premisesResponseData.user
            )
        }
    }
}

data class BoardData(val boardId: String, val roleId: String) : IAuthenticationData {
}
