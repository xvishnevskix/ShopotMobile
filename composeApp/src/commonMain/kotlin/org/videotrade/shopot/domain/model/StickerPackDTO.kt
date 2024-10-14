package org.videotrade.shopot.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class StickerPack(
    val name: String,
    val packId: String,
    val favorite: Boolean,
    val fileIds: List<String?>
)

@Serializable
data class FavoritePack(
    val id: String,
    val userId: String,
    val packId: String
)