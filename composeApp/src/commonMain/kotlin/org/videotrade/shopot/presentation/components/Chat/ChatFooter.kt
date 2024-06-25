package org.videotrade.shopot.presentation.components.Chat

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.multiplatform.AudioFactory
import org.videotrade.shopot.multiplatform.FileProviderFactory
import org.videotrade.shopot.multiplatform.PermissionsProviderFactory
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.chat_arrow_left
import shopot.composeapp.generated.resources.chat_micro_active
import shopot.composeapp.generated.resources.chat_microphone
import kotlin.math.roundToInt

@Composable
fun ChatFooter(chat: ChatItem, viewModel: ChatViewModel) {
    val scope = rememberCoroutineScope()
    
    var text by remember { mutableStateOf("") }
    
    var isRecording by remember { mutableStateOf(false) }
    var recordingTime by remember { mutableStateOf(0) }
    val swipeOffset = remember { Animatable(0f) }
    var isDragging by remember { mutableStateOf(false) }
    val audioRecorder = remember { AudioFactory.createAudioRecorder() }
    var isStartRecording by remember { mutableStateOf(false) }
    
    
    LaunchedEffect(isRecording) {
        if (isRecording) {
            while (isRecording) {
                delay(1000L)
                
                recordingTime++
                
                var seconds = recordingTime
                println("Senddddd ${seconds} ")
                seconds++
                
                if (seconds > 2) {
                    if (!isStartRecording) {
                        scope.launch {
                            println("isStartRecording")
                            
                            val microphonePer =
                                PermissionsProviderFactory.create().getPermission("microphone")
                            if (microphonePer) {
                                val audioFilePathNew = FileProviderFactory.create()
                                    .getAudioFilePath("audio_record.m4a") // Генерация пути к файлу

                                println("audioFilePathNew $audioFilePathNew")
                                
                                audioRecorder.startRecording(audioFilePathNew)
                                isStartRecording = true
                            }
                        }
                    }
                    
                }
            }
        } else {
            recordingTime = 0
        }
    }
    
    LaunchedEffect(swipeOffset.value) {
        println("swipeOffset ${swipeOffset}")
    }
    
    val infiniteTransition = rememberInfiniteTransition()
    val recordingCircleAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    val textOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    val singleImagePicker = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Single,
        scope = scope,
        onResult = { byteArrays ->
            byteArrays.firstOrNull()?.let {
                scope.launch {
                    viewModel.sendAttachments(
                        content = text,
                        fromUser = viewModel.profile.value.id,
                        chatId = chat.id,
                        it,
                        "image",
                        "jpg"
                    )
                }
            }
        }
    )
    
    Box(
        modifier = Modifier
            .imePadding()
            .padding(vertical = 15.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .fillMaxWidth()
                .heightIn(max = 125.dp, min = 58.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFF3F4F6))
        
        ) {
            if (!isRecording) {
                Box(
                    modifier = Modifier
                        .padding(start = 15.dp, end = 15.dp)
                        .size(37.dp)
                        .background(color = Color(0xFF2A293C), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        tint = Color.White,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable {
                                singleImagePicker.launch()
                            }
                    )
                }
                
                BasicTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier
                        .padding(end = 8.dp, top = 5.dp, bottom = 5.dp)
                        .weight(1f)
                        .padding(3.dp),
                    textStyle = TextStyle(
                        color = Color.Black,
                        fontSize = 16.sp
                    ),
                    cursorBrush = SolidColor(Color.Black),
                    visualTransformation = VisualTransformation.None,
                    decorationBox = { innerTextField ->
                        Box {
                            if (text.isEmpty()) {
                                Text(
                                    "Написать...",
                                    textAlign = TextAlign.Center,
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                                    letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                                    lineHeight = 20.sp,
                                    color = Color(0xFF979797),
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            } else {
                Row(
                    modifier = Modifier.padding(start = 15.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(
                                Color.Red.copy(alpha = recordingCircleAlpha),
                                shape = CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(15.dp))
                    val hours = recordingTime / 3600
                    val minutes = (recordingTime % 3600) / 60
                    val seconds = recordingTime % 60
                    Text(
                        text = "$hours:${minutes.toString().padStart(2, '0')}:${
                            seconds.toString().padStart(2, '0')
                        }",
                        fontSize = 13.sp,
                        fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                        letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                        lineHeight = 20.sp,
                        color = Color(0xFF979797),
                    )
                }
                
                Row(
                    modifier = Modifier
                        .padding(start = 50.dp)
                        .fillMaxWidth(0.45f)
                        .offset(x = (textOffset + swipeOffset.value).dp)
                        .alpha(1f + (swipeOffset.value / 100f))
                        .animateContentSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        modifier = Modifier
                            .padding(end = 3.dp)
                            .size(width = 7.dp, height = 14.dp),
                        painter = painterResource(Res.drawable.chat_arrow_left),
                        contentDescription = null,
                    )
                    Text(
                        text = "Влево - отмена",
                        fontSize = 13.sp,
                        fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                        letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                        lineHeight = 20.sp,
                        color = Color(0xFF979797),
                        modifier = Modifier
                    )
                }
            }
            
            if (text.isNotEmpty()) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    modifier = Modifier
                        .padding(2.dp)
                        .padding(end = 15.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = {
                                if (text.isNotBlank()) {
                                    viewModel.sendMessage(
                                        content = text,
                                        fromUser = viewModel.profile.value.id,
                                        chatId = chat.id,
                                        userId = chat.userId,
                                        notificationToken = chat.notificationToken,
                                        attachments = emptyList()
                                    )
                                    text = ""
                                }
                            })
                        }
                )
            } else {
                var offset by remember { mutableStateOf(Offset.Zero) }

//                val alpha = (105f + offset.x) / 20f
                val scale = 1f + (offset.x / 850f)
                
                Box(
                    contentAlignment = Alignment.CenterEnd,
                    modifier = Modifier
                        .padding(end = if (!isRecording) 15.dp else 0.dp)
                        .size(height = 65.dp, width = 150.dp)
                        .clip(RoundedCornerShape(50))
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = {
                                    println("Drag started")
                                    isDragging = true
                                },
                                onDragEnd = {
                                    println("Drag ended")
                                    isDragging = false
                                    // Проверяем, если смещение больше -200f, то сбрасываем смещение
                                    if (offset.x > -200f) {
                                        isRecording = false
                                        offset = Offset.Zero
                                    } else {
                                        // Если смещение больше чем -200f, завершаем запись
                                        isRecording = false
                                        offset = Offset.Zero
                                    }
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    val newOffset = Offset(
                                        x = (offset.x + dragAmount.x).coerceAtLeast(-200f)
                                            .coerceAtMost(0f),
                                        y = offset.y
                                    )
                                    offset = newOffset
                                }
                            )
                        }
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    println("Tap detected")
                                    
                                    val seconds = recordingTime % 60

                                    if (seconds > 2) {
                                        val stopByte = audioRecorder.stopRecording(true)
                                        
                                        if (stopByte !== null) {
                                            isStartRecording = false
                                            
                                            viewModel.sendAttachments(
                                                content = text,
                                                fromUser = viewModel.profile.value.id,
                                                chatId = chat.id,
                                                stopByte,
                                                "audio/mp4",
                                                "audio_record"
                                            
                                            )
                                        }
                                    }
                                    
                                    isRecording = false
                                    recordingTime = 0
                                    isDragging = false
                                    
                                },
                                onPress = {
                                    if (!isDragging) {
                                        isRecording = !isRecording
                                    }
                                    println("Press released")
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
                        modifier = sizeModifier
                            .offset {
                                IntOffset(
                                    offset.x.roundToInt(),
                                    offset.y.roundToInt()
                                )
                            }
                            .scale(1f + (offset.x / 850f)),
                        painter = if (!isRecording) painterResource(Res.drawable.chat_microphone) else painterResource(
                            Res.drawable.chat_micro_active
                        ),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }
                
            }
        }
    }
}