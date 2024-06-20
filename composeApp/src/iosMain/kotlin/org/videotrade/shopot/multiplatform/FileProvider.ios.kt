package org.videotrade.shopot.multiplatform

import platform.Foundation.*

actual class FileProvider {
    actual fun getAudioFilePath(fileName: String): String {
        val fileManager = NSFileManager.defaultManager()
        val urls = fileManager.URLsForDirectory(NSCachesDirectory, NSUserDomainMask)
        val documentDirectory = urls.first() as NSURL
        
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
}

actual object FileProviderFactory {
    actual fun create(): FileProvider {
        return FileProvider()
    }
}
