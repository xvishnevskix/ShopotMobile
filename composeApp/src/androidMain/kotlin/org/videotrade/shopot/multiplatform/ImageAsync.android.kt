package org.videotrade.shopot.multiplatform

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import avatarCache
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.prepareGet
import io.ktor.util.InternalAPI
import io.ktor.utils.io.jvm.javaio.copyTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.videotrade.shopot.api.EnvironmentConfig.serverUrl
import org.videotrade.shopot.api.getValueInStorage
import java.io.ByteArrayOutputStream
import java.io.File
// Определение кэша для изображений

@Composable
actual fun imageAsync(imageId: String): ByteArray? {
    var imageByteArray by remember(imageId) { mutableStateOf<ByteArray?>(null) }
    
    LaunchedEffect(imageId) {
        val imageExist = FileProviderFactory.create().existingFile(imageId, "image")
        val filePath = imageExist ?: downloadImageInCache(imageId)
        
        if (filePath != null) {
            withContext(Dispatchers.IO) {
                imageByteArray = FileProviderFactory.create().getFileBytesForDir(filePath)
            }
        }
    }
    
    return imageByteArray
}
@OptIn(InternalAPI::class)
private suspend fun downloadImageInCache(imageId: String): String? {
    val client = HttpClient(getHttpClientEngine())
    val filePath = FileProviderFactory.create().getFilePath(imageId, "image") ?: return null
    
    println("starting download")
    
    try {
        val token = getValueInStorage("accessToken")
            ?: throw IllegalStateException("Access token is missing")
        
        // Подготовка запроса для скачивания файла
        client.prepareGet("${serverUrl}file/plain/$imageId") {
            header("Authorization", "Bearer $token")
        }.execute { httpResponse ->
            // Получение файла, в который будет сохранено содержимое ответа
            val file = File(filePath)
            
            // Открытие потока для записи в файл
            file.outputStream().use { fileOutputStream ->
                // Копирование содержимого ответа в файл
                httpResponse.content.copyTo(fileOutputStream)
            }
            
            println("Image successfully downloaded and saved to: $filePath")
        }
        
        return filePath
        
    } catch (e: Exception) {
        println("Error downloading file: ${e.message}")
        e.printStackTrace()
    } finally {
        client.close()
    }
    return null
}