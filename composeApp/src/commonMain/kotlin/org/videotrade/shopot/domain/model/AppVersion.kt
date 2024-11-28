package org.videotrade.shopot.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AppVersion(
    val id: String,
    val appVersion: String,
    val criticalAppVersion: String,
    val appSize: String,
    val description: String,

)