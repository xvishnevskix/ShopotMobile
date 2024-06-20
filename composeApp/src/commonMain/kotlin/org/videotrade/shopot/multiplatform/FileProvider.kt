package org.videotrade.shopot.multiplatform

expect class FileProvider {
    fun getAudioFilePath(fileName: String): String
    
}

expect object FileProviderFactory {
    fun create(): FileProvider
}