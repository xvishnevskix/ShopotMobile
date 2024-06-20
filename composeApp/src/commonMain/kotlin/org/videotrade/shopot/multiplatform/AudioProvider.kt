package org.videotrade.shopot.multiplatform

expect class AudioRecorder {
    fun startRecording(outputFilePath: String)
    fun stopRecording()
}


expect class AudioPlayer {
    fun startPlaying(filePath: String)
    fun stopPlaying()
}

expect object AudioFactory {
    fun createAudioRecorder(): AudioRecorder
    fun createAudioPlayer(): AudioPlayer
    
}
