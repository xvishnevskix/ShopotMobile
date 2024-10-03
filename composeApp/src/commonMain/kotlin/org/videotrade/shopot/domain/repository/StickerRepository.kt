package org.videotrade.shopot.domain.repository

import kotlinx.coroutines.flow.StateFlow
import org.videotrade.shopot.presentation.components.Chat.StickerPack

interface StickerRepository {
    suspend fun downloadStickerPacks(): List<StickerPack>?
    fun getStickerPacksState(): StateFlow<List<StickerPack>>
    fun clearData()
}