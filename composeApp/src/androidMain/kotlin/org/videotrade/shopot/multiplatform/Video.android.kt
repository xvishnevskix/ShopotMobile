package org.videotrade.shopot.multiplatform

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.Environment
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import org.videotrade.shopot.androidSpecificApi.getContextObj
import java.io.File
import java.io.FileOutputStream

actual fun getFirstFrameAsBitmap(videoFilePath: String): ImageBitmap? {
    val retriever = MediaMetadataRetriever()
    return try {
        retriever.setDataSource(videoFilePath)
        val bitmap = retriever.getFrameAtTime(0) // Получаем первый кадр (на временной отметке 0)
        bitmap?.asImageBitmap() // Преобразуем Bitmap в ImageBitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    } finally {
        retriever.release()
    }
}


actual fun saveBitmapToFile(
    bitmap: ImageBitmap,
    fileName: String
): ImageBitmap? {
    TODO("Not yet implemented")
}