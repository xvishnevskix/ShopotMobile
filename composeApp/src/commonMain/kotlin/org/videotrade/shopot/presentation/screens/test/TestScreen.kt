package org.videotrade.shopot.presentation.screens.test

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.videotrade.shopot.multiplatform.AudioFactory
import org.videotrade.shopot.multiplatform.FileProviderFactory
import org.videotrade.shopot.multiplatform.PermissionsProviderFactory
import org.videotrade.shopot.multiplatform.extractAmplitudes
import org.videotrade.shopot.presentation.components.Chat.generateRandomWaveData
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.screens.common.CommonViewModel

class TestScreen : Screen {
    @Composable
    override fun Content() {
        val scope = rememberCoroutineScope()
        val commonViewModel: CommonViewModel = koinInject()
        val audioRecorder = remember { AudioFactory.createAudioRecorder() }
        val audioPlayer = remember { AudioFactory.createAudioPlayer() }
        
        var isRecording by remember { mutableStateOf(false) }
        var audioFilePath by remember { mutableStateOf("") }
        var fileId by remember { mutableStateOf("") }
        var waveData by remember { mutableStateOf<List<Float>?>(null) }
        
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
                                        val stopByte = audioRecorder.stopRecording(false)
                                        println("microphonePer $stopByte")
                                        isRecording = false
                                    } else {
                                        val audioFilePathNew = FileProviderFactory.create()
                                            .getAudioFilePath("audio_record.m4a")
                                        audioFilePath = audioFilePathNew
                                        println("audioFilePathNew $audioFilePathNew")
                                        audioRecorder.startRecording(audioFilePathNew)
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
                    
                    Button(onClick = { audioPlayer.startPlaying(audioFilePath) }) {
                        Text("Play Audio")
                    }
                    
                    Button(onClick = {
                        scope.launch {
                            val audioFile = FileProviderFactory.create()
                            val url = "https://videotradedev.ru/api/file/id/$fileId"
                            val fileName = "downloadedFile.m4a"
                            val filePath = audioFile.getAudioFilePath(fileName)
                            try {
                                println("filePath $filePath")
                                audioFile.downloadFileToDirectory(url, filePath)
                            } catch (e: Exception) {
                                println("errrrrrrr $e")
                            }
                            audioFilePath = filePath
                        }
                    }) {
                        Text("Download Audio")
                    }
                    
                    Button(onClick = {
                        scope.launch {
                            val audioDuration = audioPlayer.getAudioDuration(audioFilePath)
                            println("audioDuration $audioDuration")
                        }
                    }) {
                        Text("Get Duration Audio")
                    }
                    
                    Button(onClick = {
                        scope.launch {
                            val amplitudes = extractAmplitudes(audioFilePath)
                            waveData = amplitudes
                            println("amplitudes $amplitudes")
                        }
                    }) {
                        Text("Extract Amplitudes")
                    }
                    
                    Spacer(Modifier.height(40.dp))
                    waveData?.let { Waveform(it) }
                }
            }
        }
    }
}

@Composable
fun Waveform(waveData: List<Float>) {
    
    println("waveData $waveData")
    
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        val barWidth = size.width / (waveData.size * 2 - 1)
        val maxBarHeight = size.height
        
        waveData.forEachIndexed { index, amplitude ->
            val barHeight = maxBarHeight * amplitude
            drawRect(
                color = Color(0xFF2A293C),
                topLeft = Offset(index * 2 * barWidth, maxBarHeight / 2 - barHeight / 2),
                size = Size(barWidth, barHeight)
            )
        }
    }
}

