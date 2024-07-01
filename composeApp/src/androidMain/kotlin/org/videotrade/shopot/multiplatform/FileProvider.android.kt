package org.videotrade.shopot.multiplatform

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.jvm.javaio.copyTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Locale
import kotlin.random.Random


actual class FileProvider(private val applicationContext: Context) {
    actual fun getFilePath(fileName: String, fileType: String): String {
        // Используем каталог кэша приложения
        val directory = when (fileType) {
            "audio/mp4" -> applicationContext.cacheDir
            "image" -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            else -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        }
        
        if (!directory.exists()) {
            directory.mkdirs()
        }
        
        var file: File
        do {
            val randomSuffix = Random.nextInt(0, 100000)
            
            val newFileName = when (fileType) {
                "audio/mp4" -> "${fileName.substringBeforeLast(".")}_$randomSuffix.${
                    fileName.substringAfterLast(
                        "."
                    )
                }"
                
                else -> fileName
                
            }
//            val newFileName =
//                "${fileName.substringBeforeLast(".")}_$randomSuffix.${fileName.substringAfterLast(".")}"
            file = File(directory, newFileName)
        } while (file.exists())
        
        println("file.absolutePath ${file.absolutePath}")
        
        return file.absolutePath
    }
    
    
    @RequiresApi(Build.VERSION_CODES.O)
    actual suspend fun downloadFileToDirectory(url: String, fileDirectory: String) {
        val client = HttpClient()
        try {
            val response = client.get(url) {
            
            }.bodyAsChannel()
            
            
            println("response ${response.totalBytesRead}")
            withContext(Dispatchers.IO) {
                val path = Paths.get(fileDirectory)
                Files.newOutputStream(path).use { fileOutputStream ->
                    response.copyTo(fileOutputStream)
                }
            }
        } finally {
            client.close()
        }
    }
    
    actual fun getFileBytesForDir(fileDirectory: String): ByteArray? {
        val uri = Uri.parse(fileDirectory)
        return readBytesFromUri(applicationContext, uri)
    }
    
    
    actual fun getFileData(fileDirectory: String): FileData? {
        val uri = Uri.parse(fileDirectory)
        return getMimeType(applicationContext, uri)
    }
    
    private fun getMimeType(context: Context, uri: Uri): FileData? {
        val contentResolver = context.contentResolver
        
        // Получаем MIME-тип из ContentResolver
        val mimeType = contentResolver.getType(uri)
        
        // Определяем тип файла
        val fileType = mimeType?.substringAfter("application/") ?: run {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            MimeTypeMap.getSingleton()
                .getMimeTypeFromExtension(fileExtension?.lowercase(Locale.ROOT))
                ?.substringAfter("application/")
        }
        
        // Получаем имя файла из URI
        val fileName = getFileNameFromUri(contentResolver, uri)
        
        return if (fileName != null && fileType != null) {
            FileData(fileName, fileType)
        } else {
            null
        }
    }
    
    private fun getFileNameFromUri(contentResolver: ContentResolver, uri: Uri): String? {
        var fileName: String? = null
        val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = it.getString(nameIndex)
                }
            }
        }
        return fileName
    }
    
    
    actual fun existingFile(fileName: String, fileType: String): String? {
        val directory = when (fileType) {
            "audio/mp4" -> applicationContext.cacheDir
            "image" -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            else -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        } ?: return null
        
        return findFileInDirectory(directory, fileName, fileType)
    }
    
    private fun findFileInDirectory(directory: File, fileName: String, fileType: String): String? {
        val file = File(directory, fileName)
        return if (file.exists()) {
            file.absolutePath
        } else {
            null
        }
    }
}


actual object FileProviderFactory {
    
    private lateinit var applicationContext: Context
    fun initialize(context: Context) {
        this.applicationContext = context
    }
    
    actual fun create(): FileProvider {
        return FileProvider(applicationContext)
    }
}


private fun readBytesFromUri(context: Context, uri: Uri): ByteArray? {
    val contentResolver = context.contentResolver
    val inputStream: InputStream = contentResolver.openInputStream(uri)
        ?: throw IllegalArgumentException("Invalid file path or file does not exist: $uri")
    
    return inputStream.use { it.readBytes() }
}


private fun getMimeType(context: Context, uri: Uri): FileData? {
    val contentResolver = context.contentResolver
    
    // Получаем MIME-тип из ContentResolver
    val mimeType = contentResolver.getType(uri)
    
    val fileType = if (mimeType == null) {
        // Если ContentResolver не смог определить MIME-тип, пробуем определить его из расширения файла
        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
        if (fileExtension != null) {
            MimeTypeMap.getSingleton()
                .getMimeTypeFromExtension(fileExtension.lowercase(Locale.ROOT))
        } else {
            null
        }
    } else {
        mimeType
    }?.substringAfter("application/")
    
    val fileName = uri.lastPathSegment?.substringAfterLast("/") ?: "unknown"
    
    return if (fileType != null) {
        FileData(fileName, fileType)
    } else {
        null
    }
}

