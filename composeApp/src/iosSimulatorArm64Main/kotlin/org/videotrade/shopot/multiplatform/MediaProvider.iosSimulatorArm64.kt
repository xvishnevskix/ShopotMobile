package org.videotrade.shopot.multiplatform

import androidx.compose.ui.graphics.ImageBitmap

actual object MediaProviderFactory {
    actual fun create(): MediaProvider {
        TODO("Not yet implemented")
    }
}

actual class MediaProvider {
    actual suspend fun getMedia() {
        TODO("Not yet implemented")
        
    }
}

@Composable
actual fun loadImage(uri: String): ImageBitmap? {
    TODO("Not yet implemented")
}