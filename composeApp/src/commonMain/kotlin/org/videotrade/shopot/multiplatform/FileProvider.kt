package org.videotrade.shopot.multiplatform

expect class FileProvider {
    fun getFilePath(fileName: String, fileType: String): String
    
    
    suspend fun downloadFileToDirectory(url: String, fileDirectory: String)
    
    
    fun getFileBytesForDir(fileDirectory: String): ByteArray?
    
    fun getFileData(fileDirectory: String): FileData?
    
    
    fun existingFile(fileName: String, fileType: String): String?
    
}

expect object FileProviderFactory {
    fun create(): FileProvider
}


data class FileData(
    val fileName: String,
    val fileType: String
)