package org.videotrade.shopot.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ContactDTO(
    val id: String? = "",
    val login: String? = "",
    val email: String? = "",
    val firstName: String? = "",
    val lastName: String? = "",
    val description: String? = "",
    val phone: String = "",
    val status: String? = "",
    val isRegistred: Boolean = false,
    val icon: String?,
    val notificationToken: String? = null
)
