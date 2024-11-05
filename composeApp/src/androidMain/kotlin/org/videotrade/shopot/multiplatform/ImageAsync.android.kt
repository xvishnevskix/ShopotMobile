package org.videotrade.shopot.multiplatform

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.prepareGet
import io.ktor.util.InternalAPI
import io.ktor.utils.io.jvm.javaio.copyTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.videotrade.shopot.api.EnvironmentConfig.serverUrl
import org.videotrade.shopot.api.getValueInStorage
import java.io.File

// Определение кэша для изображений

actual suspend fun imageAsync(imageId: String, imageName: String, isCipher: Boolean): ImageBitmap? {
    try {
        val filePath = withContext(Dispatchers.IO) {
            val fileProvider = FileProviderFactory.create()
            
            if (!isCipher) {
                val imageExist = fileProvider.existingFileInDir(imageId, "image")
                
                println("imageExist $imageExist")
                
                imageExist ?: downloadImageInCache(imageId)
            } else {
                val imageExist = fileProvider.existingFileInDir(imageId, "image")
                
                println("imageExist $imageExist")
                
                imageExist ?: fileProvider.downloadCipherFile(
                    "${serverUrl}file/id/$imageId",
                    "image",
                    imageId,
                    "image"
                ) { _ -> }
                
            }
        }
        
        
        
        
        
        println("filePath4124141 $filePath")
        
        if (filePath != null) {
            return withContext(Dispatchers.IO) {
                val byteArray = File(filePath).readBytes()
                
                byteArrayToCorrectedImageBitmap(byteArray, filePath)
            }
        }
    } catch (e: Exception) {
    
    }
    
    return null
}

fun byteArrayToCorrectedImageBitmap(byteArray: ByteArray, filePath: String): ImageBitmap {
    val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    val exif = ExifInterface(filePath)
    val rotation =
        exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
    
    val rotationDegrees = when (rotation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> 90
        ExifInterface.ORIENTATION_ROTATE_180 -> 180
        ExifInterface.ORIENTATION_ROTATE_270 -> 270
        else -> 0
    }
    
    val correctedBitmap = if (rotationDegrees != 0) {
        val matrix = Matrix().apply { postRotate(rotationDegrees.toFloat()) }
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    } else {
        bitmap
    }
    
    return correctedBitmap.asImageBitmap()
}


@OptIn(InternalAPI::class)
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
            // Получение файла, в который будет сохранено содержимое ответа
            val file = File(filePath)
            
            // Открытие потока для записи в файл
            file.outputStream().use { fileOutputStream ->
                // Копирование содержимого ответа в файл
                httpResponse.content.copyTo(fileOutputStream)
            }
            
            println("Image successfully downloaded and saved to: $filePath")
        }
        
        // Проверка EXIF данных для ориентации
        val exif = ExifInterface(filePath)
        val orientation =
            exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
        println("EXIF Orientation: $orientation")
        
        return filePath
        
    } catch (e: Exception) {
        println("Error downloading file: ${e.message}")
        e.printStackTrace()
    } finally {
        client.close()
    }
    return null
}

//actual suspend fun imageAsyncIos(
//    imageId: String,
//    imageName: String,
//    isCipher: Boolean
//): ByteArray? {
//    TODO("Not yet implemented")
//}
actual suspend fun imageAsyncIos(
    imageId: String,
    imageName: String,
    isCipher: Boolean
): ByteArray? {
    TODO("Not yet implemented")
}