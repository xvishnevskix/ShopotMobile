package org.videotrade.shopot.multiplatform

expect class AudioRecorder {
    fun startRecording(outputFilePath: String)
    fun stopRecording(getDir: Boolean): String?
}


expect class AudioPlayer {
    fun startPlaying(filePath: String)
    fun stopPlaying()
    
    fun getAudioDuration(filePath: String, fileName: String): String?
    
}


expect class MusicPlayer {
    fun play()
    fun stop()
    fun isPlaying(): Boolean
    
}

expect object AudioFactory {
    fun createAudioRecorder(): AudioRecorder
    fun createAudioPlayer(): AudioPlayer
    
}
