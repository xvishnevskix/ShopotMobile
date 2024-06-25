package org.videotrade.shopot.multiplatform

import android.media.MediaPlayer
import android.media.MediaRecorder
import java.io.File
import java.io.FileInputStream

actual class AudioRecorder {
    private var mediaRecorder: MediaRecorder? = null
    private var outputFile: String = ""
    
    actual fun startRecording(outputFilePath: String) {
        outputFile = outputFilePath
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(outputFile)
            prepare()
            start()
        }
    }
    
    actual fun stopRecording(getByte: Boolean): ByteArray? {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
        
        return if (getByte) {
            val file = File(outputFile)
            val fileSize = file.length().toInt()
            val byteArray = ByteArray(fileSize)
            val fis = FileInputStream(file)
            fis.read(byteArray)
            fis.close()
            byteArray
        } else {
            null
        }
    }
}

actual class AudioPlayer {
    private var mediaPlayer: MediaPlayer? = null
    
    actual fun startPlaying(filePath: String) {
        mediaPlayer = MediaPlayer().apply {
            setDataSource(filePath)
            prepare()
            start()
        }
    }
    
    actual fun stopPlaying() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}

actual object AudioFactory {
    actual fun createAudioRecorder(): AudioRecorder {
        return AudioRecorder()
    }
    
    actual fun createAudioPlayer(): AudioPlayer {
        return AudioPlayer()
    }
}
