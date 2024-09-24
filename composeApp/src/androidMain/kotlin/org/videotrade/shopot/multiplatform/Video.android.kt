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



actual fun getAndSaveFirstFrame(videoFilePath: String, completion: (ByteArray?) -> Unit) {
    val retriever = MediaMetadataRetriever()
    return try {
        retriever.setDataSource(videoFilePath)
        val bitmap = retriever.getFrameAtTime(0) // Получаем первый кадр (на временной отметке 0)
        
        bitmap?.let {
            // Указываем папку "Загрузки"
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, "${Random.nextInt(1, 2001)}_frame.png")
            
            // Сохраняем Bitmap в файл
            FileOutputStream(file).use { out ->
                it.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            
            // Получаем массив байтов из Bitmap
            val stream = ByteArrayOutputStream()
            it.compress(Bitmap.CompressFormat.PNG, 100, stream)
            completion(stream.toByteArray()) // Возвращаем массив байтов
        } ?: completion(null)
    } catch (e: Exception) {
        e.printStackTrace()
        completion(null)
    } finally {
        retriever.release()
    }
}