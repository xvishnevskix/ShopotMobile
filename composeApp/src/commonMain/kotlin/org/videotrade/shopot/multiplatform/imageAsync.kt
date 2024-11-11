package org.videotrade.shopot.multiplatform
import androidx.compose.ui.graphics.ImageBitmap

expect suspend fun imageAsync(imageId: String,imageName: String, isCipher:Boolean): ImageBitmap?

expect suspend fun imageAsyncIos(imageId: String,imageName: String, isCipher:Boolean): ByteArray?
