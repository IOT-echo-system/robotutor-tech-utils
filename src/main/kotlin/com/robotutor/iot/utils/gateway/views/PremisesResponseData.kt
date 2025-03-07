package com.robotutor.iot.utils.gateway.views

data class PremisesResponseData(
    val premisesId: String,
    val name: String,
    val agent: AgentWithRole
)

data class AgentWithRole(val agentId: String, val role: PremisesRole)

enum class PremisesRole(val rank: Int) {
    OWNER(1),
    ADMIN(2),
    USER(3),
    BOARD(4),
    VOICE(5),
}
