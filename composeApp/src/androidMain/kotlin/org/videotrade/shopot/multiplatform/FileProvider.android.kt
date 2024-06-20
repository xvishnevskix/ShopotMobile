package org.videotrade.shopot.multiplatform

import android.content.Context
import android.os.Environment
import java.io.File
import org.koin.java.KoinJavaComponent.inject
import kotlin.random.Random


actual class FileProvider {
    actual fun getAudioFilePath(fileName: String): String {
        val directory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
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
}

actual object FileProviderFactory {
    actual fun create(): FileProvider {
        return FileProvider()
    }
}