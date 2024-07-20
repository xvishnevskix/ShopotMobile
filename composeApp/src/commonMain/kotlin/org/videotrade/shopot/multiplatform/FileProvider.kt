package org.videotrade.shopot.multiplatform

import com.eygraber.uri.Uri
import org.videotrade.shopot.domain.model.FileDTO

expect class FileProvider {
    fun getFilePath(fileName: String, fileType: String): String
    
    suspend fun downloadFileToDirectory(
        url: String,
        fileDirectory: String,
        onProgress: (Float) -> Unit
    )
    
    suspend fun uploadFileToDirectory(
        url: String,
        fileDirectory: String,
        contentType: String,
        filename: String,
        onProgress: (Float) -> Unit
    ): FileDTO?
    
    
    suspend fun uploadCipherFile(
        url: String,
        fileDirectory: String,
        cipherFilePath: String,
        contentType: String,
        filename: String,
        onProgress: (Float) -> Unit
    ): FileDTO?
    
    fun getFileBytesForDir(fileDirectory: String): ByteArray?
    
    fun getFileData(fileDirectory: String): FileData?
    
    
    fun existingFile(fileName: String, fileType: String): String?
    
    
}

expect object FileProviderFactory {
    fun create(): FileProvider
}


data class FileData(
    val fileName: String,
    val fileType: String,
    val fileSize: Int,
)