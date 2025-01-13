package org.videotrade.shopot.multiplatform

import androidx.compose.runtime.MutableState
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.refTo
import kotlinx.cinterop.value
import platform.AVFAudio.AVAudioPlayer
import platform.AVFAudio.AVAudioPlayerDelegateProtocol
import platform.AVFAudio.AVAudioQualityHigh
import platform.AVFAudio.AVAudioRecorder
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryAmbient
import platform.AVFAudio.AVAudioSessionCategoryPlayAndRecord
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.AVAudioSessionCategorySoloAmbient
import platform.AVFAudio.AVAudioSessionModeDefault
import platform.AVFAudio.AVEncoderAudioQualityKey
import platform.AVFAudio.AVFormatIDKey
import platform.AVFAudio.AVNumberOfChannelsKey
import platform.AVFAudio.AVSampleRateKey
import platform.AVFAudio.setActive
import platform.AVFoundation.AVAsset
import platform.CoreAudioTypes.kAudioFormatMPEG4AAC
import platform.CoreMedia.CMTimeGetSeconds
import platform.Foundation.NSBundle
import platform.Foundation.NSError
import platform.Foundation.NSFileManager
import platform.Foundation.NSThread
import platform.Foundation.NSURL
import platform.darwin.DISPATCH_TIME_FOREVER
import platform.darwin.NSObject
import platform.darwin.dispatch_semaphore_create
import platform.darwin.dispatch_semaphore_signal
import platform.darwin.dispatch_semaphore_wait
import platform.posix.SEEK_END
import platform.posix.fclose
import platform.posix.fopen
import platform.posix.fread
import platform.posix.fseek
import platform.posix.ftell
import platform.posix.rewind

actual class AudioRecorder {
    private var audioRecorder: AVAudioRecorder? = null
    private var outputFile: String = ""
    
    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    actual fun startRecording(outputFilePath: String) {
        try {
            println("Start recording with outputFilePath: $outputFilePath")
            outputFile = outputFilePath
            val audioFilename = NSURL.fileURLWithPath(outputFilePath)
            val fileManager = NSFileManager.defaultManager
            val fileExists = audioFilename.path?.let { fileManager.fileExistsAtPath(it) }
            
            if (fileExists == true) {
                println("File already exists: ${audioFilename.path}")
            } else {
                println("File does not exist, attempting to create: ${audioFilename.path}")
                val created =
                    audioFilename.path?.let { fileManager.createFileAtPath(it, null, null) }
                println("File creation successful: $created")
            }
            
            val settings = mapOf<Any?, Any?>(
                AVFormatIDKey to kAudioFormatMPEG4AAC,
                AVSampleRateKey to 12000.0,
                AVNumberOfChannelsKey to 1,
                AVEncoderAudioQualityKey to AVAudioQualityHigh
            )
            
            memScoped {
                val errorPtr = alloc<ObjCObjectVar<NSError?>>()
                try {
                    // Настройка AVAudioSession
                    val audioSession = AVAudioSession.sharedInstance()
                    audioSession.setCategory(AVAudioSessionCategoryPlayAndRecord, errorPtr.ptr)
                    audioSession.setMode(AVAudioSessionModeDefault, errorPtr.ptr)
                    audioSession.setActive(true, errorPtr.ptr)
                    
                    println("Initializing AVAudioRecorder with settings: $settings $audioFilename ${errorPtr.ptr}")
                    audioRecorder = AVAudioRecorder(audioFilename, settings, errorPtr.ptr)
                    println("AVAudioRecorder instance created: $audioRecorder")
                    
                    if (errorPtr.value != null) {
                        println("Error pointer is not null: ${errorPtr.value?.localizedDescription}")
                    } else {
                        println("Error pointer is null, proceeding to prepare and record")
                        audioRecorder?.apply {
                            val prepared = prepareToRecord()
                            println("Prepared to record: $prepared")
                            if (prepared) {
                                val recording = record()
                                println("Recording started: $recording")
                                if (!recording) {
                                    println("Recording failed to start")
                                }
                            } else {
                                println("Failed to prepare for recording")
                            }
                        }
                    }
                } catch (e: Exception) {
                    println("Exception while initializing AVAudioRecorder: $e")
                    e.printStackTrace()
                    audioRecorder = null
                }
            }
        } catch (e: Exception) {
            println("error: $e")
        }
    }
    
    @OptIn(ExperimentalForeignApi::class)
    actual fun stopRecording(getDir: Boolean): String? {
        try {
            println("Stop recording")
            audioRecorder?.apply {
                stop()
                println("Recording stopped")
                
                // Добавляем задержку для завершения записи
                NSThread.sleepForTimeInterval(0.5)
            }
            audioRecorder = null
            
            return if (getDir) {
                val file = fopen(outputFile, "rb")
                if (file != null) {
                    fseek(file, 0, SEEK_END)
                    val fileSize = ftell(file).toInt()
                    rewind(file)
                    val byteArray = ByteArray(fileSize)
                    fread(byteArray.refTo(0), 1u, fileSize.toULong(), file)
                    fclose(file)
                    outputFile // Возвращаем абсолютный путь к файлу
                } else {
                    null
                }
            } else {
                val file = NSFileManager.defaultManager.removeItemAtPath(outputFile, null)
                outputFile // Возвращаем абсолютный путь к файлу
            }
        } catch (e: Exception) {
            println("error: $e")
            
            return null
        }
    }
}

