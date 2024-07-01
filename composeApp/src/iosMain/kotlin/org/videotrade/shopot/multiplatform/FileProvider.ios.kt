package org.videotrade.shopot.multiplatform


import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.request.get
import io.ktor.client.statement.readBytes
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.refTo
import kotlinx.cinterop.toKString
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
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
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.URLByAppendingPathComponent
import platform.Foundation.create
import platform.Foundation.lastPathComponent
import platform.Foundation.pathExtension
import platform.Foundation.stringByAppendingPathComponent
import platform.posix.memcpy

actual class FileProvider {
    actual fun getFilePath(fileName: String, fileType: String): String {
        val fileManager = NSFileManager.defaultManager()
        val urls = fileManager.URLsForDirectory(NSDocumentDirectory, NSUserDomainMask)
        val documentDirectory = urls.first() as NSURL
        
        // Создайте уникальный файл внутри директории документов
        var fileURL: NSURL
        do {
            val randomSuffix = (0..100000).random()
            val newFileName =
                "${fileName.substringBeforeLast(".")}_$randomSuffix.${fileName.substringAfterLast(".")}"
            fileURL = documentDirectory.URLByAppendingPathComponent(newFileName)!!
        } while (fileManager.fileExistsAtPath(fileURL.path!!))
        
        println("fileURL.path ${fileURL.path}")
        
        return fileURL.path!!
    }
    
    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    actual suspend fun downloadFileToDirectory(url: String, fileDirectory: String) {
        val client = HttpClient(Darwin)
        try {
            val fileBytes = client.get(url).readBytes()
            withContext(Dispatchers.IO) {
                val fileManager = NSFileManager.defaultManager()
                val data: NSData = fileBytes.usePinned { pinned ->
                    NSData.create(bytes = pinned.addressOf(0), length = fileBytes.size.toULong())
                }
                println("Downloaded data size: ${data.length}")
                
                fileManager.createFileAtPath(fileDirectory, data, null)
            }
        } finally {
            client.close()
        }
    }
    
    actual fun getFileBytesForDir(fileDirectory: String): ByteArray? {
        val fileManager = NSFileManager.defaultManager
        val data = fileManager.contentsAtPath(fileDirectory)
            ?: throw IllegalArgumentException("Invalid file path or file does not exist: $fileDirectory")
        
        return data.toByteArray()
    }
    
    @OptIn(ExperimentalForeignApi::class)
    private fun NSData.toByteArray(): ByteArray {
        return ByteArray(this.length.toInt()).apply {
            usePinned { pinned ->
                memcpy(pinned.addressOf(0), this@toByteArray.bytes, this@toByteArray.length)
            }
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
    
}

actual object FileProviderFactory {
    actual fun create(): FileProvider {
        return FileProvider()
    }
}


