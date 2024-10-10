package org.videotrade.shopot.domain.usecase

import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.videotrade.shopot.domain.repository.StickerRepository
import org.videotrade.shopot.presentation.components.Chat.FavoritePack
import org.videotrade.shopot.presentation.components.Chat.StickerPack

class StickerUseCase : KoinComponent {
    private val repository: StickerRepository by inject()

    suspend fun downloadStickerPacks(): List<StickerPack>? {
        return repository.downloadStickerPacks()
    }

    suspend fun getFavoritePacks(): List<FavoritePack>? {
        return repository.getFavoritePacks()
    }

    suspend fun getPack(packId: String): StickerPack? {
        return repository.getPack(packId)
    }

    suspend fun addPackToFavorites(packId: String): Boolean {
        return repository.addPackToFavorites(packId)
    }

    suspend fun removePackFromFavorites(packId: String): Boolean {
        return repository.removePackFromFavorites(packId)
    }
}