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
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.onUpload
import io.ktor.client.request.forms.InputProvider
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.prepareGet
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentLength
import io.ktor.http.isSuccess
import io.ktor.util.decodeBase64Bytes
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.streams.asInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.koin.mp.KoinPlatform
import org.videotrade.shopot.api.EnvironmentConfig
import org.videotrade.shopot.api.getValueInStorage
import org.videotrade.shopot.domain.model.FileDTO
import org.videotrade.shopot.domain.model.WebRTCMessage
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
            "zip" -> applicationContext.cacheDir
            "cipher" -> applicationContext.cacheDir
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
        val client = HttpClient()
        
        try {
            client.prepareGet(url).execute { httpResponse ->
                val channel: ByteReadChannel = httpResponse.body()
                val totalBytes = httpResponse.contentLength() ?: -1L
                val file = File(fileDirectory)
                
                file.outputStream().use { outputStream ->
                    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                    var bytesCopied: Long = 0
                    var bytesRead: Int
                    
                    while (!channel.isClosedForRead) {
                        bytesRead = channel.readAvailable(buffer, 0, buffer.size)
                        if (bytesRead == -1) break
                        
                        outputStream.write(buffer, 0, bytesRead)
                        bytesCopied += bytesRead
                        
                        if (totalBytes != -1L) {
                            val progress =
                                (bytesCopied.toDouble() / totalBytes * 100).roundToInt() / 100f
                            onProgress(progress)
                        }
                    }
                }
                onProgress(1f) // Устанавливаем прогресс на 100% после завершения загрузки
                println("A file saved to ${file.path}")
            }
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
        
        // Получаем размер файла из URI
        val fileSize = getFileSizeFromUri(contentResolver, uri)
        
        return if (fileName != null && fileType != null && fileSize != null) {
            FileData(fileName, fileType, fileSize)
        } else {
            null
        }
    }
    
    private fun getFileSizeFromUri(contentResolver: ContentResolver, uri: Uri): Int? {
        val cursor = contentResolver.query(uri, null, null, null, null)
        return cursor?.use {
            val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
            if (sizeIndex != -1 && it.moveToFirst()) {
                it.getLong(sizeIndex).toInt()
            } else {
                null
            }
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
    
    
    actual suspend fun uploadCipherFile(
        url: String,
        fileDirectory: String,
        cipherFilePath: String,
        contentType: String,
        filename: String,
        onProgress: (Float) -> Unit
    ): FileDTO? {
        
        val client = HttpClient() {
            install(HttpTimeout) {
                requestTimeoutMillis = 600_000
                connectTimeoutMillis = 600_000
                socketTimeoutMillis = 600_000
            }
        }
        
        val sharedSecret = getValueInStorage("sharedSecret")
        
        val cipherWrapper: CipherWrapper = KoinPlatform.getKoin().get()
        
        val encupsChachaFileResult = cipherWrapper.encupsChachaFileCommon(
            fileDirectory,
            cipherFilePath,
            sharedSecret?.decodeBase64Bytes()!!
        )
        
        
        if (encupsChachaFileResult == null) {
            
            return null
        }
        println("result2 $fileDirectory")
        
        val file = File(cipherFilePath)
        if (!file.exists()) {
            println("File not found: ${file.absolutePath}")
            return null
        }
        
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
                        // Добавляем block и authTag как дополнительные поля
                        append(
                            "encupsFile",
                            Json.encodeToString(
                                EncapsulationFileResult.serializer(),
                                encupsChachaFileResult
                            )
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
            
        } catch (e: Exception) {
            println("File upload failed: ${e.message}")
            return null
            
        } finally {
            client.close()
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


suspend fun getFileFromUri(context: Context, uri: Uri): File =
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




