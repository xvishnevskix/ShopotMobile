package org.videotrade.shopot.multiplatform

import androidx.compose.ui.graphics.ImageBitmap

expect suspend fun imageAsync(imageId: String,imageName: String, isCipher:Boolean): ImageBitmap?
