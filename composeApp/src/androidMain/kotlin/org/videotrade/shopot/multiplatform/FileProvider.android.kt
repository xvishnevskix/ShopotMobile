package org.videotrade.shopot.multiplatform

import android.content.Context
import android.net.Uri
import android.os.Build
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
    actual fun getAudioFilePath(fileName: String): String {
        // Используем каталог кэша приложения
        val directory = applicationContext.cacheDir
        if (!directory.exists()) {
            directory.mkdirs()
        }
        
        var file: File
        do {
            val randomSuffix = Random.nextInt(0, 100000)
            val newFileName =
                "${fileName.substringBeforeLast(".")}_$randomSuffix.${fileName.substringAfterLast(".")}"
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
    
    private fun readBytesFromUri(context: Context, uri: Uri): ByteArray? {
        val contentResolver = context.contentResolver
        val inputStream: InputStream = contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("Invalid file path or file does not exist: $uri")
        
        return inputStream.use { it.readBytes() }
    }
    
    actual fun getFileType(fileDirectory: String): String? {
        val uri = Uri.parse(fileDirectory)
        return getMimeType(applicationContext, uri)
    }
    
    private fun getMimeType(context: Context, uri: Uri): String? {
        val contentResolver = context.contentResolver
        
        // Получаем MIME-тип из ContentResolver
        val mimeType = contentResolver.getType(uri)
        
        if (mimeType == null) {
            // Если ContentResolver не смог определить MIME-тип, пробуем определить его из расширения файла
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            if (fileExtension != null) {
                return MimeTypeMap.getSingleton()
                    .getMimeTypeFromExtension(fileExtension.toLowerCase(Locale.ROOT))
            }
        }
        
        return mimeType
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


