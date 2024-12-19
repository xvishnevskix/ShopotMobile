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
import io.ktor.util.decodeBase64Bytes
import io.ktor.utils.io.jvm.javaio.copyTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.mp.KoinPlatform
import org.videotrade.shopot.api.EnvironmentConfig.SERVER_URL
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
                    "${SERVER_URL}file/id/$imageId",
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

    println("starting download")
    
    try {
        val token = getValueInStorage("accessToken")
            ?: throw IllegalStateException("Access token is missing")

        val cipherPath = FileProviderFactory.create().createNewFileWithApp(
            imageId.substringBeforeLast(".", imageId),
            "cipher"
        ) ?: return null

        val filePath =
            FileProviderFactory.create().createNewFileWithApp(imageId, "image") ?: return null
        
        // Подготовка запроса для скачивания файла
        client.prepareGet("${SERVER_URL}file/id/$imageId") {
            header("Authorization", "Bearer $token")
        }.execute { httpResponse ->
            // Получение файла, в который будет сохранено содержимое ответа
            val file = File(cipherPath)

            // Открытие потока для записи в файл
            file.outputStream().use { fileOutputStream ->
                // Копирование содержимого ответа в файл
                httpResponse.content.copyTo(fileOutputStream)
            }


            val sharedSecret = getValueInStorage("sharedSecret")?.decodeBase64Bytes()
            val cipherWrapper: CipherWrapper = KoinPlatform.getKoin().get()

            val block = httpResponse.headers["block"]?.decodeBase64Bytes()
            val authTag = httpResponse.headers["authTag"]?.decodeBase64Bytes()


            if (block != null && authTag != null && sharedSecret != null) {
                val result3 = cipherWrapper.decupsChachaFileCommon(
                    cipherPath,
                    filePath,
                    block,
                    authTag,
                    sharedSecret
                )

                if (result3 != null) {
                    file.delete()
                    println("encupsChachaFileResult $result3")
                }
            } else {
                println("Decryption parameters are missing: block=$block, authTag=$authTag, sharedSecret=$sharedSecret")
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