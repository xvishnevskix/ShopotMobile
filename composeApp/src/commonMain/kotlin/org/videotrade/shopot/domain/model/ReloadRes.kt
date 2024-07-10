package org.videotrade.shopot.domain.model

import com.shepeliev.webrtckmp.SessionDescriptionType
import kotlinx.serialization.Serializable

@Serializable
data class ReloadRes(
    val accessToken: String,
    val refreshToken: String,
    val userId: String

)

