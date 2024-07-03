package org.videotrade.shopot.multiplatform


import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.call.receive
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.onUpload
import io.ktor.client.request.forms.InputProvider
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.readBytes
import io.ktor.client.statement.readText
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.content.OutgoingContent
import io.ktor.http.contentLength
import io.ktor.http.headersOf
import io.ktor.http.isSuccess
import io.ktor.util.InternalAPI
import io.ktor.utils.io.ByteReadChannel
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.cValue
import kotlinx.cinterop.refTo
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.toKString
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okio.Buffer
import org.videotrade.shopot.api.EnvironmentConfig
import org.videotrade.shopot.api.getValueInStorage
import org.videotrade.shopot.domain.model.FileDTO
import platform.CoreFoundation.CFRelease
import platform.CoreFoundation.CFStringCreateWithCString
import platform.CoreFoundation.CFStringGetCString
import platform.CoreFoundation.CFStringGetLength
import platform.CoreFoundation.CFStringRef
import platform.CoreFoundation.kCFStringEncodingUTF8
import platform.CoreServices.UTTypeCopyPreferredTagWithClass
import platform.CoreServices.UTTypeCreatePreferredIdentifierForTag
import platform.CoreServices.kUTTagClassFilenameExtension
import platform.CoreServices.kUTTagClassMIMEType
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSData
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSMakeRange
import platform.Foundation.NSOutputStream
import platform.Foundation.NSRange
import platform.Foundation.NSSearchPathDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSString
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.URLByAppendingPathComponent
import platform.Foundation.create
import platform.Foundation.dataWithContentsOfURL
import platform.Foundation.getBytes
import platform.Foundation.lastPathComponent
import platform.Foundation.outputStreamWithURL
import platform.Foundation.pathExtension
import platform.Foundation.stringByAppendingPathComponent
import platform.Foundation.temporaryDirectory
import platform.posix.memcpy
import platform.posix.uint8_tVar
import kotlin.math.roundToInt
import kotlin.random.Random

