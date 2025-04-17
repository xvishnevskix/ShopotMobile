package org.videotrade.shopot.domain.model

import kotlinx.serialization.Serializable


@Serializable
data class GroupInfo(
    val groupUsers: List<GroupUserDTO>,
    val senderRole: GroupUserRole,
)

@Serializable
data class GroupUserDTO(
    val id: String,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val icon: String? = null,
    val role: GroupUserRole,
)

enum class GroupUserRole {
    MEMBER,
    OWNER,
    ADMIN
}

