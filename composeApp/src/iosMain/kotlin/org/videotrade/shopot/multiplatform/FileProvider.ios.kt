package org.videotrade.shopot.multiplatform


import androidx.compose.ui.graphics.ImageBitmap
import io.github.vinceglb.filekit.core.FileKit
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.darwin.Darwin
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
import io.ktor.util.InternalAPI
import io.ktor.util.decodeBase64Bytes
import io.ktor.utils.io.ByteReadChannel
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.ptr
import kotlinx.cinterop.refTo
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.toKString
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.koin.mp.KoinPlatform
import org.videotrade.shopot.api.EnvironmentConfig
import org.videotrade.shopot.api.getValueInStorage
import org.videotrade.shopot.domain.model.FileDTO
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
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
import platform.Foundation.NSError
import platform.Foundation.NSFileCoordinator
import platform.Foundation.NSFileCoordinatorReadingOptions
import platform.Foundation.NSFileManager
import platform.Foundation.NSFileSize
import platform.Foundation.NSMakeRange
import platform.Foundation.NSNumber
import platform.Foundation.NSOutputStream
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSString
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.URLByAppendingPathComponent
import platform.Foundation.create
import platform.Foundation.dataWithContentsOfURL
import platform.Foundation.getBytes
import platform.Foundation.outputStreamWithURL
import platform.Foundation.pathExtension
import platform.Foundation.stringByAppendingPathComponent
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerViewController
import platform.darwin.NSObject
import kotlin.math.roundToInt
import kotlin.random.Random
import platform.Foundation.*
import kotlin.concurrent.AtomicReference

