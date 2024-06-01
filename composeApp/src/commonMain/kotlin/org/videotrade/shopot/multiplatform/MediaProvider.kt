package org.videotrade.shopot.multiplatform

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap


expect class MediaProvider {
    suspend fun getMedia(): String
    
    
    
}

expect object MediaProviderFactory {
    fun create(): MediaProvider
}

@Composable
expect fun loadImage(uri: String): ImageBitmap?
