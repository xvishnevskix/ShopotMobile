package org.videotrade.shopot.presentation.screens.test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.videotrade.shopot.api.getValueInStorage
import org.videotrade.shopot.multiplatform.AudioFactory
import org.videotrade.shopot.multiplatform.FileProviderFactory
import org.videotrade.shopot.multiplatform.PermissionsProviderFactory
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.settings.SettingsViewModel
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
        var audioFileName by remember { mutableStateOf("") }
        var fileId by remember { mutableStateOf("") }

        val settingsViewModel: SettingsViewModel = koinInject()

        val isScreenDimmed = remember { mutableStateOf(false) }
        val sad = settingsViewModel.isScreenDimmed.value
        val sharedSecret = getValueInStorage("sharedSecret")

        
        MaterialTheme {
            SafeArea {
                Column(
                    Modifier.fillMaxSize()
                        .background(if (sad) Color.Black else Color.Yellow)

                ) {
                    Button(
                        onClick = {
                            
                            scope.launch {
                                val microphonePer =
                                    PermissionsProviderFactory.create().getPermission("microphone")
                                
                                println("microphonePer $microphonePer")
                                if (microphonePer) {
                                    if (isRecording) {
                                        val stopByte = audioRecorder.stopRecording(true)
                                        
                                        if (stopByte !== null) {
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
//                                            audioFilePath = audioFilePathNew
                                            
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
                            color = Color.Black
                        )
                    }
                    
                    Button(
                        onClick = {
//                            audioPlayer.startPlaying(audioFilePath, isPlaying)
                                
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
