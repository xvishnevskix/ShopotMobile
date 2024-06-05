package org.videotrade.shopot.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ProfileDTO(
    val id: String = "",
    val login: String? = "",
    val email: String? = "",
    val firstName: String = "",
    val lastName: String = "",
    val description: String? = "",
    val status: String? = "",
    val phone: String = "",
    val icon: String? = "",
)




