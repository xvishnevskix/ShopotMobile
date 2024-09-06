package org.videotrade.shopot.multiplatform

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioManager
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.media.MediaRecorder
import org.videotrade.shopot.androidSpecificApi.getContextObj
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

actual class AudioRecorder(private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null
    private var outputFile: String = ""
    
    actual fun startRecording(outputFilePath: String) {
        // Stop any existing recording
        stopAllAudioSources()
        
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
            println("outputFile $outputFile")
            if (!file.exists()) return null
            
            val fileSize = file.length().toInt()
            val byteArray = ByteArray(fileSize)
            val fis = FileInputStream(file)
            fis.read(byteArray)
            fis.close()
            unmuteAllAudioSources()
            outputFile // Return the absolute path to the file
        } else {
            val file = File(outputFile)
            if (!file.exists()) return null
            file.delete()
            unmuteAllAudioSources()
            
            return null
        }
    }
    
    private fun stopAllAudioSources() {
        // Release the current MediaRecorder if it is still active
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
        
        // Optionally, mute all audio output
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true)
        audioManager.setStreamMute(AudioManager.STREAM_ALARM, true)
        audioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true)
        audioManager.setStreamMute(AudioManager.STREAM_RING, true)
        audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true)
    }
    
    fun unmuteAllAudioSources() {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false)
        audioManager.setStreamMute(AudioManager.STREAM_ALARM, false)
        audioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false)
        audioManager.setStreamMute(AudioManager.STREAM_RING, false)
        audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, false)
    }
}

actual class AudioPlayer(private val applicationContext: Context) {
    private var mediaPlayer: MediaPlayer? = null
    
    actual fun startPlaying(filePath: String) {
        try {
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
        } catch (e: Exception) {
            println("error $e")
        }
    }
    
    actual fun stopPlaying() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
    
    @SuppressLint("DefaultLocale")
    actual fun getAudioDuration(filePath: String, fileName: String): String? {
        try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(filePath)
            val durationStr =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            retriever.release()
            
            val milliseconds = durationStr?.toLongOrNull() ?: 0L
            val totalSeconds = milliseconds / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return String.format("%02d:%02d", minutes, seconds)
        } catch (e: Exception) {
            return null
        }
    }
}

actual object AudioFactory {
    
    private lateinit var applicationContext: Context
    
    fun initialize(context: Context) {
        applicationContext = context
    }
    
    actual fun createAudioRecorder(): AudioRecorder {
        return AudioRecorder(applicationContext)
    }
    
    actual fun createAudioPlayer(): AudioPlayer {
        return AudioPlayer(applicationContext)
    }
}


actual class MusicPlayer {
    
    private var mediaPlayer: MediaPlayer? = null
    
    // Метод для воспроизведения музыки
    actual fun play(musicName: String) {
        try {
            val context = getContextObj.getContext()
            
            if (mediaPlayer == null) {
                // Получаем AssetManager для доступа к ресурсам assets
                val assetManager = context.assets
                
                // Открываем файл из assets
                val inputStream = assetManager.open("${musicName}.mp3")
                
                // Создаем временный файл для проигрывания (т.к. MediaPlayer не работает напрямую с InputStream)
                val tempFile = File.createTempFile("music", ".mp3", context.cacheDir)
                val outputStream = FileOutputStream(tempFile)
                inputStream.copyTo(outputStream)
                inputStream.close()
                outputStream.close()
                
                // Инициализация MediaPlayer с временным файлом
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(tempFile.absolutePath)
                    isLooping = true // Устанавливаем цикличное проигрывание
                    prepare() // Подготовка MediaPlayer
                    start() // Начинаем воспроизведение
                }
            } else {
                mediaPlayer?.start() // Продолжаем проигрывание, если оно было приостановлено
            }
        } catch (e: Exception) {
        println("e: $e")
        }
    }
    
    // Метод для остановки воспроизведения музыки
    actual fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.reset()
        mediaPlayer?.release()
        mediaPlayer = null
    }
    
    // Проверка, воспроизводится ли музыка
    actual fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }
}
