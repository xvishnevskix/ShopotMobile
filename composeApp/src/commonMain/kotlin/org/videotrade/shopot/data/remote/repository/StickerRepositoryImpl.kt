package org.videotrade.shopot.data.remote.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.videotrade.shopot.data.origin
import org.videotrade.shopot.domain.repository.StickerRepository
import org.videotrade.shopot.presentation.components.Chat.Sticker
import org.videotrade.shopot.presentation.components.Chat.StickerPack
import org.videotrade.shopot.presentation.components.Chat.StickerPackResponse

class StickerRepositoryImpl : StickerRepository {

    private val stickerPacks = MutableStateFlow<List<StickerPack>>(emptyList())

    override suspend fun downloadStickerPacks(): List<StickerPack>? {
        val originInstance = origin()
        println("Sticker get")

        // Изменение: получение объекта StickerPackResponse вместо List<StickerPack>
        val stickerPackResponse = originInstance.get<StickerPackResponse>("packs") ?: return null

        println("Sticker Packs Result: ${stickerPackResponse.content}")

        // Извлечение стикерпаков из поля content
        stickerPacks.value = stickerPackResponse.content

        return stickerPackResponse.content
    }

    override fun getStickerPacksState(): StateFlow<List<StickerPack>> = stickerPacks.asStateFlow()

    override fun clearData() {
        stickerPacks.value = emptyList()
    }

    // Метод для получения списка стикеров для указанного пака
    override suspend fun getStickersForPack(packId: String): List<Sticker>? {
        val originInstance = origin()
        println("Fetching stickers for packId: $packId")

        // Используем originInstance.get для запроса стикеров
        val stickers = originInstance.get<List<Sticker>>("stickers/package/$packId/stickers") ?: return null
        println("Stickersssssssss: $stickers")

        return stickers
    }
}