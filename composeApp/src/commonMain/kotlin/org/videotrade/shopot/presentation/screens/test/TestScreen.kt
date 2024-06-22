//package org.videotrade.shopot.presentation.screens.test
//
//import androidx.compose.foundation.layout.Column
//import androidx.compose.material3.Button
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.graphics.Color
//import cafe.adriel.voyager.core.screen.Screen
//import kotlinx.coroutines.launch
//import org.koin.compose.koinInject
//import org.videotrade.shopot.multiplatform.AudioFactory
//import org.videotrade.shopot.multiplatform.FileProviderFactory
//import org.videotrade.shopot.multiplatform.PermissionsProviderFactory
//import org.videotrade.shopot.presentation.components.Common.SafeArea
//import org.videotrade.shopot.presentation.screens.common.CommonViewModel
//
//class TestScreen : Screen {
//    @Composable
//    override fun Content() {
//        val scope = rememberCoroutineScope()
//        val commonViewModel: CommonViewModel = koinInject()
//        val audioRecorder = remember { AudioFactory.createAudioRecorder() }
//        val audioPlayer = remember { AudioFactory.createAudioPlayer() }
//
//        var isRecording by remember { mutableStateOf(false) }
//        var audioFilePath by remember { mutableStateOf("") }
//
//
//
//        MaterialTheme {
//            SafeArea {
//                Column {
//                    Button(
//                        onClick = {
//
//                            scope.launch {
//                                val microphonePer =
//                                    PermissionsProviderFactory.create().getPermission("microphone")
//
//                                println("microphonePer ${microphonePer}")
//                                if (microphonePer) {
//                                    if (isRecording) {
//                                        audioRecorder.stopRecording()
//                                        isRecording = false
//                                    } else {
//                                        val audioFilePathNew = FileProviderFactory.create()
//                                            .getAudioFilePath("audio_record.m4a") // Генерация пути к файлу
//
//                                        audioFilePath = audioFilePathNew
//
//                                        println("audioFilePathNew $audioFilePathNew")
//
////                                return@Button
//
//                                        audioRecorder.startRecording(audioFilePath)
//                                        isRecording = true
//                                    }
//                                }
//                            }
//                        }
//                    ) {
//                        Text(
//                            if (isRecording) "Stop Recording" else "Start Recording",
//                            color = Color.White
//                        )
//                    }
//
//                    Button(
//                        onClick = {
//                            audioPlayer.startPlaying(audioFilePath)
//                        }
//                    ) {
//                        Text("Play Audio")
//                    }
//                }
//            }
//        }
//    }
//}
package org.videotrade.shopot.presentation.screens.test

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.videotrade.shopot.multiplatform.AudioFactory
import org.videotrade.shopot.multiplatform.FileProviderFactory
import org.videotrade.shopot.multiplatform.PermissionsProviderFactory
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.chat_micro_active
import shopot.composeapp.generated.resources.chat_microphone
import kotlin.math.roundToInt

class TestScreen : Screen {
    @Composable
    override fun Content() {
        val scope = rememberCoroutineScope()
        val commonViewModel: CommonViewModel = koinInject()
        
        
        MaterialTheme {
            SafeArea {
                DraggableText()
            }
        }
    }
}

@Composable
fun DraggableText() {
    var offset by remember { mutableStateOf(Offset.Zero) }
    var isRecording by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { /* не нужно устанавливать isLongPressed */ },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        // Перетаскивание только влево
                        if (dragAmount.x < 0) {
                            offset = Offset(
                                x = offset.x + dragAmount.x,
                                y = offset.y
                            )
                        }
                    }
                )
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isRecording = !isRecording
                    }
                )
            }
    ) {
        val sizeModifier = if (isRecording) {
            Modifier.size(width = 65.dp, height = 60.dp)
        } else {
            Modifier.size(width = 16.dp, height = 26.dp)
        }
        
        Image(
            modifier = sizeModifier.offset {
                IntOffset(
                    offset.x.roundToInt(),
                    offset.y.roundToInt()
                )
            },
            painter = if (!isRecording) painterResource(Res.drawable.chat_microphone) else painterResource(
                Res.drawable.chat_micro_active
            ),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
    }
}