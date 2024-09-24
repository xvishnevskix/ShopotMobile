package org.videotrade.shopot.multiplatform

import androidx.compose.ui.graphics.ImageBitmap
import org.jetbrains.skia.Bitmap

expect fun getFirstFrameAsBitmap(videoFilePath: String) : Bitmap?

expect fun saveBitmapToFile(bitmap: ImageBitmap, fileName: String) : ByteArray?

expect fun getImageFromVideo(url: String, completion: (ByteArray?) -> Unit)


