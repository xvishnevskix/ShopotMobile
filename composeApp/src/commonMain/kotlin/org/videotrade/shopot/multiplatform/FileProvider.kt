package org.videotrade.shopot.multiplatform

import io.github.vinceglb.filekit.core.PickerType
import org.videotrade.shopot.domain.model.FileDTO

expect class FileProvider {
    
    suspend fun pickFileAndGetAbsolutePath(pickerType: PickerType): PlatformFilePick?
    
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
        contentType: String,
        filename: String,
        onProgress: (Float) -> Unit
    ): String?
    
    fun getFileBytesForDir(fileDirectory: String): ByteArray?
    
    fun getFileData(fileDirectory: String): FileData?
    
    
    fun existingFile(fileName: String, fileType: String): String?
    
    
    suspend fun downloadCipherFile(
        url: String,
        fileDirectory: String,
        dectyptFilePath: String,
        onProgress: (Float) -> Unit
    )
    
    
}

expect object FileProviderFactory {
    fun create(): FileProvider
}


data class FileData(
    val fileName: String,
    val fileType: String,
    val fileSize: Int,
)

data class PlatformFilePick(
    val fileContentPath: String,
    val fileAbsolutePath: String
)