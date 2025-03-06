package com.robotutor.iot.utils.gateway.views

data class PremisesResponseData(
    val premisesId: String,
    val name: String,
    val user: UserWithRole
)

data class UserWithRole(val userId: String, val role: PremisesRole)

enum class PremisesRole(val rank: Int) {
    OWNER(1),
    ADMIN(2),
    USER(3),
}
