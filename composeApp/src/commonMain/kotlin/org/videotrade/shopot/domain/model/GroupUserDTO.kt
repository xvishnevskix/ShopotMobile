package org.videotrade.shopot.domain.model

import androidx.compose.ui.input.pointer.PointerIcon
import kotlinx.serialization.Serializable

@Serializable
data class GroupUserDTO(
    val firstName: String,
    val lastName: String,
    val phone: String,
    val icon: String? = null,
    val role: String
)
