package org.videotrade.shopot.domain.repository

import kotlinx.coroutines.flow.StateFlow
import org.videotrade.shopot.presentation.components.Chat.FavoritePack
import org.videotrade.shopot.presentation.components.Chat.Sticker
import org.videotrade.shopot.presentation.components.Chat.StickerPack

interface StickerRepository {
    suspend fun downloadStickerPacks(): List<StickerPack>?
    fun clearData()

    // Новый метод для получения списка стикеров
    suspend fun getStickersForPack(packId: String): List<Sticker>?

    suspend fun getFavoritePacks(userId: String): List<FavoritePack>?

    suspend fun removePackFromFavorites(packId: String, userId: String): Boolean
}