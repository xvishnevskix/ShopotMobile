package org.videotrade.shopot.multiplatform

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.core.readBytes
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.IO
import platform.Foundation.*
import platform.posix.memcpy

actual class FileProvider {
    
    actual fun getAudioFilePath(fileName: String): String {
        val fileManager = NSFileManager.defaultManager()
        val urls = fileManager.URLsForDirectory(NSDocumentDirectory, NSUserDomainMask)
        val documentDirectory = urls.first() as NSURL
        
        // Создайте новую папку в директории документов
        val newFolderName = "MyAudioFiles"
        val newFolderURL = documentDirectory.URLByAppendingPathComponent(newFolderName)
        val newFolderPath = newFolderURL!!.path
        
        if (!newFolderPath?.let { fileManager.fileExistsAtPath(it) }) {
            try {
                val attributes = emptyMap<Any?, Any>() // Пустой атрибуты
                fileManager.createDirectoryAtPath(newFolderPath, true, attributes)
                println("Folder created at path: $newFolderPath")
            } catch (e: Exception) {
                println("Error creating folder: ${e}")
            }
        } else {
            println("Folder already exists at path: $newFolderPath")
        }
        
        // Создайте уникальный файл внутри новой папки
        var fileURL: NSURL
        do {
            val randomSuffix = (0..100000).random()
            val newFileName =
                "${fileName.substringBeforeLast(".")}_$randomSuffix.${fileName.substringAfterLast(".")}"
            fileURL = newFolderURL.URLByAppendingPathComponent(newFileName)!!
        } while (fileManager.fileExistsAtPath(fileURL.path!!))
        
        println("fileURL.path ${fileURL.path}")
        
        return fileURL.path!!
    }

    
    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun downloadFileToDirectory(url: String, fileDirectory: String) {
        val client = HttpClient(Darwin)
        try {
            val response = client.get(url).bodyAsChannel()
            
            withContext(Dispatchers.IO) {
                val fileManager = NSFileManager.defaultManager()
                val data = NSMutableData()
                
                while (!response.isClosedForRead) {
                    val packet = response.readRemaining(8192)
                    
                    println("packet.readBytes() $packet")
                    
                    packet.readBytes().usePinned { pinned ->
                        data.appendBytes(pinned.addressOf(0), packet.remaining.toULong())
                    }
                }
                
                fileManager.createFileAtPath(fileDirectory, data, null)
            }
        } finally {
            client.close()
        }
    }
}

actual object FileProviderFactory {
    actual fun create(): FileProvider {
        return FileProvider()
    }
}


