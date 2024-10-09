package org.videotrade.shopot.domain.usecase

import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.videotrade.shopot.domain.repository.StickerRepository
import org.videotrade.shopot.presentation.components.Chat.Sticker
import org.videotrade.shopot.presentation.components.Chat.StickerPack

class StickerUseCase : KoinComponent {
    private val repository: StickerRepository by inject()

    suspend fun downloadStickerPacks(): List<StickerPack>? {
        return repository.downloadStickerPacks()
    }

    fun getStickerPacksState(): StateFlow<List<StickerPack>> {
        return repository.getStickerPacksState()
    }

    // Новый метод для получения списка стикеров
    suspend fun getStickersForPack(packId: String): List<Sticker>? {
        return repository.getStickersForPack(packId)
    }
}