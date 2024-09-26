package org.videotrade.shopot.multiplatform

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Environment
import android.widget.MediaController
import android.widget.VideoView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlin.random.Random


actual fun getAndSaveFirstFrame(
    videoFilePath: String,
    completion: (String?, String?, ByteArray?) -> Unit
) {
    val retriever = MediaMetadataRetriever()
    return try {
        retriever.setDataSource(videoFilePath)
        val bitmap = retriever.getFrameAtTime(0) // Получаем первый кадр (на временной отметке 0)
        val fileName = "${Random.nextInt(1, 2001)}_frame.png"
        bitmap?.let {
            // Указываем папку "Загрузки"
            val downloadsDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)

            // Сохраняем Bitmap в файл
            FileOutputStream(file).use { out ->
                it.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            // Получаем массив байтов из Bitmap
            val stream = ByteArrayOutputStream()
            it.compress(Bitmap.CompressFormat.PNG, 100, stream)

            completion(
                fileName,
                file.absolutePath,
                stream.toByteArray()
            ) // Возвращаем путь к файлу и массив байтов
        } ?: completion(null, null, null)
    } catch (e: Exception) {
        e.printStackTrace()
        completion(null, null, null)
    } finally {
        retriever.release()
    }
}
@Composable
actual fun VideoPlayer(modifier: Modifier, filePath: String) {
    // Преобразование пути к файлу в URI
    if (!File(filePath).exists()) {
      return
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            VideoView(context).apply {
                setVideoPath(filePath) // Установка URI вместо прямого пути
                val mediaController = MediaController(context)
                mediaController.setAnchorView(this)
                setMediaController(mediaController)
                start()
            }
        },
        update = {}
    )
}