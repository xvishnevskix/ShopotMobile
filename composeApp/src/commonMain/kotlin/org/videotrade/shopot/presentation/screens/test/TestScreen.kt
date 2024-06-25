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
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.koin.compose.koinInject
import org.videotrade.shopot.api.EnvironmentConfig
import org.videotrade.shopot.api.getValueInStorage
import org.videotrade.shopot.domain.model.FileDTO
import org.videotrade.shopot.multiplatform.AudioFactory
import org.videotrade.shopot.multiplatform.FileProviderFactory
import org.videotrade.shopot.multiplatform.PermissionsProviderFactory
import org.videotrade.shopot.multiplatform.getHttpClientEngine
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
                                        
                                        if (stopByte !== null) {
                                            
                                            
                                            val client =
                                                HttpClient(getHttpClientEngine())
                                            try {
                                                val token = getValueInStorage("accessToken")
                                                
                                                
                                                val response: HttpResponse =
                                                    client.post("${EnvironmentConfig.serverUrl}file/upload") {
                                                        setBody(MultiPartFormDataContent(
                                                            formData {
                                                                append(
                                                                    "file",
                                                                    stopByte,
                                                                    Headers.build {
                                                                        append(
                                                                            HttpHeaders.ContentType,
                                                                            "audio/mp4"
                                                                        )
                                                                        append(
                                                                            HttpHeaders.ContentDisposition,
                                                                            "filename=\"audio\""
                                                                        )
                                                                    })
                                                            }
                                                        ))
                                                        header(
                                                            HttpHeaders.Authorization,
                                                            "Bearer $token"
                                                        )
                                                    }
                                                
                                                
                                                
                                                println("response.Send ${response.status} ${response.bodyAsText()}")
                                                
                                                if (response.status.isSuccess()) {
                                                    val responseData: FileDTO =
                                                        Json.decodeFromString(response.bodyAsText())
                                                    println("responseData ${responseData.id}")
                                                    fileId = responseData.id
                                                    
                                                } else {
                                                    println("Failed to retrieve data: ${response.status.description} ${response.request}")
                                                    
                                                }
                                            } catch (e: Exception) {
                                                
                                                println("Error111: $e")
                                                
                                                
                                            } finally {
                                                client.close()
                                            }
                                        }
                                        
                                        println("microphonePer ${stopByte}")
                                        
                                        isRecording = false
                                    } else {
                                        val audioFilePathNew = FileProviderFactory.create()
                                            .getAudioFilePath("audio_record.m4a") // Генерация пути к файлу
                                        
                                        audioFilePath = audioFilePathNew
                                        
                                        println("audioFilePathNew $audioFilePathNew")

//                                return@Button
                                        
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
                    
                    Button(
                        onClick = {
                            audioPlayer.startPlaying(audioFilePath)
                        }
                    ) {
                        Text("Play Audio")
                    }
                    
                    
                    Button(
                        onClick = {
                            scope.launch {
                                val audioFile = FileProviderFactory.create()
                                val url =
                                    "https://videotradedev.ru/api/file/id/${fileId}"
                                val fileName = "downloadedFile.mp4"
                                
                                val filePath = audioFile.getAudioFilePath(fileName)
                                
                                audioFile.downloadFileToDirectory(url, filePath)
                                
                                
                                audioFilePath = filePath
                                
                            }
                        }
                    ) {
                        Text("Download Audio")
                    }
                }
            }
        }
    }
}