actual class AudioPlayer {
    private var audioPlayer: AVAudioPlayer? = null
    
    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    actual fun startPlaying(filePath: String, isPlaying: MutableState<Boolean>): Boolean
    {
        println("Start playing with filePath: $filePath")
        val audioURL = NSURL.fileURLWithPath(filePath)
        
        memScoped {
            val errorPtr = alloc<ObjCObjectVar<NSError?>>()
            println("Allocated error pointer")
            
            try {
                println("Initializing AVAudioPlayer")
                
                // Настройка аудиосессии для использования основного динамика
                val audioSession = AVAudioSession.sharedInstance()
                println("Retrieved shared audio session instance")
                
                audioSession.setCategory(AVAudioSessionCategoryPlayback, error = errorPtr.ptr)
                println("Set audio session category to Playback")
                
                audioSession.setActive(true, error = errorPtr.ptr)
                println("Activated audio session")
                
                if (errorPtr.value != null) {
                    println("Error setting audio session: ${errorPtr.value?.localizedDescription}")
                    return@memScoped
                }
                
                val player = AVAudioPlayer(audioURL, errorPtr.ptr)
                println("Initialized AVAudioPlayer with audioURL")
                
                if (errorPtr.value != null) {
                    println("Error initializing AVAudioPlayer: ${errorPtr.value?.localizedDescription}")
                    return@memScoped
                }
                
                // Устанавливаем делегат
                println("Setting AVAudioPlayer delegate")
                player.delegate = object : NSObject(), AVAudioPlayerDelegateProtocol {
                    override fun audioPlayerDidFinishPlaying(player: AVAudioPlayer, successfully: Boolean) {
                        println("Playback finished. Successfully: $successfully")
                        isPlaying.value = false // Обновляем состояние
                    }
                    
                    override fun audioPlayerDecodeErrorDidOccur(player: AVAudioPlayer, error: NSError?) {
                        println("Playback error: ${error?.localizedDescription}")
                        isPlaying.value = false // Обновляем состояние
                    }
                }
                
                println("Setting up audioPlayer")
                audioPlayer = player.apply {
                    println("Preparing to play audio")
                    if (prepareToPlay()) {
                        println("Audio prepared successfully")
                        if (play()) {
                            println("Audio playback started successfully")
                            isPlaying.value = true // Устанавливаем состояние "воспроизведение начато"
                            return true
                        } else {
                            println("Failed to start playing audio")
                            isPlaying.value = false
                            return false
                        }
                    } else {
                        println("Failed to prepare audio for playback")
                        isPlaying.value = false
                    }
                }
            } catch (e: Exception) {
                println("Exception while initializing AVAudioPlayer: ${e.message}")
                e.printStackTrace()
                isPlaying.value = false
                audioPlayer = null
                return false
            }
        }
        println("Exiting startPlaying function")
        return false
    }
    
    actual fun stopPlaying() {
        println("Stop playing")
        audioPlayer?.stop()
        println("Playing stopped")
        audioPlayer = null
    }
    
    @OptIn(ExperimentalForeignApi::class)
    actual fun getAudioDuration(filePath: String, fileName: String): String? {
        val url = NSURL.fileURLWithPath(filePath)
        val asset = AVAsset.assetWithURL(url)
        
        // Подождем пока asset будет загружен
        val semaphore = dispatch_semaphore_create(0)
        var duration: Long = 0L
        
        asset.loadValuesAsynchronouslyForKeys(listOf("duration")) {
            memScoped {
                val errorPtr = alloc<ObjCObjectVar<NSError?>>()
                if (asset.statusOfValueForKey("duration", errorPtr.ptr) == AVKeyValueStatusLoaded) {
                    val durationCMTime = asset.duration
                    duration = (CMTimeGetSeconds(durationCMTime) * 1000).toLong()
                }
                dispatch_semaphore_signal(semaphore)
            }
        }
        
        // Ждем пока asset загрузится
        dispatch_semaphore_wait(semaphore, DISPATCH_TIME_FOREVER)
        
        val totalSeconds = duration / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "${if (minutes < 10) "0" else ""}$minutes:${if (seconds < 10) "0" else ""}$seconds"
    }
    
    companion object {
        private const val AVKeyValueStatusLoaded: Long = 2
    }
}

