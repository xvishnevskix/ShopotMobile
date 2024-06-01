package org.videotrade.shopot.multiplatform

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

actual class MediaProvider {
    actual suspend fun getMedia(): String {
        TODO("Not yet implemented")
    }
    
    
}

actual object MediaProviderFactory {
    actual fun create(): MediaProvider {
        TODO("Not yet implemented")
    }
}

@Composable
actual fun loadImage(uri: String): ImageBitmap? {
    TODO("Not yet implemented")
}