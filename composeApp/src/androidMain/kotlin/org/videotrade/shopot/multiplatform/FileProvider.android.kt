package org.videotrade.shopot.multiplatform

import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.ContactsContract.Directory
import androidx.annotation.RequiresApi
import io.ktor.client.HttpClient
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.get
import io.ktor.client.statement.HttpStatement
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.HttpHeaders
import io.ktor.utils.io.jvm.javaio.copyTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import org.koin.java.KoinJavaComponent.inject
import java.nio.file.Files
import java.nio.file.Paths
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
    
    @RequiresApi(Build.VERSION_CODES.O)
    actual suspend fun downloadFileToDirectory(url: String, fileDirectory: String) {
        
        
        println("outputPath $fileDirectory")
        
        downloadFile(url, fileDirectory)
        
    }
}


actual object FileProviderFactory {
    actual fun create(): FileProvider {
        return FileProvider()
    }
}


// Общая функция для загрузки файла
@RequiresApi(Build.VERSION_CODES.O)
suspend fun downloadFile(url: String, outputPath: String) {
    val client = HttpClient()
    try {
        val response = client.get(url) {
        }.bodyAsChannel()
        
        
        println("response ${response.totalBytesRead}")
        withContext(Dispatchers.IO) {
            val path = Paths.get(outputPath)
            Files.newOutputStream(path).use { fileOutputStream ->
                response.copyTo(fileOutputStream)
            }
        }
    } finally {
        client.close()
    }
}