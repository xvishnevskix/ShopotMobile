package org.videotrade.shopot.multiplatform

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
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
import java.io.FileInputStream

// Определение кэша для изображений

actual suspend fun imageAsync(imageId: String, imageName: String, isCipher: Boolean): ByteArray? {
    val filePath = withContext(Dispatchers.IO) {
        
        if (!isCipher) {
            val imageExist = FileProviderFactory.create().existingFile(imageId, "image")
            
            println("imageExist $imageExist")
            
            imageExist ?: downloadImageInCache(imageId)
        } else {
            val imageExist = FileProviderFactory.create().existingFile(imageName, "image")
            
            println("imageExist $imageExist")
            
            imageExist ?: FileProviderFactory.create().downloadCipherFile(
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
    val projection = arrayOf(MediaStore.Images.Media._ID)
    val selection = "${MediaStore.Images.Media.DATA} = ?"
    val selectionArgs = arrayOf(filePath)
    
    val contentUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
    } else {
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }
    
    val uri = context.contentResolver.query(contentUri, projection, selection, selectionArgs, null)
        .use { cursor ->
            if (cursor != null && cursor.moveToFirst()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                ContentUris.withAppendedId(contentUri, id)
            } else {
                null
            }
        }
    
    return uri?.let {
        try {
            context.contentResolver.openInputStream(it)?.use { inputStream ->
                inputStream.readBytes()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    } ?: run {
        // Если файл не найден через MediaStore, попробуем открыть его напрямую по filePath
        try {
            val file = File(filePath)
            if (file.exists()) {
                FileInputStream(file).use { fileInputStream ->
                    fileInputStream.readBytes()
                }
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
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