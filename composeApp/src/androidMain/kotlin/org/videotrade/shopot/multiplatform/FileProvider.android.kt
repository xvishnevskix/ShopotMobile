package org.videotrade.shopot.multiplatform

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.jvm.javaio.copyTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.random.Random


actual class FileProvider(private val applicationContext: Context) {
    actual fun getAudioFilePath(fileName: String): String {
        // Используем каталог кэша приложения
        val directory = applicationContext.cacheDir
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
        val client = HttpClient()
        try {
            val response = client.get(url) {
            }.bodyAsChannel()
            
            
            println("response ${response.totalBytesRead}")
            withContext(Dispatchers.IO) {
                val path = Paths.get(fileDirectory)
                Files.newOutputStream(path).use { fileOutputStream ->
                    response.copyTo(fileOutputStream)
                }
            }
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


