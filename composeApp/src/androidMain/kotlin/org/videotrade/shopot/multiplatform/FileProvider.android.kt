package org.videotrade.shopot.multiplatform

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.onDownload
import io.ktor.client.plugins.onUpload
import io.ktor.client.request.forms.InputProvider
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentLength
import io.ktor.http.isSuccess
import io.ktor.utils.io.jvm.javaio.copyTo
import io.ktor.utils.io.streams.asInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.videotrade.shopot.api.EnvironmentConfig
import org.videotrade.shopot.api.getValueInStorage
import org.videotrade.shopot.domain.model.FileDTO
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.util.Locale
import kotlin.math.roundToInt
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
    actual suspend fun downloadFileToDirectory(
        url: String,
        fileDirectory: String,
        onProgress: (Float) -> Unit
    ) {
        val client = HttpClient {
            HttpResponseValidator {
                handleResponseExceptionWithRequest { exception, _ ->
                    throw exception
                }
            }
        }
        
        try {
            val response: HttpResponse = client.get(url) {
                onDownload { bytesSentTotal, contentLength ->
                    if (contentLength != -1L) { // -1 means that the content length is unknown
                        val progress =
                            (bytesSentTotal.toDouble() / contentLength * 100).roundToInt() / 100f
                        onProgress(progress)
                    }
                }
            }
            
            val totalBytes = response.contentLength() ?: -1
            println("Total file size: ${totalBytes / (1024 * 1024)} MB")
            
            val file = File(fileDirectory)
            withContext(Dispatchers.IO) {
                FileOutputStream(file).use { outputStream ->
                    response.bodyAsChannel().copyTo(outputStream)
                }
            }
            onProgress(1f) // Set progress to 100% after download is complete
        } finally {
            client.close()
        }
    }
    
    actual fun getFileBytesForDir(fileDirectory: String): ByteArray? {
        val uri = Uri.parse(fileDirectory)
        return readBytesFromUri(applicationContext, uri)
    }
    
    
    @RequiresApi(Build.VERSION_CODES.Q)
    actual fun getFileData(fileDirectory: String): FileData? {
        
        println("uri $fileDirectory")
        
        val uri = Uri.parse(fileDirectory)
        
        return getData(applicationContext, uri)
    }
    
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getData(context: Context, uri: Uri): FileData? {
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
        
        // Получаем абсолютный путь файла из URI
        
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
                val nameIndex = it.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
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
    
    
    actual suspend fun uploadFileToDirectory(
        url: String,
        fileDirectory: String,
        contentType: String,
        filename: String,
        onProgress: (Float) -> Unit
    ): FileDTO? {
        val uri = Uri.parse(fileDirectory)
        println("Parsed URI: $uri")
        
        val client = HttpClient() {
            install(HttpTimeout) {
                requestTimeoutMillis = 600_000
                connectTimeoutMillis = 600_000
                socketTimeoutMillis = 600_000
            }
        }
        
        // Get the file from URI
        val file = getFileFromUri(applicationContext, uri)
        println("Local file path: ${file.absolutePath}")
        
        try {
            val token = getValueInStorage("accessToken")
            
            val response: HttpResponse = client.post("${EnvironmentConfig.serverUrl}$url") {
                setBody(MultiPartFormDataContent(
                    formData {
                        append(
                            "file",
                            InputProvider(file.length()) { file.inputStream().asInput() },
                            Headers.build {
                                append(HttpHeaders.ContentType, contentType)
                                append(HttpHeaders.ContentDisposition, "filename=\"$filename\"")
                            }
                        )
                    }
                ))
                header(HttpHeaders.Authorization, "Bearer $token")
                
                onUpload { bytesSentTotal, contentLength ->
                    if (contentLength != -1L) { // -1 means that the content length is unknown
                        val progress = (bytesSentTotal.toDouble() / contentLength * 100).toFloat()
                        onProgress(progress)
                    }
                }
            }
            if (response.status.isSuccess()) {
                val responseData: FileDTO = Json.decodeFromString(response.bodyAsText())
                
                
                return responseData
                
            } else {
                println("Failed to retrieve data: ${response.status.description} ${response.request}")
                return null
                
            }
            println("File uploaded successfully: ${response.status}")
        } catch (e: Exception) {
            println("File upload failed: ${e.message}")
            return null
            
        } finally {
            
            client.close()
            
        }
    }
    
    private suspend fun getFileFromUri(context: Context, uri: Uri): File =
        withContext(Dispatchers.IO) {
            val fileName = getFileName(context, uri)
            val tempFile = File(context.cacheDir, fileName)
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            tempFile
        }
    
    @SuppressLint("Range")
    fun getFileName(context: Context, uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result!!.substring(cut + 1)
            }
        }
        return result!!
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
    
    val tempFile = File.createTempFile("temp", null, context.cacheDir)
    inputStream.use { input ->
        tempFile.outputStream().use { output ->
            input.copyTo(output)
        }
    }
    
    val byteArrayOutputStream = ByteArrayOutputStream()
    FileInputStream(tempFile).use { input ->
        val buffer = ByteArray(1024) // Buffer size of 1KB
        var bytesRead: Int
        
        while (input.read(buffer).also { bytesRead = it } != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead)
        }
    }
    
    tempFile.delete() // Удаляем временный файл
    
    return byteArrayOutputStream.toByteArray()
}