actual class FileProvider {
    
    actual fun getFilePath(fileName: String, fileType: String): String {
        val directory: NSURL? = when (fileType) {
            "audio/mp4" -> NSURL.fileURLWithPath(NSTemporaryDirectory(), true)
            "image" -> NSFileManager.defaultManager.URLsForDirectory(
                NSDocumentDirectory,
                NSUserDomainMask
            ).firstOrNull() as NSURL?
            
            else -> NSFileManager.defaultManager.URLsForDirectory(
                NSDocumentDirectory,
                NSUserDomainMask
            ).firstOrNull() as NSURL?
        }
        
        if (directory == null) {
            throw IllegalStateException("Could not get directory for file type $fileType")
        }
        
        var file: NSURL
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
            
            file = directory.URLByAppendingPathComponent(newFileName)!!
        } while (file.path?.let { NSFileManager.defaultManager.fileExistsAtPath(it) } == true)
        
        println("file.absolutePath ${file.path}")
        
        return file.path!!
    }
    
    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    actual suspend fun downloadFileToDirectory(
        url: String,
        fileDirectory: String,
        onProgress: (Float) -> Unit
    ) {
        val client = HttpClient(Darwin)
        
        try {
            client.prepareGet(url).execute { httpResponse ->
                val channel: ByteReadChannel = httpResponse.body()
                val totalBytes = httpResponse.contentLength() ?: -1L
                val file = NSURL.fileURLWithPath(fileDirectory)
                val outputStream = NSOutputStream.outputStreamWithURL(file, true)
                
                outputStream?.open()
                val buffer = ByteArray(8192)
                var bytesCopied: Long = 0
                var bytesRead: Int
                
                while (!channel.isClosedForRead) {
                    bytesRead = channel.readAvailable(buffer, 0, buffer.size)
                    if (bytesRead == -1) break
                    
                    buffer.usePinned { pinnedBuffer ->
                        val nsData = NSData.create(
                            bytes = pinnedBuffer.addressOf(0),
                            length = bytesRead.toULong()
                        )
                        outputStream?.write(nsData.bytes?.reinterpret(), nsData.length)
                    }
                    bytesCopied += bytesRead
                    
                    if (totalBytes != -1L) {
                        val progress =
                            (bytesCopied.toDouble() / totalBytes * 100).roundToInt() / 100f
                        onProgress(progress)
                    }
                }
                
                outputStream?.close()
                onProgress(1f) // Устанавливаем прогресс на 100% после завершения загрузки
                println("Файл сохранен в ${file.path}")
            }
        } finally {
            client.close()
        }
    }
    
    
    actual fun getFileData(fileDirectory: String): FileData? {
        val url = NSURL.fileURLWithPath(fileDirectory)
        return getMimeType(url)
    }
    
    @OptIn(ExperimentalForeignApi::class)
    private fun getMimeType(url: NSURL): FileData? {
        val pathExtension = url.pathExtension ?: return null
        val pathExtensionCF = pathExtension.toCFString()
        
        val uti = UTTypeCreatePreferredIdentifierForTag(
            kUTTagClassFilenameExtension,
            pathExtensionCF,
            null
        ) ?: return null
        
        val mimeType = UTTypeCopyPreferredTagWithClass(uti, kUTTagClassMIMEType)
        
        val mimeTypeString = mimeType?.let {
            convertCFStringToString(it)
        }
        
        // Освобождение ресурсов
        CFRelease(uti)
        if (mimeType != null) {
            CFRelease(mimeType)
        }
        
        val fileType = mimeTypeString?.substringAfter("application/")
        val fileName = url.lastPathComponent ?: "unknown"
        
        return if (fileType != null) {
            FileData(fileName, fileType)
        } else {
            null
        }
    }
    
    // Extension function to convert String to CFStringRef
    @OptIn(ExperimentalForeignApi::class)
    fun String.toCFString(): CFStringRef? {
        return CFStringCreateWithCString(null, this, kCFStringEncodingUTF8)
    }
    
    // Function to convert CFStringRef to Kotlin String
    @OptIn(ExperimentalForeignApi::class)
    fun convertCFStringToString(cfString: CFStringRef): String {
        val length = CFStringGetLength(cfString).toInt() + 1 // +1 for null-terminator
        val buffer = ByteArray(length)
        CFStringGetCString(cfString, buffer.refTo(0), buffer.size.toLong(), kCFStringEncodingUTF8)
        return buffer.toKString().trimEnd('\u0000') // Remove null terminator
    }
    
    
    actual fun existingFile(fileName: String, fileType: String): String? {
        val directory: NSString? = when (fileType) {
            "audio/mp4" -> getCacheDirectory()
            "image" -> getDownloadDirectory()
            else -> getDownloadDirectory()
        }
        
        directory ?: return null
        
        return findFileInDirectory(directory, fileName, fileType)
    }
    
    private fun getCacheDirectory(): NSString? {
        val paths = NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, true)
        return paths.firstOrNull() as NSString?
    }
    
    private fun getDownloadDirectory(): NSString? {
        val paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, true)
        return paths.firstOrNull() as NSString?
    }
    
    private fun findFileInDirectory(
        directory: NSString,
        fileName: String,
        fileType: String
    ): String? {
        val filePath = directory.stringByAppendingPathComponent(fileName)
        val fileManager = NSFileManager.defaultManager
        return if (fileManager.fileExistsAtPath(filePath)) {
            filePath
        } else {
            null
        }
    }
    
    
    @OptIn(InternalAPI::class)
    actual suspend fun uploadFileToDirectory(
        url: String,
        fileDirectory: String,
        contentType: String,
        filename: String,
        onProgress: (Float) -> Unit
    ): FileDTO? {
        val client = HttpClient(Darwin) {
            install(HttpTimeout) {
                requestTimeoutMillis = 600_000
                connectTimeoutMillis = 600_000
                socketTimeoutMillis = 600_000
            }
        }
        
        // Get the file from the provided directory
        val file = NSURL.fileURLWithPath(fileDirectory)
        val fileData = NSData.dataWithContentsOfURL(file) ?: return null
        
        try {
            val token = getValueInStorage("accessToken")
            
            val response: HttpResponse = client.post("${EnvironmentConfig.serverUrl}$url") {
                body = MultiPartFormDataContent(
                    formData {
                        append("file", fileData.toByteArray(),
                            Headers.build {
                                append(HttpHeaders.ContentType, contentType)
                                append(HttpHeaders.ContentDisposition, "filename=\"$filename\"")
                            }
                        )
                    }
                )
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
    
    // Helper function to convert NSData to ByteArray
    @OptIn(ExperimentalForeignApi::class)
    fun NSData.toByteArray(): ByteArray {
        val length = this.length.toInt()
        val byteArray = ByteArray(length)
        byteArray.usePinned {
            this.getBytes(it.addressOf(0), NSMakeRange(0.toULong(), length.toULong()))
        }
        return byteArray
    }
    
    actual fun getFileBytesForDir(fileDirectory: String): ByteArray? {
        TODO("Not yet implemented")
    }
    
}

actual object FileProviderFactory {
    actual fun create(): FileProvider {
        return FileProvider()
    }
}


