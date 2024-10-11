package org.videotrade.shopot.domain.usecase

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.videotrade.shopot.domain.model.FavoritePack
import org.videotrade.shopot.domain.model.StickerPack
import org.videotrade.shopot.domain.repository.StickerRepository

class StickerUseCase : KoinComponent {
    private val repository: StickerRepository by inject()

    suspend fun downloadStickerPacks(page: Int, size: Int): List<StickerPack>? {
        return repository.downloadStickerPacks(page, size)
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