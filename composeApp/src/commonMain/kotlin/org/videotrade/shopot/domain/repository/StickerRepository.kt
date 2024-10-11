package org.videotrade.shopot.domain.repository

import org.videotrade.shopot.domain.model.FavoritePack
import org.videotrade.shopot.domain.model.StickerPack


interface StickerRepository {
    suspend fun downloadStickerPacks(page: Int, size: Int): List<StickerPack>?

    suspend fun getFavoritePacks(): List<FavoritePack>?

    suspend fun getPack(packId:String): StickerPack?

    suspend fun addPackToFavorites(packId: String): Boolean

    suspend fun removePackFromFavorites(packId: String): Boolean

    fun clearData()
}