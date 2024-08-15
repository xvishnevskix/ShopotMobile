package org.videotrade.shopot.presentation.screens.test

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.screen.Screen
import dev.icerock.moko.mvvm.compose.getViewModel
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.videotrade.shopot.multiplatform.AudioFactory
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
        

        MaterialTheme {
            SafeArea {
                Column {
                    Button(
                        onClick = {
                            scope.launch {
                                val microphonePer =
                                    PermissionsProviderFactory.create().getPermission("microphone")
                                
                                println("microphonePer ${microphonePer}")
                                if (microphonePer) {
                                    if (isRecording) {
                                        audioRecorder.stopRecording(true)
                                        isRecording = false
                                    } else {
                                        val audioFilePathNew = FileProviderFactory.create()
                                            .getFilePath(
                                                "audio_record${
                                                    Random.nextInt(
                                                        1,
                                                        501
                                                    )
                                                }.m4a", ""
                                            ) // Генерация пути к файлу
                                        
                                        if (audioFilePathNew != null) {
                                            audioFilePath = audioFilePathNew
                                        }
                                        
                                        println("audioFilePathNew $audioFilePathNew")

//                                return@Button
                                        
                                        audioRecorder.startRecording(audioFilePath)
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
                            audioPlayer.startPlaying(audioFilePath)
                        }
                    ) {
                        Text("Play Audio")
                    }
                }
            }
        }
    }
}


