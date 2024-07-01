package org.videotrade.shopot.multiplatform

expect class FileProvider {
    fun getAudioFilePath(fileName: String): String
    
    
    suspend fun downloadFileToDirectory(url: String, fileDirectory: String)
    
    
    fun getFileBytesForDir(fileDirectory: String): ByteArray?
    
    fun getFileType(fileDirectory: String): String?
    
    
    
    
}

expect object FileProviderFactory {
    fun create(): FileProvider
}