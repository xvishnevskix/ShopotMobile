package org.videotrade.shopot.multiplatform

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.Environment
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import org.videotrade.shopot.androidSpecificApi.getContextObj
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlin.random.Random

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
): ByteArray? {
    // Преобразуем ImageBitmap в Bitmap
    val androidBitmap = bitmap.asAndroidBitmap() // Убедитесь, что у вас есть функция для преобразования в Bitmap
    
    // Указываем папку "Загрузки"
    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val file = File(downloadsDir, "${Random.nextInt(1, 2001).toString() + fileName}.png")
    
    return try {
        // Сохраняем Bitmap в файл
        FileOutputStream(file).use { out ->
            androidBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        
        // Получаем массив байтов из Bitmap
        val stream = ByteArrayOutputStream()
        androidBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.toByteArray() // Возвращаем массив байтов
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}