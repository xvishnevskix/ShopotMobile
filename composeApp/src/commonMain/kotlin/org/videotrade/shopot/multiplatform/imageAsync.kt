package org.videotrade.shopot.multiplatform

expect suspend fun imageAsync(imageId: String,imageName: String, isCipher:Boolean): ByteArray?
