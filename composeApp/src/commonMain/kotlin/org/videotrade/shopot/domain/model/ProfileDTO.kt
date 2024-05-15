package org.videotrade.shopot.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ProfileDTO(
    val id: String = "",
    val phone: String = "",
    val discription: String = "",
    val first_name: String = "",
    val last_name: String = "",
    val notificationToken: String = "",
    val status: Int = 0,
    val icon: String? = null,
    val messages_count: Int = 0,
    val login: String = "",
    val secret_key: String? = null,
    val vox: String? = null,
    val vox_pwd: String? = null
)


@Serializable
data class UserProfile(
    
    val message: ProfileDTO,
    
    )
    
    