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
import org.videotrade.shopot.data.origin
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
//package org.videotrade.shopot.presentation.screens.test
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material.ExperimentalMaterialApi
//import androidx.compose.material.pullrefresh.PullRefreshState
//import androidx.compose.material.pullrefresh.pullRefresh
//import androidx.compose.material.pullrefresh.rememberPullRefreshState
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.IntOffset
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import cafe.adriel.voyager.core.screen.Screen
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//import org.koin.compose.koinInject
//import org.videotrade.shopot.presentation.components.Common.SafeArea
//import org.videotrade.shopot.presentation.screens.common.CommonViewModel
//
//class TestScreen : Screen {
//    @OptIn(ExperimentalMaterialApi::class)
//    @Composable
//    override fun Content() {
//        val scope = rememberCoroutineScope()
//        val commonViewModel: CommonViewModel = koinInject()
//
//        var items by remember { mutableStateOf(listOf("Item 1", "Item 2", "Item 3", "Item 1", "Item 2", "Item 3", "Item 1", "Item 2", "Item 3", "Item 1", "Item 2", "Item 3", "Item 1", "Item 2", "Item 3", "Item 1", "Item 2", "Item 3", "Item 1", "Item 2", "Item 3", "Item 1", "Item 2", "Item 3")) }
//        var refreshing by remember { mutableStateOf(false) }
//
//        val refreshState = rememberPullRefreshState(
//            refreshing = refreshing,
//            onRefresh = {
//                scope.launch {
//                    // Имитируем обновление данных
//                    refreshing = true
//                    items = items + "New Item"
//                    refreshing = false
//                }
//            }
//        )
//
//        MaterialTheme {
//            SafeArea {
//                Box(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .pullRefresh(refreshState)
//                ) {
//                    LazyColumn(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .offset { IntOffset(x = 0, y = (refreshState.progress * 100).toInt()) },
//                        verticalArrangement = Arrangement.Top,
//                        horizontalAlignment = Alignment.CenterHorizontally
//                    ) {
//                        items(items) { item ->
//                            Text(
//                                text = item,
//                                fontSize = 20.sp,
//                                modifier = Modifier
//                                    .padding(16.dp)
//                                    .fillMaxWidth(),
//                                textAlign = TextAlign.Center
//                            )
//                        }
//                    }
//                    PullRefreshIndicator(refreshState, Modifier.align(Alignment.TopCenter))
//                }
//            }
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterialApi::class)
//@Composable
//fun PullRefreshIndicator(state: PullRefreshState, modifier: Modifier = Modifier) {
//    val progress = state.progress
//
//    Box(
//        contentAlignment = Alignment.Center,
//        modifier = modifier
//            .size(56.dp)
//            .padding(16.dp)
//    ) {
//        CircularProgressIndicator(
//            progress = progress
//        )
//    }
//}

