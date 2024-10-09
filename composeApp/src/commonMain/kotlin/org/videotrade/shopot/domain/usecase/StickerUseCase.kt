package org.videotrade.shopot.domain.usecase

import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.videotrade.shopot.domain.repository.StickerRepository
import org.videotrade.shopot.presentation.components.Chat.FavoritePack
import org.videotrade.shopot.presentation.components.Chat.Sticker
import org.videotrade.shopot.presentation.components.Chat.StickerPack

class StickerUseCase : KoinComponent {
    private val repository: StickerRepository by inject()

    suspend fun downloadStickerPacks(): List<StickerPack>? {
        return repository.downloadStickerPacks()
    }


    suspend fun getStickersForPack(packId: String): List<Sticker>? {
        return repository.getStickersForPack(packId)
    }

    suspend fun getFavoritePacks(userId: String): List<FavoritePack>? {
        return repository.getFavoritePacks(userId) // Предполагается, что Repository реализован для получения избранных паков
    }

    suspend fun removePackFromFavorites(packId: String, userId: String): Boolean {
        return repository.removePackFromFavorites(packId, userId)
    }
}