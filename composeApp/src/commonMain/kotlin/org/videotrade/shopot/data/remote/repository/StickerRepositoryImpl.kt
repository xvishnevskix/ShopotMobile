package org.videotrade.shopot.data.remote.repository

import kotlinx.coroutines.flow.MutableStateFlow
import org.videotrade.shopot.data.origin
import org.videotrade.shopot.domain.model.FavoritePack
import org.videotrade.shopot.domain.model.StickerPack
import org.videotrade.shopot.domain.repository.StickerRepository

class StickerRepositoryImpl : StickerRepository {

    private val stickerPacks = MutableStateFlow<List<StickerPack>>(emptyList())

    override suspend fun downloadStickerPacks(): List<StickerPack>? {
        val originInstance = origin()
        println("Sticker get")


        val stickerPackList = originInstance.get<List<StickerPack>>("packs") ?: return null

        println("Sticker Packs Result: $stickerPackList")


        stickerPacks.value = stickerPackList

        return stickerPackList
    }


    override suspend fun getFavoritePacks(): List<FavoritePack>? {
        val originInstance = origin()
        println("Fetching favorite packs")

        val favoritePacks = originInstance.get<List<FavoritePack>>("packs/favorites") ?: return null
        println("Favorite packs: $favoritePacks")

        return favoritePacks
    }

    override suspend fun getPack(packId: String): StickerPack? {
        val originInstance = origin()
        println("Fetching pack with packId: $packId")

        val stickerPack = originInstance.get<StickerPack>("packs/$packId") ?: return null
        println("Fetched Sticker Pack: $stickerPack")

        return stickerPack
    }

    override suspend fun addPackToFavorites(packId: String): Boolean {
        return try {
            val originInstance = origin()
            val response = originInstance.post("packs/$packId/favorite", "")
            response?.let {
                println("Pack successfully added to favorites: $it")
                true
            } ?: run {
                println("Failed to add pack to favorites")
                false
            }
        } catch (e: Exception) {
            println("Error while adding pack to favorites: ${e.message}")
            false
        }
    }


    override suspend fun removePackFromFavorites(packId: String): Boolean {
        val originInstance = origin()
        println("Removing pack from favorites for packId: $packId")

        val url = "packs/$packId/favorite"

        return originInstance.delete(url)
    }

    override fun clearData() {
        stickerPacks.value = emptyList()
    }
}