package org.videotrade.shopot.multiplatform

import androidx.compose.ui.graphics.ImageBitmap
import io.github.vinceglb.filekit.core.PickerType
import org.videotrade.shopot.domain.model.FileDTO

expect class FileProvider {

    suspend fun pickFile(pickerType: PickerType): PlatformFilePick?

    suspend fun pickGallery(): PlatformFilePick?
    
    fun getFilePath(fileName: String, fileType: String): String?
    
    
    fun createNewFileWithApp(fileName: String, fileType: String): String?
    
    fun saveFileInDir(fileName: String, fileDirectory: String, fileType: String): String?
    
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
    
    suspend fun uploadFileNotInput(
        url: String,
        fileDirectory: String,
        fileType: String,
        filename: String,
        onProgress: (Float) -> Unit
    ): String?
    
    
    suspend fun uploadCipherFile(
        url: String,
        fileDirectory: String,
        contentType: String,
        filename: String,
        onProgress: (Float) -> Unit
    ): String?
    
    suspend fun uploadVideoFile(
        url: String,
        videoPath: String,
        photoPath: String,
        contentType: String,
        videoName: String,
        photoName: String,
        onProgress: (Float) -> Unit
    ): List<String>?
    
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