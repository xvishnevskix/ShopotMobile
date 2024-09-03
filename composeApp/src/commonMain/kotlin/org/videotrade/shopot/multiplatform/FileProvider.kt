package org.videotrade.shopot.multiplatform

import androidx.compose.ui.graphics.ImageBitmap
import io.github.vinceglb.filekit.core.PickerType
import org.videotrade.shopot.domain.model.FileDTO

expect class FileProvider {
    
    suspend fun pickFile(pickerType: PickerType): PlatformFilePick?
    
    fun getFilePath(fileName: String, fileType: String): String?
    
    suspend fun downloadFileToDirectory(
        url: String,
        fileDirectory: String,
        onProgress: (Float) -> Unit
    )
    suspend fun downloadCipherFile(
        url: String,
        contentType: String,
        filename: String,
        dirType: String,
        onProgress: (Float) -> Unit
    ): String?
    
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
    fun getFileSizeFromUri(fileDirectory: String): Long?
    suspend fun delFile(fileDirectory: String): Boolean
    
    suspend fun loadBitmapFromFile(filePath: String): ImageBitmap?
}

expect object FileProviderFactory {
    fun create(): FileProvider
}


data class FileData(
//    val fileName: String,
    val fileType: String,
    val fileSize: Long?,
//    var fileSize: Long,
)

data class PlatformFilePick(
    val fileContentPath: String,
    val fileAbsolutePath: String,
    val fileSize: Long?,
    val fileName: String,
)