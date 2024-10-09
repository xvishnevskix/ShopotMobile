package org.videotrade.shopot.domain.repository

import kotlinx.coroutines.flow.StateFlow
import org.videotrade.shopot.presentation.components.Chat.Sticker
import org.videotrade.shopot.presentation.components.Chat.StickerPack

interface StickerRepository {
    suspend fun downloadStickerPacks(): List<StickerPack>?
    fun getStickerPacksState(): StateFlow<List<StickerPack>>
    fun clearData()

    // Новый метод для получения списка стикеров
    suspend fun getStickersForPack(packId: String): List<Sticker>?
}