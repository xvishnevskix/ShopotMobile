package org.videotrade.shopot.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class GroupUserDTO(
    val firstName: String,
    val lastName: String,
    val phone: String,
)
