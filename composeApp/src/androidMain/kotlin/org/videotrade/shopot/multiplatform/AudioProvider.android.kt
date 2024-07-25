package org.videotrade.shopot.multiplatform

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioManager
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.media.MediaRecorder
import org.junit.runner.manipulation.Ordering
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
    
    actual fun stopRecording(getDir: Boolean): String? {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
        
        return if (getDir) {
            val file = File(outputFile)
            val fileSize = file.length().toInt()
            val byteArray = ByteArray(fileSize)
            val fis = FileInputStream(file)
            fis.read(byteArray)
            fis.close()
            outputFile // Возвращаем абсолютный путь к файлу
        } else {
            null
        }
    }
}

actual class AudioPlayer(private val applicationContext: Context) {
    private var mediaPlayer: MediaPlayer? = null
    
    actual fun startPlaying(filePath: String) {
        // Получение экземпляра AudioManager
        val audioManager =
            applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        // Установка аудиосессии для воспроизведения через основной динамик
        audioManager.mode = AudioManager.MODE_NORMAL
        audioManager.isSpeakerphoneOn = true
        println("filePath $filePath")
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
    
    private lateinit var applicationContext: Context
    
    fun initialize(context: Context) {
        applicationContext = context
    }
    
    actual fun createAudioRecorder(): AudioRecorder {
        return AudioRecorder()
    }
    
    actual fun createAudioPlayer(): AudioPlayer {
        return AudioPlayer(applicationContext)
    }
}
