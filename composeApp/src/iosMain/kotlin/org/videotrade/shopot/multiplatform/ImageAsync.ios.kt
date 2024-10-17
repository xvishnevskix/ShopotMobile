package org.videotrade.shopot.multiplatform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import coil3.compose.rememberAsyncImagePainter
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.readBytes
import io.ktor.util.InternalAPI
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import org.videotrade.shopot.api.EnvironmentConfig.serverUrl
import org.videotrade.shopot.api.getValueInStorage
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.dataWithBytes
import platform.Foundation.dataWithContentsOfURL
import platform.Foundation.writeToURL
import platform.UIKit.UIImage


@Composable
actual fun imageAsync(imageId: String): ByteArray? {
    var imageBitmap by remember(imageId) { mutableStateOf<ByteArray?>(null) }
    
    LaunchedEffect(imageId) {
        val imageExist = FileProviderFactory.create().existingFile(imageId, "image")
        val filePath = imageExist ?: downloadImageInCache(imageId)
        
        if (filePath != null) {
            withContext(Dispatchers.IO) {
                val fileUrl = NSURL.fileURLWithPath(filePath)
                val imageData = NSData.dataWithContentsOfURL(fileUrl)
                
                if (imageData != null) {
                    imageBitmap = imageData.toByteArray()                }
            }
        }
    }
    
    return imageBitmap
}


@OptIn(InternalAPI::class, ExperimentalForeignApi::class)
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
            val fileManager = NSFileManager.defaultManager
            val fileUrl = NSURL.fileURLWithPath(filePath)
            
            // Убедимся, что файл существует; если нет — создаем пустой файл
            if (!fileManager.fileExistsAtPath(filePath)) {
                fileManager.createFileAtPath(filePath, null, null)
            }
            
            try {
                // Получение данных из ответа и запись в файл
                val data = httpResponse.readBytes()
                val nsData = data.usePinned { pinned ->
                    NSData.dataWithBytes(pinned.addressOf(0), data.size.toULong())
                }
                
                if (nsData.writeToURL(fileUrl, true)) {
                    println("Image successfully downloaded and saved to: $filePath")
                } else {
                    println("Error writing file to path: $filePath")
                    return@execute null
                }
            } catch (e: Exception) {
                println("Error writing file: ${e.message}")
                e.printStackTrace()
                return@execute null
            }
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

