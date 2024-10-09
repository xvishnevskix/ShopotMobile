package org.videotrade.shopot.data.remote.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.videotrade.shopot.data.origin
import org.videotrade.shopot.domain.repository.StickerRepository
import org.videotrade.shopot.presentation.components.Chat.FavoritePack
import org.videotrade.shopot.presentation.components.Chat.Sticker
import org.videotrade.shopot.presentation.components.Chat.StickerPack
import org.videotrade.shopot.presentation.components.Chat.StickerPackResponse

class StickerRepositoryImpl : StickerRepository {

    private val stickerPacks = MutableStateFlow<List<StickerPack>>(emptyList())

    override suspend fun downloadStickerPacks(): List<StickerPack>? {
        val originInstance = origin()
        println("Sticker get")

        val stickerPackResponse = originInstance.get<StickerPackResponse>("packs") ?: return null

        println("Sticker Packs Result: ${stickerPackResponse.content}")


        stickerPacks.value = stickerPackResponse.content

        return stickerPackResponse.content
    }



    override suspend fun getStickersForPack(packId: String): List<Sticker>? {
        val originInstance = origin()
        println("Fetching stickers for packId: $packId")

        // Используем originInstance.get для запроса стикеров
        val stickers = originInstance.get<List<Sticker>>("stickers/package/$packId/stickers") ?: return null
        println("Stickersssssssss: $stickers")

        return stickers
    }

    override suspend fun getFavoritePacks(userId: String): List<FavoritePack>? {
        val originInstance = origin()
        println("Fetching favorite packs for userId: $userId")

        // Используем originInstance.get для запроса избранных паков
        val favoritePacks = originInstance.get<List<FavoritePack>>("packs/$userId/favorites") ?: return null
        println("Favorite packs: $favoritePacks")

        return favoritePacks
    }

    override suspend fun removePackFromFavorites(packId: String, userId: String): Boolean {
        val originInstance = origin()
        println("Removing pack from favorites for userId: $userId and packId: $packId")

        val url = "packs/$packId/favorite"
        val headers = mapOf("X-User-Id" to userId)

        return originInstance.delete(url, headers)
    }

    override fun clearData() {
        stickerPacks.value = emptyList()
    }
}