package org.videotrade.shopot.multiplatform

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import io.github.vinceglb.filekit.core.FileKit
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
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
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.koin.mp.KoinPlatform
import org.videotrade.shopot.androidSpecificApi.getContextObj
import org.videotrade.shopot.api.EnvironmentConfig
import org.videotrade.shopot.api.getValueInStorage
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.random.Random


actual class FileProvider(private val applicationContext: Context) {
    
    private val cipherWrapper: CipherWrapper = KoinPlatform.getKoin().get()
    
    private val context = getContextObj.getContext()
    
    
    actual suspend fun pickFile(pickerType: PickerType): PlatformFilePick? {
        try {
            val filePick = FileKit.pickFile(
                type = pickerType,
                mode = PickerMode.Single,
            )
            
            var filePathNew = ""
            
            if (filePick?.uri !== null) {
                
                runBlocking {
                    val file = getFileFromUri(getContextObj.getContext(), filePick.uri)
                    filePathNew = file.absoluteFile.toString()
                }
                
                return PlatformFilePick(
                    filePick.uri.toString(),
                    filePathNew,
                    filePick.getSize(),
                    filePick.name
                )
                
            }
            return null
        } catch (e: Exception) {
            return null
        }
    }
    
    
    actual suspend fun pickGallery(): PlatformFilePick? {
        val imageAndVideoType = PickerType.ImageAndVideo
        
        
        try {
            val filePick = FileKit.pickFile(
                type = imageAndVideoType,
                mode = PickerMode.Single,
            )
            
            println("filePick $filePick")
            var filePathNew = ""
            
            if (filePick?.uri !== null) {
                
                runBlocking {
                    val file = getFileFromUri(getContextObj.getContext(), filePick.uri)
                    filePathNew = file.absoluteFile.toString()
                }
                
                return PlatformFilePick(
                    filePick.uri.toString(),
                    filePathNew,
                    filePick.getSize(),
                    filePick.name
                )
                
            }
            return null
        } catch (e: Exception) {
            return null
        }
    }
    
    
    actual fun getFilePath(fileName: String, fileType: String): String? {
        println("111111111")
        
        // Используем каталог кэша приложения
        val directory = when (fileType) {
            "audio/mp4" -> applicationContext.cacheDir
//            "audio/mp4" -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            "image" -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            "zip" -> applicationContext.cacheDir
            "file" -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            "cipher" -> applicationContext.cacheDir
            "cache" -> applicationContext.cacheDir
            else -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        }
        println("2222222")
        
        if (!directory.exists()) {
            directory.mkdirs()
        }
        println("333333")
        
        // Проверка, существует ли файл
        val existingFile = File(directory, fileName)
        if (existingFile.exists()) {
            println("file.absolutePath ${existingFile.absolutePath}")
            return null
        }
        
        var file: File
        do {
            file = File(directory, fileName)
            
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
    
    
    actual suspend fun downloadCipherFile(
        url: String,
        contentType: String,
        filename: String,
        dirType: String,
        onProgress: (Float) -> Unit
    ): String? {
        val client = HttpClient()
        
        println("starting decrypt")
        
        try {
            val token = getValueInStorage("accessToken")
                ?: throw IllegalStateException("Access token is missing")
            println("starting decrypt1 ${Random.nextInt(1, 10000).toString() + filename}")
            
            val fileDirectory = getFilePath(
                filename.substringBeforeLast(".", filename),
                "cipher"
            ) ?: return null
            println("1111111")
            
            val decryptFilePath = getFilePath(filename, dirType) ?: return null
            println("222222")
            
            println("decryptFilePath $decryptFilePath")
            
            var filePath = ""
            
            client.prepareGet(url) { header("Authorization", "Bearer $token") }
                .execute { httpResponse ->
                    
                    println("httpResponse")
                    
                    val block = httpResponse.headers["block"]?.decodeBase64Bytes()
                    val authTag = httpResponse.headers["authTag"]?.decodeBase64Bytes()
                    val channel: ByteReadChannel = httpResponse.body()
                    val totalBytes = httpResponse.contentLength() ?: -1L
                    
                    println("totalBytes $totalBytes")
                    
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
                    
                    val sharedSecret = getValueInStorage("sharedSecret")?.decodeBase64Bytes()
                    val cipherWrapper: CipherWrapper = KoinPlatform.getKoin().get()
                    
                    println("filenameDown $filename")
                    
                    // Проверяем, что block, authTag и sharedSecret не равны null
                    if (block != null && authTag != null && sharedSecret != null) {
                        val result3 = cipherWrapper.decupsChachaFileCommon(
                            fileDirectory,
                            decryptFilePath,
                            block,
                            authTag,
                            sharedSecret
                        )
                        
                        if (result3 != null) {
                            file.delete()
                            println("encupsChachaFileResult $result3")
                            
                            filePath = result3
                        }
                    } else {
                        println("Decryption parameters are missing: block=$block, authTag=$authTag, sharedSecret=$sharedSecret")
                    }
                    
                    onProgress(1f) // Устанавливаем прогресс на 100% после завершения загрузки
                    println("A file saved to ${file.path}")
                }
            
            return filePath
            
        } catch (e: Exception) {
            println("Error file ${e.message}")
            e.printStackTrace()
        } finally {
            client.close()
        }
        return null
    }
    
    
    actual suspend fun uploadCipherFile(
        url: String,
        fileDirectory: String,
        contentType: String,
        filename: String,
        onProgress: (Float) -> Unit
    ): String? {
        println("111111111313123123131")
        val commonViewModel: CommonViewModel = KoinPlatform.getKoin().get()
        
        val client = HttpClient() {
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
        
        val file = File(cipherFilePath)
        if (!file.exists()) {
            println("File not found: ${file.absolutePath}")
            return null
        }
        println("11111111 ${file.inputStream().asInput()}")
        
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
            
            
            println("11111111 ${response.status} ${response.toString()}")
            
            if (response.status.isSuccess()) {
                println("11111111")
                
                val jsonElement = Json.parseToJsonElement(response.bodyAsText())
                
                println("jsonElementFile ${jsonElement}")
                
                val id = jsonElement.jsonObject["id"]?.jsonPrimitive?.content
                
                println("id $id")
                
                file.delete()

//                val fileCopy = File(fileDirectory)
//
//                fileCopy.delete()
                
                return id
                
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
        
        return null
        
    }
    
    actual fun getFileBytesForDir(fileDirectory: String): ByteArray? {
        val uri = Uri.parse(fileDirectory)
        return readBytesFromUri(applicationContext, uri)
    }
    
    
    @RequiresApi(Build.VERSION_CODES.Q)
    actual fun getFileData(fileDirectory: String): FileData? {
        println("uri $fileDirectory")
        val uri = Uri.parse(fileDirectory)
        
        println("uri $uri")
        
        return getData(applicationContext, uri, fileDirectory)
    }
    
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getData(context: Context, uri: Uri, fileDirectory: String): FileData? {
        val contentResolver = context.contentResolver
        
        
        // Определяем тип файла
        val mimeType = contentResolver.getType(uri)
        
        val fileType = mimeType?.substringAfter("application/") ?: run {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            MimeTypeMap.getSingleton()
                .getMimeTypeFromExtension(fileExtension?.lowercase(Locale.ROOT))
                ?.substringAfter("application/")
        }

//        // Получаем имя файла из URI
//        val fileName = getFileNameFromUri(contentResolver, uri)
//
//        // Получаем размер файла из URI
        val fileSize = getFileSizeFromUri(fileDirectory)
        
        return if (fileType != null) {
            FileData(fileType, fileSize)
        } else {
            null
        }
    }
    
    actual fun getFileSizeFromUri(fileDirectory: String): Long? {
        
        val contentResolver = applicationContext.contentResolver
        
        val uri = Uri.parse(fileDirectory)
        
        val cursor = contentResolver.query(uri, null, null, null, null)
        return cursor?.use {
            val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
            if (sizeIndex != -1 && it.moveToFirst()) {
                it.getLong(sizeIndex)
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
//            "audio/mp4" -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            "image" -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            "video" -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            else -> applicationContext.cacheDir
        } ?: return null
        
        return findFileInDirectory(directory, fileName, fileType)
    }
    
    
    private fun findFileInDirectory(directory: File, fileName: String, fileType: String): String? {
        
        println("findFileInDirectory3121 ${directory} $fileName")
        val file = File(directory, fileName)
        println("findFileInDirectory ${file.exists()} $file")
        return if (file.exists()) {
            file.absolutePath
        } else {
            null
        }
    }
    
    
    actual fun createNewFileWithApp(fileName: String, fileType: String): String? {
        
        // Определяем каталог для хранения файла в зависимости от типа файла
        val directory = when (fileType) {
            "audio" -> File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), "Audio")
            "video" -> File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), "Video")
            "image" -> File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Images")
            "document" -> File(
                context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
                "Documents"
            )
            
            "zip" -> File(context.cacheDir, "Zips")
            "cipher" -> File(context.cacheDir, "CipherFiles")
            "cache" -> File(context.cacheDir, "CacheFiles")
            else -> File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "Others")
        }
        
        // Создаем папку, если она не существует
        if (!directory.exists()) {
            directory.mkdirs()
        }
        
        // Проверка, существует ли файл с таким именем
        val existingFile = File(directory, fileName)
        if (existingFile.exists()) {
            println("Файл уже существует: ${existingFile.absolutePath}")
            return null
        }
        
        val file = File(directory, fileName)
        println("Путь к файлу: ${file.absolutePath}")
        
        return file.absolutePath
    }
    
    actual fun saveFileInDir(fileName: String, fileDirectory: String, fileType: String): String? {
        val sourceFile = File(fileDirectory)
        
        // Проверяем, существует ли исходный файл
        if (!sourceFile.exists()) {
            println("Исходный файл не найден: $fileDirectory")
            return null
        }
        
        // Определяем каталог для сохранения файла в зависимости от типа файла
        val directory = when (fileType) {
            "audio" -> File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), "Audio")
            "video" -> File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), "Video")
            "image" -> File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Images")
            "document" -> File(
                context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
                "Documents"
            )
            
            "zip" -> File(context.cacheDir, "Zips")
            "cipher" -> File(context.cacheDir, "CipherFiles")
            "cache" -> File(context.cacheDir, "CacheFiles")
            else -> File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "Others")
        }
        
        // Создаем каталог, если он не существует
        if (!directory.exists()) {
            directory.mkdirs()
        }
        
        // Определяем путь для нового файла
        val destinationFile = File(directory, fileName)
        
        return try {
            // Копируем файл в нужный каталог
            sourceFile.copyTo(destinationFile, overwrite = true)
            println("Файл успешно сохранен: ${destinationFile.absolutePath}")
            destinationFile.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
    
    
    actual fun existingFileInDir(fileName: String, fileType: String): String? {
        // Определяем каталог для поиска файла в зависимости от типа файла
        val directory = when (fileType) {
            "audio" -> File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), "Audio")
            "video" -> File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), "Video")
            "image" -> File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Images")
            "document" -> File(
                context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
                "Documents"
            )
            
            "zip" -> File(context.cacheDir, "Zips")
            "cipher" -> File(context.cacheDir, "CipherFiles")
            "cache" -> File(context.cacheDir, "CacheFiles")
            else -> File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "Others")
        }
        
        // Проверяем, существует ли файл в указанной директории
        val file = File(directory, fileName)
        return if (file.exists()) {
            println("Файл найден: ${file.absolutePath}")
            file.absolutePath
        } else {
            println("Файл не найден: $fileName в каталоге $directory")
            null
        }
    }
    
    
    actual suspend fun uploadFileNotInput(
        url: String,
        fileDirectory: String,
        fileType: String,
        filename: String,
        onProgress: (Float) -> Unit
    ): String? {
        val uri = Uri.parse(fileDirectory)
        println("Parsed URI: $uri")
        
        val client = HttpClient() {
            install(HttpTimeout) {
                requestTimeoutMillis = 600_000
                connectTimeoutMillis = 600_000
                socketTimeoutMillis = 600_000
            }
        }
        
        
        try {
            val token = getValueInStorage("accessToken")
            
            val sharedSecret = getValueInStorage("sharedSecret")
            
            val fileNameCipher = "cipherFile${Random.nextInt(0, 100000)}"
            
            val cipherFilePath = createNewFileWithApp(
                fileNameCipher,
                "cipher"
            ) ?: return null
            
            val encupsChachaFileResult = cipherWrapper.encupsChachaFileCommon(
                fileDirectory,
                cipherFilePath,
                sharedSecret?.decodeBase64Bytes()!!
            ) ?: return null
            
            val cipherFile = File(cipherFilePath)
            
            
            val response: HttpResponse = client.post("${EnvironmentConfig.serverUrl}$url") {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append(
                                "file",
                                cipherFile.readBytes(),
                                Headers.build {
                                    append(HttpHeaders.ContentType, fileType)
                                    append(
                                        HttpHeaders.ContentDisposition,
                                        "filename=\"${filename}\""
                                    )
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
                )
                
                
                
                onUpload { bytesSentTotal, contentLength ->
                    if (contentLength != -1L) { // -1 means that the content length is unknown
                        val progress = (bytesSentTotal.toDouble() / contentLength * 100).toFloat()
                        onProgress(progress)
                    }
                }
                header(HttpHeaders.Authorization, "Bearer $token")
            }
            
            if (response.status.isSuccess()) {
                val jsonElement = Json.parseToJsonElement(response.bodyAsText())
                
                println("jsonElementFile ${jsonElement}")
                
                val id = jsonElement.jsonObject["id"]?.jsonPrimitive?.content
                
                saveFileInDir(filename, fileDirectory, fileType)
                
                cipherFile.delete()
                
                return id
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
    
    actual suspend fun delFile(fileDirectory: String): Boolean {
        try {
            val uri = Uri.parse(fileDirectory)
            
            val file = getFileFromUri(applicationContext, uri)
            
            
            return file.delete()
        } catch (e: Exception) {
            println("error delFile: $e")
            return false
        }
    }
    
    actual suspend fun loadBitmapFromFile(filePath: String): ImageBitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(filePath)
                if (file.exists()) {
                    val bitmap = BitmapFactory.decodeStream(FileInputStream(file))
                    bitmap?.asImageBitmap()
                } else {
                    null
                }
            } catch (e: Exception) {
                println("Error loading image from file: ${e.message}")
                null
            }
        }
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
            
            val videoFile = File(cipherVideoPath)
            val photoFile = File(cipherPhotoPath)
            
            if (!videoFile.exists() && !photoFile.exists()) {
                return null
            }
            
            try {
                val token = getValueInStorage("accessToken")
                
                val response: HttpResponse = client.post("${EnvironmentConfig.serverUrl}$url") {
                    setBody(MultiPartFormDataContent(
                        formData {
                            // Первый файл
                            append(
                                "videoFile",
                                InputProvider(videoFile.length()) {
                                    videoFile.inputStream().asInput()
                                },
                                Headers.build {
                                    append(HttpHeaders.ContentType, "mp4")
                                    append(
                                        HttpHeaders.ContentDisposition,
                                        "filename=\"${videoName}\""
                                    )
                                }
                            )
                            
                            // Второй файл
                            append(
                                "preloadFile",
                                InputProvider(photoFile.length()) {
                                    photoFile.inputStream().asInput()
                                },
                                Headers.build {
                                    append(HttpHeaders.ContentType, "image")
                                    append(
                                        HttpHeaders.ContentDisposition,
                                        "filename=\"${photoName}\""
                                    )
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
                            
                            append(
                                "encupsFilePreload",
                                Json.encodeToString(
                                    EncapsulationFileResult.serializer(),
                                    encupsChachaPhotoResult
                                )
                            )
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
                    
                    videoFile.delete()
                    photoFile.delete()
                    
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


