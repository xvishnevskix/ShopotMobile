package org.videotrade.shopot.multiplatform


import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.request.get
import io.ktor.client.statement.HttpStatement
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.readBytes
import io.ktor.utils.io.core.readBytes
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.toCValues
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import platform.Foundation.NSData
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSMutableData
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.URLByAppendingPathComponent
import platform.Foundation.appendBytes
import platform.Foundation.create

actual class FileProvider {
    
    
    actual fun getAudioFilePath(fileName: String): String {
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
    
    
    //    @OptIn(ExperimentalForeignApi::class)
//    actual suspend fun downloadFileToDirectory(url: String, fileDirectory: String) {
//        val client = HttpClient(Darwin)
//        try {
//            val response = client.get(url).bodyAsChannel()
//            val httpStatement = client.get(url)
//            withContext(Dispatchers.IO) {
//                val fileManager = NSFileManager.defaultManager()
//                val data = NSMutableData()
//
//                while (!response.isClosedForRead) {
//                    val packet = response.readRemaining(100000)
//
//
//                    packet.readBytes().usePinned { pinned ->
//                        data.appendBytes(pinned.addressOf(0), packet.remaining.toULong())
//                    }
//                }
//                println("packet.data $data")
//
//                fileManager.createFileAtPath(fileDirectory, data, null)
//            }
//        } finally {
//            client.close()
//        }
//    }
//}
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

}

actual object FileProviderFactory {
    actual fun create(): FileProvider {
        return FileProvider()
    }
}


