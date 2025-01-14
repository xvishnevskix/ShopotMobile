package org.videotrade.shopot.multiplatform

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.AudioManager.ADJUST_UNMUTE
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.compose.runtime.MutableState
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
            setAudioSource(MediaRecorder.AudioSource.MIC) // Используем микрофон
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4) // Формат файла MPEG-4
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC) // Кодек AAC (широкая поддержка)
            setAudioEncodingBitRate(128000) // Битрейт 128 kbps
            setAudioSamplingRate(44100) // Частота дискретизации 44.1 kHz
            setOutputFile(outputFile) // Путь к файлу
            prepare() // Подготовка
            start() // Начало записи
        }

    }
    
    actual fun stopRecording(getDir: Boolean): String? {
        try {
            
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            
            return if (getDir) {
                val file = File(outputFile)
                println("outputFile $outputFile")
                if (!file.exists()) return null
                println("11111")
                
                val fileSize = file.length().toInt()
                println("22222")
                
                val byteArray = ByteArray(fileSize)
                println("33333")
                
                val fis = FileInputStream(file)
                println("44444")
                
                fis.read(byteArray)
                println("55555")
                
                fis.close()
                println("66666")
                
                unmuteAllAudioSources(getContextObj.getContext())
                println("7777")
                
                outputFile // Return the absolute path to the file
            } else {
                val file = File(outputFile)
                if (!file.exists()) return null
                file.delete()
                unmuteAllAudioSources(getContextObj.getContext())
                
                return null
            }
        } catch (e: Exception) {
            println("error $e")
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
    
    private fun unmuteAllAudioSources(context: Context) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (hasDndPermission(context)) {
            audioManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, ADJUST_UNMUTE, 0)
            audioManager.adjustStreamVolume(AudioManager.STREAM_ALARM, ADJUST_UNMUTE, 0)
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, ADJUST_UNMUTE, 0)
            audioManager.adjustStreamVolume(AudioManager.STREAM_RING, ADJUST_UNMUTE, 0)
            audioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, ADJUST_UNMUTE, 0)
        }
    }
    
    private fun hasDndPermission(context: Context): Boolean {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return notificationManager.isNotificationPolicyAccessGranted
    }
}

actual class AudioPlayer(private val applicationContext: Context) {
    private var mediaPlayer: MediaPlayer? = null
    
    actual fun startPlaying(filePath: String, isPlaying: MutableState<Boolean>): Boolean {
        try {
            // Получение экземпляра AudioManager
            val audioManager =
                applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            
            // Установка аудиосессии для воспроизведения через основной динамик
            audioManager.mode = AudioManager.MODE_NORMAL
            audioManager.isSpeakerphoneOn = true
            println("filePath $filePath")
            
            // Создание и настройка MediaPlayer
            mediaPlayer = MediaPlayer().apply {
                setDataSource(filePath)
                prepare()
                start()
                
                // Установка слушателя завершения воспроизведения
                setOnCompletionListener {
                    println("Playback completed")
                    isPlaying.value = false
                    mediaPlayer = null
                    // Здесь можно выполнить дополнительные действия, например:
                    // - Уведомить пользователя
                    // - Очистить ресурсы
                    // - Начать воспроизведение следующего трека
                }
            }
            
            return true
            
        } catch (e: Exception) {
            println("error $e")
            return false
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
            
            // Получение длительности в миллисекундах и добавление 500 мс
            val milliseconds = (durationStr?.toLongOrNull() ?: 0L) + 500
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
    
    
    actual fun createMusicPlayer(): MusicPlayer {
        return MusicPlayer()
    }
}


actual class MusicPlayer {
    
    private var mediaPlayer: MediaPlayer? = null
    
    actual fun play(musicName: String, isRepeat: Boolean, isCategoryMusic: MusicType) {
        try {
            val context = getContextObj.getContext()
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            
            // Запрашиваем аудио фокус для системного звука
            val focusRequest = audioManager.requestAudioFocus(
                { focusChange ->
                    if (isRepeat) {
                        when (focusChange) {
                            AudioManager.AUDIOFOCUS_LOSS -> {
                                mediaPlayer?.pause()
                            }
                            
                            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                                mediaPlayer?.pause()
                            }
                            
                            AudioManager.AUDIOFOCUS_GAIN -> {
                                mediaPlayer?.start()
                            }
                        }
                    } else {
                        mediaPlayer?.start()
                    }
                    
                    
                },
                AudioManager.STREAM_ALARM, // Используем системный поток для уведомлений/сигналов
                AudioManager.AUDIOFOCUS_GAIN
            )
            
            if (focusRequest == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                if (mediaPlayer == null) {
                    val assetManager = context.assets
                    val inputStream = assetManager.open("${musicName}.mp3")
                    
                    val tempFile = File.createTempFile("system_sound", ".mp3", context.cacheDir)
                    val outputStream = FileOutputStream(tempFile)
                    inputStream.copyTo(outputStream)
                    inputStream.close()
                    outputStream.close()
                    
                    mediaPlayer = MediaPlayer().apply {
                        setDataSource(tempFile.absolutePath)
                        
                        
                        val audioAttributes = when (isCategoryMusic) {
                            MusicType.Notification -> {
                                AudioAttributes.Builder()
                                    .setUsage(AudioAttributes.USAGE_NOTIFICATION) // Устанавливаем использование для уведомлений
                                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION) // Тип контента для системных звуков
                                    .build()
                            }
                            
                            MusicType.Ringtone -> AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE) // Устанавливаем использование для мелодии звонка
                                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION) // Тип контента для звуков оповещений и звонков
                                .build()
                            
                            else -> AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_NOTIFICATION) // Устанавливаем использование для уведомлений
                                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION) // Тип контента для системных звуков
                                .build()
                        }
                        
                        
                        setAudioAttributes(audioAttributes) // Устанавливаем аудио атрибуты
                        isLooping =
                            isRepeat // Устанавливаем цикличное воспроизведение в зависимости от параметра isRepeat
                        prepare()
                        start()
                    }
                    
                } else {
                    mediaPlayer?.start()
                }
            } else {
                println("Не удалось получить аудио фокус.")
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
    
    fun stopMediaPlayer() {
        mediaPlayer?.stop()
        
    }
    
    // Проверка, воспроизводится ли музыка
    actual fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }
}
