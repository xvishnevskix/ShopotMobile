package org.videotrade.shopot.multiplatform

expect class AudioRecorder {
    fun startRecording(outputFilePath: String)
    fun stopRecording(getByte: Boolean): ByteArray?
}


expect class AudioPlayer {
    fun startPlaying(filePath: String)
    fun stopPlaying()
    
    fun getAudioDuration(filePath: String): String
    
}

expect object AudioFactory {
    fun createAudioRecorder(): AudioRecorder
    fun createAudioPlayer(): AudioPlayer
    
}
