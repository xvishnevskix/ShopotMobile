package org.videotrade.shopot.multiplatform

import androidx.compose.ui.graphics.ImageBitmap
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


actual suspend fun imageAsync(imageId: String, imageName: String, isCipher: Boolean): ImageBitmap? {
    return null
}


@OptIn(InternalAPI::class, ExperimentalForeignApi::class)
private suspend fun downloadImageInCache(imageId: String): String? {
    val client = HttpClient(getHttpClientEngine())
    val filePath =
        FileProviderFactory.create().createNewFileWithApp(imageId, "image") ?: return null
    
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

actual suspend fun imageAsyncIos(
    imageId: String,
    imageName: String,
    isCipher: Boolean
): ByteArray? {
    val imageExist = FileProviderFactory.create().existingFileInDir(imageId, "image")
    val filePath = imageExist ?: downloadImageInCache(imageId)
    
    return if (filePath != null) {
        withContext(Dispatchers.IO) {
            val fileUrl = NSURL.fileURLWithPath(filePath)
            val imageData = NSData.dataWithContentsOfURL(fileUrl)
            
            imageData?.toByteArray()
        }
    } else {
        null
    }
}