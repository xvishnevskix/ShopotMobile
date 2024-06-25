package org.videotrade.shopot.multiplatform

import android.annotation.SuppressLint
import android.media.MediaMetadataRetriever
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
    
    @SuppressLint("DefaultLocale")
    actual fun getAudioDuration(filePath: String): String {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(filePath)
        val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        retriever.release()
        
        val milliseconds = durationStr?.toLongOrNull() ?: 0L
        val totalSeconds = milliseconds / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
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
