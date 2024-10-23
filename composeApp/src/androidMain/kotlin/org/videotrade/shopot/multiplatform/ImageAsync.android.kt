package org.videotrade.shopot.multiplatform

import android.content.Context
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.prepareGet
import io.ktor.util.InternalAPI
import io.ktor.utils.io.jvm.javaio.copyTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.videotrade.shopot.androidSpecificApi.getContextObj
import org.videotrade.shopot.api.EnvironmentConfig.serverUrl
import org.videotrade.shopot.api.getValueInStorage
import java.io.File

// Определение кэша для изображений

actual suspend fun imageAsync(imageId: String, imageName: String, isCipher: Boolean): ByteArray? {
    val filePath = withContext(Dispatchers.IO) {
        val fileProvider = FileProviderFactory.create()
        
        if (!isCipher) {
            val imageExist = fileProvider.existingFileInDir(imageId, "image")
            
            println("imageExist $imageExist")
            
            imageExist ?: downloadImageInCache(imageId)
        } else {
            val imageExist = fileProvider.existingFileInDir(imageName, "image")
            
            println("imageExist $imageExist")
            
            imageExist ?: fileProvider.downloadCipherFile(
                "${serverUrl}file/id/$imageId",
                "image",
                imageName,
                "image"
            ) { _ -> }
            
        }
    }
    
    
    
    
    
    println("filePath4124141 $filePath")
    
    if (filePath != null) {
        return withContext(Dispatchers.IO) {


//            val bitmap = BitmapFactory.decodeFile(filePath)
//
//            if (bitmap != null) {
//                val outputStream = ByteArrayOutputStream()
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
//                outputStream.toByteArray()
//            } else {
//                null
//            }
            
            getFileAsByteArray(getContextObj.getContext(), filePath)
        }
    }
    
    return null
}

fun getFileAsByteArray(context: Context, filePath: String): ByteArray? {
   return try {
       val op = File(filePath).readBytes()
       println("op ${op.size}")
       op
    } catch (e: Exception) {
       null
       
    }
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
        
        return filePath
        
    } catch (e: Exception) {
        println("Error downloading file: ${e.message}")
        e.printStackTrace()
    } finally {
        client.close()
    }
    return null
}