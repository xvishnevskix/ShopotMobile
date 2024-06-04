package org.videotrade.shopot.domain.model

import kotlinx.serialization.Serializable


@Serializable
data class FileDTO(
    val id: String,
    val type: String,
    val name: String,
    val url: String,
    val size: Int ,
    val originalFilePath: String,
)

