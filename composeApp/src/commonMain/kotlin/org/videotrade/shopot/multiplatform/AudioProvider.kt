package org.videotrade.shopot.multiplatform

import androidx.compose.runtime.MutableState

expect class AudioRecorder {
    fun startRecording(outputFilePath: String)
    fun stopRecording(getDir: Boolean): String?
}


expect class AudioPlayer {
    fun startPlaying(filePath: String, isPlaying: MutableState<Boolean>): Boolean
    fun stopPlaying()
    
    fun getAudioDuration(filePath: String, fileName: String): String?
    
}


expect class MusicPlayer {
    fun play(musicName: String, isRepeat: Boolean, isCategoryMusic: MusicType)
    fun stop()
    fun isPlaying(): Boolean
}

expect object AudioFactory {
    fun createAudioRecorder(): AudioRecorder
    fun createAudioPlayer(): AudioPlayer
    fun createMusicPlayer(): MusicPlayer
    
}


enum class MusicType {
    Notification,
    Ringtone
}


expect fun configureAudioSession()
