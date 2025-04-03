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
import platform.AVFAudio.AVAudioSessionCategoryOptionAllowBluetooth
import platform.AVFAudio.AVAudioSessionCategoryOptionDefaultToSpeaker
import platform.AVFAudio.AVAudioSessionCategoryPlayAndRecord
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.AVAudioSessionModeDefault
import platform.AVFAudio.AVAudioSessionModeVoiceChat
import platform.AVFAudio.AVAudioSessionSetActiveOptionNotifyOthersOnDeactivation
import platform.AVFAudio.AVEncoderAudioQualityKey
import platform.AVFAudio.AVFormatIDKey
import platform.AVFAudio.AVNumberOfChannelsKey
import platform.AVFAudio.AVSampleRateKey
import platform.AVFAudio.availableInputs
import platform.AVFAudio.currentRoute
import platform.AVFAudio.setActive
import platform.AVFoundation.AVAsset
import platform.CoreAudioTypes.kAudioFormatMPEG4AAC
import platform.CoreMedia.CMTimeGetSeconds
import platform.Foundation.NSError
import platform.Foundation.NSFileManager
import platform.Foundation.NSThread
import platform.Foundation.NSURL
import platform.darwin.DISPATCH_TIME_FOREVER
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
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
    
    @OptIn(ExperimentalForeignApi::class)
    actual fun startPlaying(filePath: String, isPlaying: MutableState<Boolean>): Boolean {
        println("Start playing with filePath: $filePath")
        val audioURL = NSURL.fileURLWithPath(filePath)
        
        if (!NSFileManager.defaultManager.fileExistsAtPath(filePath)) {
            println("File does not exist at path: $filePath")
            isPlaying.value = false
            return false
        }
        
        memScoped {
            val errorPtr = alloc<ObjCObjectVar<NSError?>>()
            
            try {
                val audioSession = AVAudioSession.sharedInstance()
                audioSession.setCategory(AVAudioSessionCategoryPlayback, error = errorPtr.ptr)
                if (errorPtr.value != null) {
                    println("Error setting audio session category: ${errorPtr.value?.localizedDescription}")
                    isPlaying.value = false
                    return false
                }
                
                audioSession.setActive(true, error = errorPtr.ptr)
                if (errorPtr.value != null) {
                    println("Error activating audio session: ${errorPtr.value?.localizedDescription}")
                    isPlaying.value = false
                    return false
                }
                
                println("Initializing AVAudioPlayer")
                audioPlayer = AVAudioPlayer(audioURL, errorPtr.ptr)
                
                if (errorPtr.value != null || audioPlayer == null) {
                    println("Error initializing AVAudioPlayer: ${errorPtr.value?.localizedDescription}")
                    isPlaying.value = false
                    return false
                }
                
                audioPlayer?.delegate = object : NSObject(), AVAudioPlayerDelegateProtocol {
                    override fun audioPlayerDidFinishPlaying(player: AVAudioPlayer, successfully: Boolean) {
                        println("Playback finished. Successfully: $successfully")
                        isPlaying.value = false
                    }
                    
                    override fun audioPlayerDecodeErrorDidOccur(player: AVAudioPlayer, error: NSError?) {
                        println("Playback error occurred: ${error?.localizedDescription}")
                        isPlaying.value = false
                    }
                }
                
                if (audioPlayer?.prepareToPlay() == true) {
                    if (audioPlayer?.play() == true) {
                        println("Playback started")
                        isPlaying.value = true
                    } else {
                        println("Failed to start playback")
                        isPlaying.value = false
                    }
                } else {
                    println("Failed to prepare audio player")
                    isPlaying.value = false
                }
            } catch (e: Exception) {
                println("Exception while initializing AVAudioPlayer: ${e.message}")
                isPlaying.value = false
                return false
            }
        }
        
        return true
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
    
    
    @OptIn(ExperimentalForeignApi::class)
    actual fun stopAllAudioStreams() {
        try {
            val audioSession = AVAudioSession.sharedInstance()
            
            memScoped {
                val error = alloc<ObjCObjectVar<NSError?>>()
                
                // Деактивируем текущую аудиосессию, уведомляя другие приложения
                val success = audioSession.setActive(
                    false,
                    withFlags = 1L, // Уведомить другие приложения о деактивации
                    error = error.ptr
                )
                
                if (!success) {
                    println("Ошибка при деактивации аудиосессии: ${error.value?.localizedDescription}")
                } else {
                    println("Все аудиопотоки остановлены успешно.")
                }
            }
        } catch (e: Exception) {
            println("Ошибка при остановке всех аудиопотоков: ${e.message}")
        }
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
    }
    
    //    {
//        // Получаем путь к файлу в бандле приложения
//        val filePath = NSBundle.mainBundle.pathForResource(name = musicName, ofType = "mp3")
//
//        if (filePath == null) {
//            println("Ошибка: Файл $musicName.mp3 не найден в бандле.")
//            return
//        }
//
//        val fileUrl = NSURL.fileURLWithPath(filePath)
//
//        try {
//            // Настройка аудиосессии
//            val audioSession = AVAudioSession.sharedInstance()
//
//            when (isCategoryMusic) {
//                MusicType.Notification -> {
//                    audioSession.setCategory(AVAudioSessionCategoryAmbient, error = null)
//                }
//                MusicType.Ringtone -> {
//                    audioSession.setCategory(AVAudioSessionCategorySoloAmbient, error = null)
//                }
//            }
//            audioSession.setActive(true, error = null)
//
//            // Инициализация AVAudioPlayer
//            audioPlayer = AVAudioPlayer(contentsOfURL = fileUrl, error = null).apply {
//                numberOfLoops = if (isRepeat) -1 else 0 // Устанавливаем количество повторений в зависимости от isRepeat
//                prepareToPlay()                         // Подготовка к воспроизведению
//                play()                                  // Начало воспроизведения
//            }
//
//            if (audioPlayer == null) {
//                println("Ошибка: Не удалось инициализировать AVAudioPlayer для файла $musicName.mp3.")
//            }
//        } catch (e: Exception) {
//            println("Ошибка при попытке воспроизведения: ${e.message}")
//        }
//    }
    actual fun stop() {
        audioPlayer?.stop()
        audioPlayer = null
    }
    
    actual fun isPlaying(): Boolean {
        return audioPlayer?.playing ?: false
    }
    
    
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual fun configureAudioSession() {
    val audioSession = AVAudioSession.sharedInstance()
    
    memScoped {
        val error = alloc<ObjCObjectVar<NSError?>>()
        
        // ❌ Принудительное отключение всех аудиосессий перед настройкой
        val wasDeactivated = audioSession.setActive(
            false, // Отключаем текущую сессию
            AVAudioSessionSetActiveOptionNotifyOthersOnDeactivation, // Уведомляем другие приложения
            error.ptr
        )
        
        if (!wasDeactivated) {
            println("⚠️ Ошибка деактивации предыдущей аудиосессии: ${error.value?.localizedDescription}")
        } else {
            println("✅ Все аудиосессии отключены перед настройкой")
        }
        
        // ✅ Устанавливаем категорию
        val success = audioSession.setCategory(
            AVAudioSessionCategoryPlayAndRecord,
            mode = AVAudioSessionModeVoiceChat, // Режим звонка
            options = AVAudioSessionCategoryOptionAllowBluetooth
                    or AVAudioSessionCategoryOptionDefaultToSpeaker, // Динамик по умолчанию
            error.ptr
        )
        
        if (!success) {
            println("❌ Ошибка установки категории аудиосессии: ${error.value?.localizedDescription}")
            return@memScoped
        }
        
        // 🔍 Лог текущего состояния аудиосессии перед активацией
        println("🔍 Audio session category: ${audioSession.category}")
        println("🔍 Audio session mode: ${audioSession.mode}")
        println("🔍 Audio session availableInputs: ${audioSession.availableInputs}")
        println("🔍 Audio session currentRoute: ${audioSession.currentRoute}")
        
        // ✅ Активация аудиосессии после установки параметров
        dispatch_async(dispatch_get_main_queue()) {
            val isActive = audioSession.setActive(true, error.ptr)
            if (!isActive) {
                println("❌ Ошибка активации аудиосессии: ${error.value?.localizedDescription}")
            } else {
                println("✅ Аудиосессия успешно активирована")
            }
        }
    }
}


