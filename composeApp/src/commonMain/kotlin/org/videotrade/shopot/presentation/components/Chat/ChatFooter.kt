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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.draw.shadow
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
import androidx.compose.ui.window.Popup
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.api.getCurrentTimeList
import org.videotrade.shopot.domain.model.Attachment
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.multiplatform.FileProviderFactory
import org.videotrade.shopot.multiplatform.PermissionsProviderFactory
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.chat_arrow_left
import shopot.composeapp.generated.resources.chat_micro_active
import shopot.composeapp.generated.resources.chat_microphone
import shopot.composeapp.generated.resources.menu_file
import shopot.composeapp.generated.resources.menu_gallery
import kotlin.math.roundToInt
import kotlin.random.Random


data class MenuItem(
    val text: String,
    val imagePath: DrawableResource,
    val onClick: () -> Unit,
)


@Composable
fun ChatFooter(chat: ChatItem, viewModel: ChatViewModel) {
    val scope = rememberCoroutineScope()
    
    var text by remember { mutableStateOf("") }
    
    var recordingTime by remember { mutableStateOf(0) }
    val swipeOffset = remember { Animatable(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var isStartRecording by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showFilePicker by remember { mutableStateOf(false) }
    
    
    val audioRecorder = viewModel.audioRecorder.collectAsState().value
    val isRecording = viewModel.isRecording.collectAsState().value
    
    
    LaunchedEffect(isRecording) {
        if (isRecording) {
            while (isRecording) {
                delay(1000L)
                
                recordingTime++
                
                var seconds = recordingTime
                seconds++
                
                if (seconds > 1) {
                    if (!isStartRecording) {
                        scope.launch {
//                            println("isStartRecording")
                            println("start Audio")
                            
                            val microphonePer =
                                PermissionsProviderFactory.create().getPermission("microphone")
                            if (microphonePer) {
                                val audioFilePathNew = FileProviderFactory.create()
                                    .getFilePath(
                                        "audio_record.m4a",
                                        "audio/mp4"
                                    ) // Генерация пути к файлу
                                
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
                        "image",
                        "jpg",
                        null,
                        it,
                    )
                }
            }
        }
    )
    
    val menuItems = listOf(
        MenuItem(
            text = "Галерея",
            imagePath = Res.drawable.menu_gallery,
            onClick = {
                singleImagePicker.launch()
            }
        ),
//    MenuItem(
//        text = "Фото",
//        imagePath = Res.drawable.edit_pencil,
//        onClick = {
//
//        }
//    ),
        MenuItem(
            text = "Файл",
            imagePath = Res.drawable.menu_file,
            onClick = {
                
                scope.launch {
                    try {
                        val absltPath = FileProviderFactory.create()
                            .pickFileAndGetAbsolutePath(PickerType.File(listOf("pdf", "zip")))
                        
                        if (absltPath !== null) {
                            val fileData =
                                FileProviderFactory.create().getFileData(absltPath.fileContentPath)
                            
                            println("fileData $fileData ${Random.nextInt(1, 501)}")
                            
                            val uploadIdd = Random.nextInt(1, 501).toString()
                            
                            if (fileData !== null) {
                                viewModel.addMessage(
                                    MessageItem(
                                        Random.nextInt(1, 501).toString(),
                                        viewModel.profile.value.id,
                                        "",
                                        "",
                                        "",
                                        0,
                                        getCurrentTimeList(),
                                        false,
                                        chat.id,
                                        false,
                                        true,
                                        listOf(
                                            Attachment(
                                                Random.nextInt(1, 501).toString(),
                                                Random.nextInt(1, 501).toString(),
                                                viewModel.profile.value.id,
                                                Random.nextInt(1, 501).toString(),
                                                fileData.fileType,
                                                fileData.fileName,
                                                originalFileDir = absltPath.fileAbsolutePath,
                                                fileData.fileSize
                                            )
                                        ),
                                        upload = true,
                                        uploadId = Random.nextInt(1, 501).toString()
                                    )
                                )
                            }
                        }
                        
                        
                    } catch (e: Exception) {
                        println("Error: ${e.message}")
                    }
                }
            }
        ),
//     MenuItem(
//        text = "Видео",
//        imagePath = Res.drawable.edit_pencil,
//        onClick = {
//
//        }
//    ),
    )
    
    Box(
        modifier = Modifier
            .imePadding()
            .padding(vertical = 15.dp)
            .background(Color.White)
    ) {
        
        if (showMenu) {
            Popup(
                alignment = Alignment.TopStart,
                onDismissRequest = { showMenu = false },
                
                ) {
                Column(
                    modifier = Modifier
                        .padding(bottom = 55.dp, start = 12.dp)
                        .fillMaxWidth(0.5f)
                        .shadow(
                            elevation = 6.dp,
                            shape = RoundedCornerShape(8.dp),
                            clip = false,
                            ambientColor = Color.Gray,
                            spotColor = Color.Gray
                        )
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                ) {
                    menuItems.forEachIndexed { index, editOption ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(
                                    start = 16.dp,
                                    end = 16.dp,
                                    top = 9.dp,
                                    bottom = 5.dp
                                )
                                .fillMaxWidth()
                                .clickable {
                                    editOption.onClick()
                                    showMenu = false
                                }
                        ) {
                            Image(
                                painter = painterResource(editOption.imagePath),
                                contentDescription = null,
                                modifier = Modifier.size(25.dp)
                            )
                            Spacer(modifier = Modifier.width(20.dp))
                            Text(
                                text = editOption.text,
                                textAlign = TextAlign.Center,
                                fontSize = 15.sp,
                                fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                                letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                                lineHeight = 20.sp,
                                color = Color(0xFF000000)
                            )
                        }
                        if (index < editOptions.size - 1) {
                            Divider(color = Color.Gray.copy(alpha = 0.12f))
                        } else {
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        }
        
        
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
//                            .clickable {
//                                singleImagePicker.launch()
//                            }
                            
                            .clickable {
                                showMenu = true
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
                    tint = Color(0xFF29303C),
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
                                        notificationToken = chat.notificationToken,
                                        attachments = emptyList(),
                                        login = "${viewModel.profile.value.firstName} ${viewModel.profile.value.lastName}",
                                        true
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
                                        println("Drag send")
                                        
                                        val seconds = recordingTime % 60
                                        
                                        if (seconds > 1) {
                                            val stopByte = audioRecorder.stopRecording(true)
                                            
                                            if (stopByte !== null) {
                                                isStartRecording = false
                                                
                                                viewModel.sendAttachments(
                                                    content = text,
                                                    fromUser = viewModel.profile.value.id,
                                                    chatId = chat.id,
                                                    "audio/mp4",
                                                    "audio_record",
                                                    null,
                                                    stopByte,
                                                    
                                                    
                                                    )
                                            }
                                        }
                                        viewModel.setIsRecording(false)
                                        offset = Offset.Zero
                                    } else {
                                        // Если смещение больше чем -200f, завершаем запись
                                        viewModel.setIsRecording(false)
                                        
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
                                    
                                    if (newOffset.x <= -200f) {
                                        viewModel.setIsRecording(false)
                                        
                                        audioRecorder.stopRecording(false)
                                        offset = Offset.Zero
                                    }
                                    
                                    offset = newOffset
                                }
                            )
                        }
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    println("Tap detected")
                                    
                                    val seconds = recordingTime % 60
                                    
                                    if (seconds > 1) {
                                        val stopByte = audioRecorder.stopRecording(true)
                                        
                                        if (stopByte !== null) {
                                            isStartRecording = false
                                            
                                            viewModel.sendAttachments(
                                                content = text,
                                                fromUser = viewModel.profile.value.id,
                                                chatId = chat.id,
                                                "audio/mp4",
                                                "audio_record",
                                                null,
                                                stopByte,
                                                
                                                )
                                        }
                                    }
                                    
                                    viewModel.setIsRecording(false)
                                    
                                    recordingTime = 0
                                    isDragging = false
                                    
                                },
                                onPress = {
                                    if (!isDragging) {
                                        viewModel.setIsRecording(!isRecording)
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
                        contentScale = ContentScale.Crop,
//                        colorFilter = ColorFilter.tint(Color(0xFF29303C))
                    )
                }
                
            }
        }
    }
}
