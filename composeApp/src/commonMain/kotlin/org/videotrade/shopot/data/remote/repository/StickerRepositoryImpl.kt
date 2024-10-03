package org.videotrade.shopot.data.remote.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.videotrade.shopot.data.origin
import org.videotrade.shopot.domain.repository.StickerRepository
import org.videotrade.shopot.presentation.components.Chat.StickerPack

class StickerRepositoryImpl : StickerRepository {

    private val stickerPacks = MutableStateFlow<List<StickerPack>>(emptyList())

    override suspend fun downloadStickerPacks(): List<StickerPack>? {
        val originInstance = origin()

        val stickerPacksRes = originInstance.get<List<StickerPack>>("stickers/package/all") ?: return null

        println("Sticker Packs Result: $stickerPacksRes")

        stickerPacks.value = stickerPacksRes

        return stickerPacksRes
    }

    override fun getStickerPacksState(): StateFlow<List<StickerPack>> = stickerPacks.asStateFlow()

    override fun clearData() {
        stickerPacks.value = emptyList()
    }
}