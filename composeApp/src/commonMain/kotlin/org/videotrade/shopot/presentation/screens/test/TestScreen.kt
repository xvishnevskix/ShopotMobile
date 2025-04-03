package org.videotrade.shopot.presentation.screens.test

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.screen.Screen
import io.ktor.util.decodeBase64Bytes
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.mp.KoinPlatform
import org.videotrade.shopot.api.getValueInStorage
import org.videotrade.shopot.multiplatform.AudioFactory
import org.videotrade.shopot.multiplatform.CipherWrapper
import org.videotrade.shopot.multiplatform.EncapsulationFileResult
import org.videotrade.shopot.multiplatform.FileProviderFactory
import org.videotrade.shopot.multiplatform.PermissionsProviderFactory
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import kotlin.random.Random


class TestScreen : Screen {
    @Composable
    override fun Content() {
        val scope = rememberCoroutineScope()
        val commonViewModel: CommonViewModel = koinInject()
        val audioRecorder = remember { AudioFactory.createAudioRecorder() }
        val audioPlayer = remember { AudioFactory.createAudioPlayer() }
        
        var isRecording by remember { mutableStateOf(false) }
        var audioFilePath by remember { mutableStateOf("") }
        var audioCipherFilePath by remember { mutableStateOf("") }
        var audioFileName by remember { mutableStateOf("") }
        var fileId by remember { mutableStateOf("") }
        var cipherFileResult by remember { mutableStateOf<EncapsulationFileResult?>(null) }
        val cipherWrapper: CipherWrapper =
            KoinPlatform.getKoin().get()
        var isPlaying = remember() { mutableStateOf(false) }
        
        val sharedSecret = getValueInStorage("sharedSecret")
        
        
        MaterialTheme {
            SafeArea {
                Column {
                    Button(
                        onClick = {
                            
                            scope.launch {
                                val microphonePer =
                                    PermissionsProviderFactory.create().getPermission("microphone")
                                
                                println("microphonePer $microphonePer")
                                if (microphonePer) {
                                    if (isRecording) {
                                        val stopByte = audioRecorder.stopRecording(true)
                                        
//                                        if (stopByte !== null) {
//                                            val fileNameCipher =
//                                                "cipherFile${Random.nextInt(0, 100000)}"
//
//
//                                            val cipherFilePath = FileProviderFactory.create()
//                                                .createNewFileWithApp(
//                                                    fileNameCipher,
//                                                    "cipher"
//                                                )
//
//
//                                            if (cipherFilePath != null) {
//                                                println("audioCipherFilePath $audioCipherFilePath")
//                                                audioCipherFilePath = cipherFilePath
//                                            }
//
//                                            cipherFileResult = cipherFilePath?.let {
//                                                cipherWrapper.encupsChachaFileCommon(
//                                                    stopByte,
//                                                    it,
//                                                    sharedSecret?.decodeBase64Bytes()!!
//                                                )
//                                            }
//                                        }
                                        if (stopByte != null) {
                                            audioFilePath = stopByte
                                        }
                                        println("microphonePer ${stopByte}")
                                        
                                        isRecording = false
                                    } else {
                                        val fileName = "${Random.nextInt(1, 10000)}audio_record.m4a"
                                        val audioFilePathNew = FileProviderFactory.create()
                                            .createNewFileWithApp(
                                                fileName,
                                                "audio/mp4"
                                            ) // Генерация пути к файлу
                                        
                                        
                                        audioFileName = fileName
                                        
                                        if (audioFilePathNew != null) {
                                            audioFilePath = audioFilePathNew
                                            
                                            audioRecorder.startRecording(audioFilePathNew)
                                            
                                            
                                        }
                                        println("audioFilePathNew $audioFilePathNew")
                                        
                                        isRecording = true
                                    }
                                }
                            }
                        }
                    ) {
                        Text(
                            if (isRecording) "Stop Recording" else "Start Recording",
                            color = Color.White
                        )
                    }
                    
                    Button(
                        onClick = {
                            val fileName = "${Random.nextInt(1, 10000)}audio_record.m4a"
                            
//                            val audioFileNew = FileProviderFactory.create()
//                                .createNewFileWithApp(
//                                    fileName,
//                                    "audio/mp4"
//                                )
//                            if (cipherFileResult !== null) {
//                                val result3 =
//                                    audioFileNew?.let {
//                                        cipherWrapper.decupsChachaFileCommon(
//                                            audioCipherFilePath,
//                                            it,
//                                            cipherFileResult!!.block,
//                                            cipherFileResult!!.authTag,
//                                            sharedSecret?.decodeBase64Bytes()!!
//                                        )
//                                    }
//
//                                println("result3 $result3")
//
//                                if (result3 != null) {
//                                    audioPlayer.startPlaying(audioFilePath, isPlaying)
//                                }
//
//                            }
                            
                            audioPlayer.startPlaying(audioFilePath, isPlaying)
                            
                        }
                    ) {
                        Text("Play Audio")
                    }
                    
                    Button(
                        onClick = {
                            scope.launch {
                                val audioRecorderaa =
                                    audioPlayer.getAudioDuration(audioFilePath, audioFileName)
                                
                                
                                println("audioRecorder $audioRecorderaa")
                                
                            }
                        }
                    ) {
                        Text("getDurr Audio")
                    }
                }
            }
        }
    }
}


