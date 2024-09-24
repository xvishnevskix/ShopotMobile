package org.videotrade.shopot.multiplatform

import androidx.compose.ui.graphics.ImageBitmap

expect fun getFirstFrameAsBitmap(videoFilePath: String) : ImageBitmap?

expect fun saveBitmapToFile(bitmap: ImageBitmap, fileName: String) : ImageBitmap?