actual class FileProvider {
    
    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    actual suspend fun pickFile(pickerType: PickerType): PlatformFilePick? {
        try {
            val filePick = FileKit.pickFile(
                type = pickerType,
                mode = PickerMode.Single,
            )
            
            if (filePick?.nsUrl != null) {
                val filePath = filePick.nsUrl.path
                val fileManager = NSFileManager.defaultManager
                
                if (filePath != null) {
                    val fileURL = NSURL.fileURLWithPath(filePath)
                    
                    // Начинаем доступ к файлу
                    val securityScoped = fileURL.startAccessingSecurityScopedResource()
                    
                    // Проверяем существование файла
                    val fileExists = fileManager.fileExistsAtPath(filePath)
                    println("fileExists at path $filePath: $fileExists")
                    
                    if (fileExists) {
                        println("file exists at path: $filePath")
                        
                        // Копируем файл в директорию Documents
                        val documentsDirectories =
                            fileManager.URLsForDirectory(NSDocumentDirectory, NSUserDomainMask)
                        val documentsDirectory = documentsDirectories[0] as? NSURL
                        if (documentsDirectory != null) {
                            val destinationURL =
                                documentsDirectory.URLByAppendingPathComponent(filePick.name)
                            
                            // Проверка и удаление существующего файла
                            if (fileManager.fileExistsAtPath(destinationURL?.path!!)) {
                                val deleteErrorPtr = nativeHeap.alloc<ObjCObjectVar<NSError?>>()
                                fileManager.removeItemAtURL(destinationURL, deleteErrorPtr.ptr)
                                if (deleteErrorPtr.value != null) {
                                    println("Error deleting existing file: ${deleteErrorPtr.value}")
                                    return null
                                }
                            }
                            
                            val success = copyFile(fileURL, destinationURL)
                            if (success) {
                                println("file copied to: ${destinationURL.path}")
                                
                                // Заканчиваем доступ к файлу
                                if (securityScoped) {
                                    fileURL.stopAccessingSecurityScopedResource()
                                }
                                
                                // Проверка значений для PlatformFilePick
                                val originalPath = filePick.path
                                val destinationPath = destinationURL.path
                                val fileSize = getFileSize(destinationURL)
                                val fileName = filePick.name
                                
                                if (originalPath == null || destinationPath == null || fileSize == null) {
                                    println("Null value detected: originalPath=$originalPath, destinationPath=$destinationPath, fileSize=$fileSize, fileName=$fileName")
                                    throw NullPointerException("One or more values are null")
                                }
                                
                                return PlatformFilePick(
                                    originalPath,
                                    destinationPath,
                                    fileSize,
                                    fileName
                                )
                            } else {
                                println("Failed to copy file to: ${destinationURL.path}")
                            }
                        } else {
                            println("Could not find documents directory")
                        }
                    } else {
                        println("file does not exist at path: $filePath")
                    }
                    
                    // Заканчиваем доступ к файлу
                    if (securityScoped) {
                        fileURL.stopAccessingSecurityScopedResource()
                    }
                } else {
                    println("filePath is null")
                }
            } else {
                println("filePick is null or nsUrl is null")
            }
            return null
        } catch (e: Exception) {
            println("Exception occurred: ${e}")
            return null
        }
    }
    
    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    actual suspend fun pickGallery(): PlatformFilePick? {
        val imageAndVideoType = PickerType.ImageAndVideo
        
        
        try {
            val filePick = FileKit.pickFile(
                type = imageAndVideoType,
                mode = PickerMode.Single,
            )
            
            if (filePick?.nsUrl != null) {
                val filePath = filePick.nsUrl.path
                val fileManager = NSFileManager.defaultManager
                
                if (filePath != null) {
                    val fileURL = NSURL.fileURLWithPath(filePath)
                    
                    // Начинаем доступ к файлу
                    val securityScoped = fileURL.startAccessingSecurityScopedResource()
                    
                    // Проверяем существование файла
                    val fileExists = fileManager.fileExistsAtPath(filePath)
                    println("fileExists at path $filePath: $fileExists")
                    
                    if (fileExists) {
                        println("file exists at path: $filePath")
                        
                        // Копируем файл в директорию Documents
                        val documentsDirectories =
                            fileManager.URLsForDirectory(NSDocumentDirectory, NSUserDomainMask)
                        val documentsDirectory = documentsDirectories[0] as? NSURL
                        if (documentsDirectory != null) {
                            val destinationURL =
                                documentsDirectory.URLByAppendingPathComponent(filePick.name)
                            
                            // Проверка и удаление существующего файла
                            if (fileManager.fileExistsAtPath(destinationURL?.path!!)) {
                                val deleteErrorPtr = nativeHeap.alloc<ObjCObjectVar<NSError?>>()
                                fileManager.removeItemAtURL(destinationURL, deleteErrorPtr.ptr)
                                if (deleteErrorPtr.value != null) {
                                    println("Error deleting existing file: ${deleteErrorPtr.value}")
                                    return null
                                }
                            }
                            
                            val success = copyFile(fileURL, destinationURL)
                            if (success) {
                                println("file copied to: ${destinationURL.path}")
                                
                                // Заканчиваем доступ к файлу
                                if (securityScoped) {
                                    fileURL.stopAccessingSecurityScopedResource()
                                }
                                
                                // Проверка значений для PlatformFilePick
                                val originalPath = filePick.path
                                val destinationPath = destinationURL.path
                                val fileSize = getFileSize(destinationURL)
                                val fileName = filePick.name
                                
                                if (originalPath == null || destinationPath == null || fileSize == null) {
                                    println("Null value detected: originalPath=$originalPath, destinationPath=$destinationPath, fileSize=$fileSize, fileName=$fileName")
                                    throw NullPointerException("One or more values are null")
                                }
                                
                                return PlatformFilePick(
                                    originalPath,
                                    destinationPath,
                                    fileSize,
                                    fileName
                                )
                            } else {
                                println("Failed to copy file to: ${destinationURL.path}")
                            }
                        } else {
                            println("Could not find documents directory")
                        }
                    } else {
                        println("file does not exist at path: $filePath")
                    }
                    
                    // Заканчиваем доступ к файлу
                    if (securityScoped) {
                        fileURL.stopAccessingSecurityScopedResource()
                    }
                } else {
                    println("filePath is null")
                }
            } else {
                println("filePick is null or nsUrl is null")
            }
            return null
        } catch (e: Exception) {
            println("Exception occurred: ${e}")
            return null
        }
    }
    
    
    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    private fun copyFile(sourceURL: NSURL, destinationURL: NSURL): Boolean {
        val fileManager = NSFileManager.defaultManager
        memScoped {
            val errorPtr = alloc<ObjCObjectVar<NSError?>>()
            fileManager.copyItemAtURL(sourceURL, destinationURL, errorPtr.ptr)
            if (errorPtr.value != null) {
                println("Error copying file: ${errorPtr.value}")
                return false
            }
        }
        return true
    }
    @OptIn(ExperimentalForeignApi::class)
    fun copyFileToDocuments(filePath: String, destinationFilename: String): Boolean {
        return try {
            val fileManager = NSFileManager.defaultManager()
            
            // Получение URL для директории Documents
            val urls =
                fileManager.URLsForDirectory(NSDocumentDirectory, NSUserDomainMask) as List<*>
            val documentsURL = urls.firstOrNull() as? NSURL
            
            documentsURL?.let { url ->
                // Создание URL для целевого файла
                val destinationURL = url.URLByAppendingPathComponent(destinationFilename)
                
                // Создание URL для исходного файла
                val sourceURL = NSURL.fileURLWithPath(filePath)
                
                // Копирование файла
                if (destinationURL != null) {
                    fileManager.copyItemAtURL(sourceURL, toURL = destinationURL, error = null)
                }
                
                println("File copied successfully to: $destinationURL")
                true
            } ?: run {
                println("Documents directory not found.")
                false
            }
        } catch (e: Exception) {
            println("Exception occurred: ${e}")
            false
        }
    }
    
    private fun saveFileInDownload(sourceFilePath: String, destinationFilename: String) {
        val tempFileURL = createTempFile(destinationFilename)
        copyFile(sourceFilePath, tempFileURL)
        
        val documentPicker = UIDocumentPickerViewController(
            forExportingURLs = listOf(tempFileURL)
        )
        documentPicker.delegate = object : NSObject(), UIDocumentPickerDelegateProtocol {
            override fun documentPicker(
                controller: UIDocumentPickerViewController,
                didPickDocumentsAtURLs: List<*>
            ) {
                println("File saved successfully.")
            }
        }
        
        // Получите корневой viewController и представьте documentPicker
        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        rootViewController?.presentViewController(
            documentPicker,
            animated = true,
            completion = null
        )
    }
    
    private fun createTempFile(filename: String): NSURL {
        val tempDir = NSTemporaryDirectory() as String
        val tempPath = tempDir + filename
        return NSURL.fileURLWithPath(tempPath)
    }
    
    @OptIn(ExperimentalForeignApi::class)
    fun copyFile(sourceFilePath: String, destinationURL: NSURL) {
        val fileManager = NSFileManager.defaultManager()
        val sourceURL = NSURL.fileURLWithPath(sourceFilePath)
        
        memScoped {
            val errorPtr = alloc<ObjCObjectVar<NSError?>>()
            
            if (!fileManager.copyItemAtURL(
                    sourceURL,
                    toURL = destinationURL,
                    error = errorPtr.ptr
                )
            ) {
                println("Failed to copy file: ${errorPtr}")
            } else {
                println("File copied successfully to: $destinationURL")
            }
        }
    }
    
    
    // Пример использования функции
    private fun savePickedFile(sourceFilePath: String, fileName: String) {
        saveFileInDownload(sourceFilePath, fileName)
    }
    
    actual fun getFilePath(fileName: String, fileType: String): String? {
        val directory: NSURL? = when (fileType) {
            "audio/mp4" -> NSFileManager.defaultManager.URLsForDirectory(
                NSCachesDirectory,
                NSUserDomainMask
            ).firstOrNull() as NSURL?
            "image" -> NSFileManager.defaultManager.URLsForDirectory(
                NSDocumentDirectory,
                NSUserDomainMask
            ).firstOrNull() as NSURL?
            
            "zip" -> NSURL.fileURLWithPath(NSTemporaryDirectory(), true)
            "file" -> NSFileManager.defaultManager.URLsForDirectory(
                NSDocumentDirectory,
                NSUserDomainMask
            ).firstOrNull() as NSURL?
            
            "cipher" -> NSURL.fileURLWithPath(NSTemporaryDirectory(), true)
            else -> NSFileManager.defaultManager.URLsForDirectory(
                NSDocumentDirectory,
                NSUserDomainMask
            ).firstOrNull() as NSURL?
        }
        
        if (directory == null) {
            throw IllegalStateException("Could not get directory for file type $fileType")
        }
        
        // Проверка, существует ли файл
        val existingFile = directory.URLByAppendingPathComponent(fileName)
        if (existingFile?.path?.let { NSFileManager.defaultManager.fileExistsAtPath(it) } == true) {
            println("file.absolutePath ${existingFile.path}")
            return existingFile.path
        }
        
        var file: NSURL
        do {
            file = directory.URLByAppendingPathComponent(fileName)!!
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
    
    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    actual suspend fun downloadCipherFile(
        url: String,
        contentType: String,
        filename: String,
        dirType: String,
        onProgress: (Float) -> Unit
    ): String? {
        val client = HttpClient(Darwin)
        
        println("starting decrypt")
        
        try {
            
            val token = getValueInStorage("accessToken")
            println("starting decrypt1 ${Random.nextInt(1, 10000).toString() + filename}")
            
            val fileDirectory = getFilePath(
                filename.substringBeforeLast(
                    ".",
                    filename
                ), "cipher"
            ) ?: return null
            
            val dectyptFilePath = getFilePath(
                filename,
                dirType
            ) ?: return null
            
            var filePath = ""
            
            client.prepareGet(url) { header("Authorization", "Bearer $token") }
                .execute { httpResponse ->
                    val channel: ByteReadChannel = httpResponse.body()
                    val totalBytes = httpResponse.contentLength() ?: -1L
                    val file = NSURL.fileURLWithPath(fileDirectory)
                    val outputStream = NSOutputStream.outputStreamWithURL(file, true)
                    
                    val block = httpResponse.headers["block"]
                    val authTag = httpResponse.headers["authTag"]
                    
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
                    val sharedSecret = getValueInStorage("sharedSecret")
                    
                    val cipherWrapper: CipherWrapper = KoinPlatform.getKoin().get()
                    
                    println("filenameDown ${filename}")
                    
                    
                    val result3 =
                        cipherWrapper.decupsChachaFileCommon(
                            fileDirectory,
                            dectyptFilePath,
                            block?.decodeBase64Bytes()!!,
                            authTag?.decodeBase64Bytes()!!,
                            sharedSecret?.decodeBase64Bytes()!!
                        )
                    
                    
                    if (result3 !== null) {
                        
                        println("encupsChachaFileResult $result3")
                        
                        
                        
                        if (dirType !== "audio/mp4" && dirType !== "image") {
                            savePickedFile(result3, filename)
                            
                        }
                        
                        filePath = result3
                    }
                    
                    
                    
                    outputStream?.close()
                    onProgress(1f) // Устанавливаем прогресс на 100% после завершения загрузки
                    println("Файл сохранен в ${file.path}")
                }
            
            return filePath
            
        } catch (e: Exception) {
            println("Error file $e")
            return null
        } finally {
            client.close()
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
    
    @OptIn(InternalAPI::class)
    actual suspend fun uploadCipherFile(
        url: String,
        fileDirectory: String,
        contentType: String,
        filename: String,
        onProgress: (Float) -> Unit
    ): String? {
        val client = HttpClient(Darwin) {
            install(HttpTimeout) {
                requestTimeoutMillis = 600_000
                connectTimeoutMillis = 600_000
                socketTimeoutMillis = 600_000
            }
        }
        
        val sharedSecret = getValueInStorage("sharedSecret")
        
        val cipherWrapper: CipherWrapper = KoinPlatform.getKoin().get()
        
        
        println("22222 $filename $contentType")
        
        val fileNameCipher = "cipherFile${Random.nextInt(0, 100000)}"
        
        
        val cipherFilePath = FileProviderFactory.create()
            .getFilePath(
                fileNameCipher,
                "cipher"
            )
        
        if (cipherFilePath == null) return null
        
        
        val encupsChachaFileResult = cipherWrapper.encupsChachaFileCommon(
            fileDirectory,
            cipherFilePath,
            sharedSecret?.decodeBase64Bytes()!!
        )
        println("444444")
        
        if (encupsChachaFileResult == null) {
            
            return null
        }
        println("result2 $encupsChachaFileResult")
        
        // Get the file from the provided directory
        val file = NSURL.fileURLWithPath(cipherFilePath)
        val fileData = NSData.dataWithContentsOfURL(file) ?: return null
        
        println("66666 $file")
        
        try {
            val token = getValueInStorage("accessToken")
            println("77777")
            
            val response: HttpResponse = client.post("${EnvironmentConfig.serverUrl}$url") {
                body = MultiPartFormDataContent(
                    formData {
                        append("file", fileData.toByteArray(),
                            Headers.build {
                                append(HttpHeaders.ContentType, contentType)
                                append(HttpHeaders.ContentDisposition, "filename=\"$filename\"")
                            }
                        )
                        
                        append(
                            "encupsFile",
                            Json.encodeToString(
                                EncapsulationFileResult.serializer(),
                                encupsChachaFileResult
                            )
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
            println("8888888 ${response}")
            
            if (response.status.isSuccess()) {
                val jsonElement = Json.parseToJsonElement(response.bodyAsText())
                
                println("jsonElementFile ${jsonElement}")
                
                val id = jsonElement.jsonObject["id"]?.jsonPrimitive?.content
                
                return id
                
            } else {
                println("Failed to retrieve data: ${response.status} ${response.request}")
                return null
            }
        } catch (e: Exception) {
            println("File upload failed: ${e.message}")
            return null
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
        val fileSize = getFileSize(url)
        
        
        
        return if (fileType != null) {
            FileData(fileType, fileSize)
        } else {
            null
        }
    }
    
    // Extension function to convert String to CFStringRef
    @OptIn(ExperimentalForeignApi::class)
    fun String.toCFString(): CFStringRef? {
        return CFStringCreateWithCString(null, this, kCFStringEncodingUTF8)
    }
    
    @OptIn(ExperimentalForeignApi::class)
    private fun getFileSize(url: NSURL): Long? {
        val filePath = url.path ?: return null
        val fileManager = NSFileManager.defaultManager()
        val attributes = fileManager.attributesOfItemAtPath(filePath, null) ?: return null
        val fileSize = attributes[NSFileSize] as? NSNumber
        return fileSize?.longValue
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
    
    actual fun getFileSizeFromUri(fileDirectory: String): Long? {
        val url = NSURL.fileURLWithPath(fileDirectory)
        return getFileSize(url)
    }
    
    actual suspend fun delFile(fileDirectory: String): Boolean {
        TODO("Not yet implemented")
    }
    
    actual suspend fun loadBitmapFromFile(filePath: String): ImageBitmap? {
        TODO("Not yet implemented")
    }
    
    
    actual suspend fun uploadVideoFile(
        url: String,
        videoPath: String,
        photoPath: String,
        contentType: String,
        videoName: String,
        photoName: String,
        onProgress: (Float) -> Unit
    ): List<String>? {
        val client = HttpClient() {
            install(HttpTimeout) {
                requestTimeoutMillis = 600_000
                connectTimeoutMillis = 600_000
                socketTimeoutMillis = 600_000
            }
        }
        
        val sharedSecret = getValueInStorage("sharedSecret")
        
        val cipherWrapper: CipherWrapper = KoinPlatform.getKoin().get()
        
        
        val videoNameCipher = "videoCipherFile${Random.nextInt(0, 100000)}"
        val photoNameCipher = "photoCipherFile${Random.nextInt(0, 100000)}"
        
        
        val cipherVideoPath = FileProviderFactory.create()
            .getFilePath(
                videoNameCipher,
                "cipher"
            )
        
        val cipherPhotoPath = FileProviderFactory.create()
            .getFilePath(
                photoNameCipher,
                "cipher"
            )
        
        if (cipherVideoPath == null) return null
        if (cipherPhotoPath == null) return null
        
        
        val encupsChachaVideoResult = cipherWrapper.encupsChachaFileCommon(
            videoPath,
            cipherVideoPath,
            sharedSecret?.decodeBase64Bytes()!!
        )
        
        
        val encupsChachaPhotoResult = cipherWrapper.encupsChachaFileCommon(
            photoPath,
            cipherPhotoPath,
            sharedSecret.decodeBase64Bytes()
        )
        
        if (encupsChachaVideoResult !== null && encupsChachaPhotoResult !== null) {

            
            val videoFile = NSURL.fileURLWithPath(cipherVideoPath)
            val photoFile = NSURL.fileURLWithPath(cipherPhotoPath)
            
            val videoFileData = NSData.dataWithContentsOfURL(videoFile) ?: return null
            val photoFileData = NSData.dataWithContentsOfURL(photoFile) ?: return null
            
            try {
                val token = getValueInStorage("accessToken")
                
                val response: HttpResponse = client.post("${EnvironmentConfig.serverUrl}$url") {
                    setBody(MultiPartFormDataContent(
                        formData {
 
                            formData {
                                append("videoFile", videoFileData.toByteArray(),
                                    Headers.build {
                                        append(HttpHeaders.ContentType, "mp4")
                                        append(HttpHeaders.ContentDisposition, "filename=\"$videoName\"")
                                    }
                                )
                                
                                // Дополнительные поля, такие как block и authTag
                                append(
                                    "encupsFileVideo",
                                    Json.encodeToString(
                                        EncapsulationFileResult.serializer(),
                                        encupsChachaVideoResult
                                    )
                                )
                            }
                            
                            formData {
                                append("preloadFile", photoFileData.toByteArray(),
                                    Headers.build {
                                        append(HttpHeaders.ContentType, "image")
                                        append(HttpHeaders.ContentDisposition, "filename=\"$photoName\"")
                                    }
                                )
                                
                                // Дополнительные поля, такие как block и authTag
                                append(
                                    "encupsFilePreload",
                                    Json.encodeToString(
                                        EncapsulationFileResult.serializer(),
                                        encupsChachaPhotoResult
                                    )
                                )
                            }
                        }
                    ))
                    header(HttpHeaders.Authorization, "Bearer $token")
                    
                    onUpload { bytesSentTotal, contentLength ->
                        if (contentLength != -1L) { // -1 means that the content length is unknown
                            val progress =
                                (bytesSentTotal.toDouble() / contentLength * 100).toFloat()
                            onProgress(progress)
                        }
                    }
                }
                
                
                println("11111111 ${response.status} ${response.toString()}")
                
                if (response.status.isSuccess()) {
                    println("11111111")
                    
                    val jsonElement = Json.parseToJsonElement(response.bodyAsText())
                    
                    println("jsonElementFile ${jsonElement}")
                    
                    val ids: List<String> =
                        Json.decodeFromString(jsonElement.toString())
                    
                    println("id $ids")
                    
                    
                    return ids
                } else {

//                commonViewModel.toaster.show("Filed")
                    
                    println("Failed to retrieve data: ${response.status.description} ${response.request}")
                    return null
                }
                
            } catch (e: Exception) {
//            commonViewModel.toaster.show("Filed")
                
                println("File upload failed: ${e.message}")
                return null
                
            } finally {
                
                client.close()
                
                
            }
        }
        return null
    }
    
}

actual object FileProviderFactory {
    actual fun create(): FileProvider {
        return FileProvider()
    }
}