actual object AudioFactory {
    actual fun createAudioRecorder(): AudioRecorder {
        println("Creating AudioRecorder")
        return AudioRecorder()
    }
    
    actual fun createAudioPlayer(): AudioPlayer {
        println("Creating AudioPlayer")
        return AudioPlayer()
    }
    
    actual fun createMusicPlayer(): MusicPlayer {
        return MusicPlayer()
    }
}

actual class MusicPlayer {
    
    private var audioPlayer: AVAudioPlayer? = null
    
    @OptIn(ExperimentalForeignApi::class)
    actual fun play(musicName: String, isRepeat: Boolean, isCategoryMusic: MusicType) {
        // Получаем путь к файлу в бандле приложения
        val filePath = NSBundle.mainBundle.pathForResource(name = musicName, ofType = "mp3")
        
        if (filePath == null) {
            println("Ошибка: Файл $musicName.mp3 не найден в бандле.")
            return
        }
        
        val fileUrl = NSURL.fileURLWithPath(filePath)
        
        try {
            // Настройка аудиосессии
            val audioSession = AVAudioSession.sharedInstance()
            
            when (isCategoryMusic) {
                MusicType.Notification -> {
                    audioSession.setCategory(AVAudioSessionCategoryAmbient, error = null)
                }
                MusicType.Ringtone -> {
                    audioSession.setCategory(AVAudioSessionCategorySoloAmbient, error = null)
                }
            }
            audioSession.setActive(true, error = null)
            
            // Инициализация AVAudioPlayer
            audioPlayer = AVAudioPlayer(contentsOfURL = fileUrl, error = null).apply {
                numberOfLoops = if (isRepeat) -1 else 0 // Устанавливаем количество повторений в зависимости от isRepeat
                prepareToPlay()                         // Подготовка к воспроизведению
                play()                                  // Начало воспроизведения
            }
            
            if (audioPlayer == null) {
                println("Ошибка: Не удалось инициализировать AVAudioPlayer для файла $musicName.mp3.")
            }
        } catch (e: Exception) {
            println("Ошибка при попытке воспроизведения: ${e.message}")
        }
    }
    actual fun stop() {
        audioPlayer?.stop()
        audioPlayer = null
    }
    
    actual fun isPlaying(): Boolean {
        return audioPlayer?.playing ?: false
    }
}