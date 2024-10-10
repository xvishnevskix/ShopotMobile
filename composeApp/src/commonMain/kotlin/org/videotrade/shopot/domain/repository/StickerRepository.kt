package org.videotrade.shopot.domain.repository

import kotlinx.coroutines.flow.StateFlow
import org.videotrade.shopot.presentation.components.Chat.FavoritePack
import org.videotrade.shopot.presentation.components.Chat.StickerPack

interface StickerRepository {
    suspend fun downloadStickerPacks(): List<StickerPack>?

    suspend fun getFavoritePacks(): List<FavoritePack>?

    suspend fun getPack(packId:String): StickerPack?

    suspend fun addPackToFavorites(packId: String): Boolean

    suspend fun removePackFromFavorites(packId: String): Boolean

    fun clearData()
}