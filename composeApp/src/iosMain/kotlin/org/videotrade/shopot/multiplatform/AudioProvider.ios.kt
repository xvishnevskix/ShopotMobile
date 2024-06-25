package org.videotrade.shopot.multiplatform

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.refTo
import kotlinx.cinterop.value
import platform.AVFAudio.AVAudioPlayer
import platform.AVFAudio.AVAudioQualityHigh
import platform.AVFAudio.AVAudioRecorder
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayAndRecord
import platform.AVFAudio.AVAudioSessionModeDefault
import platform.AVFAudio.AVEncoderAudioQualityKey
import platform.AVFAudio.AVFormatIDKey
import platform.AVFAudio.AVNumberOfChannelsKey
import platform.AVFAudio.AVSampleRateKey
import platform.AVFAudio.setActive
import platform.CoreAudioTypes.kAudioFormatMPEG4AAC
import platform.Foundation.NSError
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.create
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
        println("Start recording with outputFilePath: $outputFilePath")
        outputFile = outputFilePath
        val audioFilename = NSURL.fileURLWithPath(outputFilePath)
        val fileManager = NSFileManager.defaultManager
        val fileExists = audioFilename.path?.let { fileManager.fileExistsAtPath(it) }
        
        if (fileExists == true) {
            println("File already exists: ${audioFilename.path}")
        } else {
            println("File does not exist, attempting to create: ${audioFilename.path}")
            val created = audioFilename.path?.let { fileManager.createFileAtPath(it, null, null) }
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
    }
    
    @OptIn(ExperimentalForeignApi::class)
    actual fun stopRecording(getByte: Boolean): ByteArray? {
        println("Stop recording")
        audioRecorder?.apply {
            stop()
            println("Recording stopped")
        }
        audioRecorder = null
        
        return if (getByte) {
            val file = fopen(outputFile, "rb")
            if (file != null) {
                fseek(file, 0, platform.posix.SEEK_END)
                val fileSize = ftell(file).toInt()
                rewind(file)
                val byteArray = ByteArray(fileSize)
                fread(byteArray.refTo(0), 1u, fileSize.toULong(), file)
                fclose(file)
                byteArray
            } else {
                null
            }
        } else {
            null
        }
    }
}

actual class AudioPlayer {
    private var audioPlayer: AVAudioPlayer? = null
    
    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    actual fun startPlaying(filePath: String) {
        println("Start playing with filePath: $filePath")
        val audioURL = NSURL.fileURLWithPath(filePath)
        
        memScoped {
            val errorPtr = alloc<ObjCObjectVar<NSError?>>()
            try {
                println("Initializing AVAudioPlayer")
                audioPlayer = AVAudioPlayer(audioURL, errorPtr.ptr).apply {
                    if (errorPtr.value == null) {
                        println("Successfully initialized AVAudioPlayer")
                        val prepared = prepareToPlay()
                        println("Prepared to play: $prepared")
                        val playing = play()
                        println("Playing started: $playing")
                    } else {
                        println("Error initializing AVAudioPlayer: ${errorPtr.value?.localizedDescription}")
                        audioPlayer = null // Устанавливаем в null, если произошла ошибка
                    }
                }
            } catch (e: Exception) {
                println("Exception while initializing AVAudioPlayer: ${e.message}")
                e.printStackTrace()
                audioPlayer = null
            }
        }
    }
    
    actual fun stopPlaying() {
        println("Stop playing")
        audioPlayer?.stop()
        println("Playing stopped")
        audioPlayer = null
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
}
