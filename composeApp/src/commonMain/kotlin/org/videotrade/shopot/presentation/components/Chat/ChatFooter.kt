package org.videotrade.shopot.presentation.components.Chat

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.zIndex
import dev.icerock.moko.resources.compose.stringResource
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.multiplatform.FileProviderFactory
import org.videotrade.shopot.multiplatform.PermissionsProviderFactory
import org.videotrade.shopot.multiplatform.Platform
import org.videotrade.shopot.multiplatform.getAndSaveFirstFrame
import org.videotrade.shopot.multiplatform.getPlatform
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.arrow_left
import shopot.composeapp.generated.resources.chat_forward
import shopot.composeapp.generated.resources.chat_micro
import shopot.composeapp.generated.resources.chat_send_arrow
import shopot.composeapp.generated.resources.clip
import shopot.composeapp.generated.resources.keyboard
import shopot.composeapp.generated.resources.lock
import shopot.composeapp.generated.resources.menu_file
import shopot.composeapp.generated.resources.menu_gallery
import shopot.composeapp.generated.resources.sticker_menu
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.random.Random


data class MenuItem(
    val text: String,
    val imagePath: DrawableResource,
    val onClick: () -> Unit,
)


@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ChatFooter(
    chat: ChatItem,
    viewModel: ChatViewModel,
    showStickerMenu: MutableState<Boolean>,
    onStickerButtonClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val colors = MaterialTheme.colorScheme

    var recordingTime by remember { mutableStateOf(0) }
    val swipeOffset = remember { Animatable(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var isStartRecording by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showFilePicker by remember { mutableStateOf(false) }
    var voiceName by remember { mutableStateOf("") }
    var voicePath by remember { mutableStateOf("") }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val footerText by viewModel.footerText.collectAsState()
    val isStopAndSendVoice = remember { mutableStateOf(false) }
    val isRecordingLock = remember { mutableStateOf(false) }
    var activeAxis by remember { mutableStateOf<String?>(null) }


    val audioRecorder = viewModel.audioRecorder.collectAsState().value
    val isRecording = viewModel.isRecording.collectAsState().value

    val selectedMessagePair = viewModel.selectedMessagesByChat.collectAsState().value[chat.chatId]
    val profile = viewModel.profile.collectAsState().value
    val selectedMessage = selectedMessagePair?.first
    val selectedMessageSenderName = selectedMessagePair?.second

    LaunchedEffect(isStopAndSendVoice.value) {
        if (isStopAndSendVoice.value) {
            println("isStopAndSendVoice")
            viewModel.sendVoice(chat, voiceName)

            isStopAndSendVoice.value = false
        }
    }


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
                            voiceName = "audio_record${Random.nextInt(0, 100000)}.m4a"
                            val microphonePer =
                                PermissionsProviderFactory.create().getPermission("microphone")
                            if (microphonePer) {
                                val audioFilePathNew = FileProviderFactory.create()
                                    .createNewFileWithApp(
                                        voiceName,
                                        "audio/mp4"
                                    ) // Генерация пути к файлу

                                println("audioFilePathNew $audioFilePathNew")

                                if (audioFilePathNew != null) {
                                    voicePath = audioFilePathNew

                                    audioRecorder.startRecording(audioFilePathNew)
                                }
                                isStartRecording = true
                            } else {

                                println("perStop")

                                viewModel.setIsRecording(false)

                                audioRecorder.stopRecording(false)
                                offset = Offset.Zero
                            }
                        }
                    }

                }
            }
        } else {
            recordingTime = 0
        }
    }

    LaunchedEffect(showStickerMenu) {
        if (showStickerMenu.value) {
            keyboardController?.hide()
        }
    }

    DisposableEffect(showStickerMenu.value) {
        if (showStickerMenu.value) {
            keyboardController?.hide()
        }
        onDispose { }
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
        targetValue = -15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

//    val singleImagePicker = rememberImagePickerLauncher(
//        selectionMode = SelectionMode.Single,
//        scope = scope,
//        onResult = { byteArrays ->
//            byteArrays.firstOrNull()?.let {
//                scope.launch {
//                    viewModel.sendAttachments(
//                        content = text,
//                        fromUser = viewModel.profile.value.id,
//                        chatId = chat.id,
//                        "image",
//                        "jpg",
//                        null,
//                        it,
//                    )
//                }
//            }
//        }
//    )

    val menuItems = listOf(
        MenuItem(
            text = stringResource(MokoRes.strings.gallery),
            imagePath = Res.drawable.menu_gallery,
            onClick = {
//                viewModel.sendImage(
//                    footerText,
//                    viewModel.profile.value.id,
//                    chat.id,
//                    "image",
//                    "jpg",
//                )

                scope.launch {
                    try {
                        val filePick = FileProviderFactory.create()
                            .pickGallery()

                        if (filePick !== null) {
                            val fileData =
                                FileProviderFactory.create().getFileData(filePick.fileContentPath)

                            if (fileData?.fileType?.substringBefore("/") == "image") {
                                viewModel.sendImage(
                                    viewModel.footerText.value,
                                    viewModel.profile.value.id,
                                    chat.id,
                                    filePick.fileName,
                                    filePick.fileAbsolutePath,
                                    fileData.fileType
                                )
                            } else {
                                getAndSaveFirstFrame(filePick.fileAbsolutePath) { photoName, photoPath, photoByteArray ->
                                    viewModel.addFileMessage(
                                        chat,
                                        "video",
                                        filePick,
                                        photoPath,
                                        photoName,
                                    )
                                }
                            }

                        }


                    } catch (e: Exception) {
                        println("Error: ${e.message}")
                    }

                }
            }
        ),
        MenuItem(
            text = stringResource(MokoRes.strings.file),
            imagePath = Res.drawable.menu_file,
            onClick = {

                scope.launch {
                    try {
                        val filePick = FileProviderFactory.create()
                            .pickFile(PickerType.File(listOf("pdf", "zip")))

                        if (filePick !== null) {
                            val fileData =
                                FileProviderFactory.create().getFileData(filePick.fileContentPath)

                            if (fileData !== null) {
                                viewModel.addFileMessage(chat, fileData.fileType, filePick)
                            }
                        }


                    } catch (e: Exception) {
                        println("Error: ${e.message}")
                    }
                }
            }
        ),
//        MenuItem(
//            text = stringResource(MokoRes.strings.video),
//            imagePath = Res.drawable.menu_video,
//            onClick = {
//                scope.launch {
//                    try {
//                        val filePick = FileProviderFactory.create()
//                            .pickFile(PickerType.File(listOf("mp4")))
//
//                        if (filePick != null) {
//                            getAndSaveFirstFrame(filePick.fileAbsolutePath) { photoName, photoPath, photoByteArray ->
//                                viewModel.addFileMessage(
//                                    chat,
//                                    "mp4",
//                                    filePick,
//                                    photoPath,
//                                    photoName,
//                                    photoByteArray
//                                )
//                            }
//                        }
//
//                    } catch (e: Exception) {
//                        println("Error: ${e.message}")
//                    }
//                }
//            }
//        ),
    )


    val collapsedHeight = if (selectedMessage != null) 375.dp else 56.dp
    val collapsedselectedHeight = if (selectedMessage != null) 41.dp else 0.dp

    // Анимация высоты Row
    val height by animateDpAsState(targetValue = collapsedHeight)
    val selectedHeight by animateDpAsState(targetValue = collapsedselectedHeight)


    val maxHeight =
        if (selectedMessage != null) 240.dp else 200.dp // Увеличиваем максимальную высоту компонента
    val minHeight = when {
//        selectedMessage != null && footerText.length  > 120 && showMenu -> 258.dp
//        selectedMessage != null && footerText.length > 120 -> 200.dp
        selectedMessage != null && showMenu -> 160.dp // Укажите высоту, если оба условия истинны
        selectedMessage != null -> 97.dp
        showMenu -> 120.dp
        else -> 56.dp
    }

    val lineHeight = 16.dp // Высота одной строки текста в dp
    val maxLines = 8       // Максимальное количество строк текста

    // Рассчитываем количество строк на основе текста
    val calculatedLines = remember(footerText) {
        val totalLines = footerText.split("\n").size
        val approximateLines =
            (footerText.length / 20).coerceAtLeast(1) // Примерно 20 символов на строку
        totalLines.coerceAtLeast(approximateLines)
    }.coerceAtMost(maxLines)

    // Высота на основе количества строк
    val targetHeight = minHeight + calculatedLines * lineHeight

    // Ограничиваем высоту максимумом
    val finalHeight = targetHeight.coerceAtMost(maxHeight)

    // Анимируем высоту
    val animatedHeight by animateDpAsState(targetValue = finalHeight)


// Анимация постоянного движения вверх и вниз
    val animationOffset by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val lockOffsetY = remember { mutableStateOf(-50f) }


// Общее смещение с учетом свайпа и анимации
    val totalLockOffsetY = lockOffsetY.value.dp + animationOffset.dp


    Box(
        modifier = Modifier
    ) {

        if (isRecording && !isRecordingLock.value) {
            Box(
                modifier = Modifier
                    .zIndex(1f)
                    .align(Alignment.TopEnd)
                    .offset(x = (-16).dp, y = totalLockOffsetY) // Смещение контролируется состоянием
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 30.dp, height = 40.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFCAB7A3)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(Res.drawable.lock),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .zIndex(0f)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(colors.background)
                .then(
                    if (getPlatform() == Platform.Ios) {
                        Modifier
                            .padding(bottom = 15.dp)
                    } else {
                        Modifier
                            .imePadding()
                            .padding(bottom = 5.dp)
                            .windowInsetsPadding(WindowInsets.navigationBars) // This line adds padding for the navigation bar
                    }
                )


        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 10.dp)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .heightIn(max = 375.dp, min = 56.dp)
                    .height(animatedHeight)

            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    if (selectedMessage != null && selectedMessageSenderName != null) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(selectedHeight)
                                    .background(colors.background),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            )

                            {

                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = painterResource(Res.drawable.chat_forward),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(width = 20.dp, height = 14.dp)
                                            .graphicsLayer(scaleX = -1f), // Отражение по горизонтали
                                        colorFilter = ColorFilter.tint(Color(0xFFCAB7A3))
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Box(
                                        modifier = Modifier
                                            .width(1.dp)
                                            .fillMaxHeight()
                                            .background(Color(0xFFCAB7A3))
                                    ) {

                                    }
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Box(modifier = Modifier.weight(1f)) {

                                    SelectedMessageFormat(
                                        selectedMessage,
                                        profile,
                                        viewModel,
                                        isFromFooter = true
                                    )
                                }

                                Box(
                                    modifier = Modifier.fillMaxHeight().width(60.dp)
                                        .pointerInput(Unit) {
                                            viewModel.clearSelection(chatId = chat.chatId)
                                        }, contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close",
                                        tint = Color(0xFFCAB7A3),
                                        modifier = Modifier.size(20.dp),
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }


                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .heightIn(max = 375.dp, min = 60.dp)

                    ) {
                        if (isRecording) {
                            Row(
                                modifier = Modifier.width(90.dp).padding(top = 5.dp),
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
//                            val hours = recordingTime / 3600
                                val minutes = (recordingTime % 3600) / 60
                                val seconds = recordingTime % 60
                                Text(
                                    text = "${minutes.toString().padStart(2, '0')}:${
                                        seconds.toString().padStart(2, '0')
                                    }",
                                    //text = "$hours:${minutes.toString().padStart(2, '0')}:${
                                    //                                    seconds.toString().padStart(2, '0')
                                    //                                }",
                                    fontSize = 16.sp,
                                    lineHeight = 16.sp,
                                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                    fontWeight = FontWeight(400),
                                    color = colors.secondary,
                                    letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                )
                            }

                            AnimatedContent(
                                targetState = isRecordingLock.value,
                                transitionSpec = {
                                    (slideInVertically(initialOffsetY = { fullHeight -> fullHeight }) + fadeIn(animationSpec = tween(300)))
                                        .togetherWith(slideOutVertically(targetOffsetY = { fullHeight -> -fullHeight }) + fadeOut(animationSpec = tween(150)))
                                },
                                modifier = Modifier
                                    .padding(start = 10.dp, end = 10.dp)
                                    .fillMaxWidth(0.65f)
                            ) { isLocked ->
                                if (!isLocked) {
                                    Row(
                                        modifier = Modifier
                                            .offset(x = swipeOffset.value.dp) // Смещение зависит от `swipeOffset`
                                            .alpha(1f + (swipeOffset.value / 50f)),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .offset(x = (textOffset + swipeOffset.value).dp)
                                                .alpha(1f + (swipeOffset.value / 100f)),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            Image(
                                                modifier = Modifier
                                                    .size(width = 7.dp, height = 14.dp),
                                                painter = painterResource(Res.drawable.arrow_left),
                                                contentDescription = null,
                                                contentScale = ContentScale.Crop,
                                                colorFilter = ColorFilter.tint(colors.secondary)
                                            )
                                            Spacer(modifier = Modifier.width(9.dp))
                                            Text(
                                                text = stringResource(MokoRes.strings.left_cancel),
                                                fontSize = 16.sp,
                                                lineHeight = 16.sp,
                                                fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                                fontWeight = FontWeight(400),
                                                color = colors.secondary,
                                                letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                            )
                                        }
                                    }
                                } else {
                                    Row(
                                        modifier = Modifier
                                            .pointerInput(Unit) {


                                                viewModel.setIsRecording(false)
                                                audioRecorder.stopRecording(false)
                                                isStartRecording = false
                                                offset = Offset.Zero
                                                scope.launch {
                                                    swipeOffset.snapTo(0f)
                                                }
                                                isRecordingLock.value = false



                                        },
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = stringResource(MokoRes.strings.cancel),
                                            fontSize = 16.sp,
                                            lineHeight = 16.sp,
                                            fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                            fontWeight = FontWeight(400),
                                            color = colors.secondary,
                                            letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                        )
                                    }
                                }
                            }

                        }


                        Row(
                            modifier = Modifier
                                .alpha(if (isRecording) 0f else 1f)
                                .weight(1f)
                                .animateContentSize(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AnimatedVisibility(
                                visible = !isRecording,
                                enter = fadeIn(animationSpec = tween(300)) + expandIn(expandFrom = Alignment.Center),
                                exit = fadeOut(animationSpec = tween(300)) + shrinkOut(shrinkTowards = Alignment.Center)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .padding(end = 13.dp).width(20.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (showMenu) {
                                        Image(
                                            painter = painterResource(Res.drawable.keyboard),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(width = 20.dp, height = 12.dp)
                                                .pointerInput(Unit) {
                                                    showMenu = false
                                                },
                                            colorFilter = ColorFilter.tint(colors.primary)
                                        )
                                    } else {
                                        Image(
                                            painter = painterResource(Res.drawable.clip),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(width = 15.86.dp, height = 17.94.dp)
                                                .pointerInput(Unit) {
                                                    showMenu = true
                                                },
                                            colorFilter = ColorFilter.tint(colors.primary)
                                        )
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .border(
                                        width = 1.dp,
                                        color = colors.onSecondary,
                                        shape = RoundedCornerShape(size = 16.dp)
                                    )
                                    .animateContentSize()
                            ) {


                                BasicTextField(
                                    value = footerText,
                                    onValueChange = { newText ->
                                        if (!isRecording) {
                                            viewModel.footerText.value = if (footerText.isEmpty()) {
                                                // Преобразуем первый символ в заглавный, если поле было пустым
                                                newText.replaceFirstChar { it.uppercase() }
                                            } else {
                                                newText
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .heightIn(max = 130.dp, min = 56.dp)
                                        .padding(16.dp)
                                        .fillMaxWidth(), // Для обеспечения выравнивания по ширине
                                    textStyle = TextStyle(
                                        fontSize = 16.sp,
                                        lineHeight = lineHeight.value.sp,
                                        fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                        fontWeight = FontWeight(400),
                                        color = colors.primary,
                                        letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                        textAlign = TextAlign.Start
                                    ),
                                    cursorBrush = SolidColor(colors.primary),
                                    visualTransformation = VisualTransformation.None,
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        capitalization = KeyboardCapitalization.Sentences // Заставляет начинать с заглавной буквы
                                    ),
                                    decorationBox = { innerTextField ->
                                        Box(
                                            modifier = Modifier.fillMaxWidth(), // Обеспечивает выравнивание текста по центру
                                            contentAlignment = Alignment.CenterStart // Центрируем внутреннее поле
                                        ) {
                                            if (footerText.isEmpty()) {
                                                Text(
                                                    stringResource(MokoRes.strings.write_message),
                                                    fontSize = 16.sp,
                                                    lineHeight = lineHeight.value.sp,
                                                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                                    fontWeight = FontWeight(400),
                                                    color = colors.secondary,
                                                    letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                                    textAlign = TextAlign.Start
                                                )
                                            }
                                            innerTextField() // Вставка текстового поля в Box
                                        }
                                    },
                                )

                                if (!isRecording) {
                                    Box(
                                        contentAlignment = Alignment.CenterEnd,
                                        modifier = Modifier
                                            .padding(end = 19.dp)
                                            .size(height = 56.dp, width = 30.dp)
                                            .clickable {
                                                onStickerButtonClick()
                                            }
                                    ) {
                                        Image(
                                            painter = painterResource(Res.drawable.sticker_menu),
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(18.dp),
                                            colorFilter = ColorFilter.tint(colors.secondary)
                                        )
                                    }
                                }

                            }
                            Spacer(modifier = Modifier.width(12.dp))


                        }


                        val boxSize by animateDpAsState(
                            targetValue = if (footerText.isNotEmpty()) 56.dp else 50.dp,
                            animationSpec = tween(
                                durationMillis = 100, // Длительность анимации
                                easing = FastOutSlowInEasing // Плавное увеличение
                            )
                        )

                        val isFooterTextNotEmpty = footerText.isNotEmpty()


                        AnimatedVisibility(
                            visible = isFooterTextNotEmpty,
                            enter = fadeIn(animationSpec = tween(100)) + expandIn(expandFrom = Alignment.Center),
                            exit = fadeOut(animationSpec = tween(100)) + shrinkOut(shrinkTowards = Alignment.Center)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = Color(0xFFCAB7A3),
                                        shape = RoundedCornerShape(size = 16.dp)
                                    )
                                    .size(boxSize),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(Res.drawable.chat_send_arrow),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.pointerInput(Unit) {
                                        detectTapGestures(onTap = {
                                            if (footerText.isNotBlank()) {
                                                //обрезаем пробелы в конце и начале текста
                                                val trimmedText = footerText.trim()
                                                viewModel.sendMessage(
                                                    content = trimmedText,
                                                    fromUser = viewModel.profile.value.id,
                                                    chatId = chat.id,
                                                    notificationToken = chat.notificationToken,
                                                    attachments = emptyList(),
                                                    login = "${viewModel.profile.value.firstName} ${viewModel.profile.value.lastName}",
                                                    true
                                                )
                                                viewModel.footerText.value = ""
                                            }
                                        })
                                    }
                                )
                            }
                        }



                        AnimatedVisibility(
                            visible = !isFooterTextNotEmpty,
                            enter = fadeIn(animationSpec = tween(300)) + expandIn(expandFrom = Alignment.Center),
                            exit = fadeOut(animationSpec = tween(300)) + shrinkOut(shrinkTowards = Alignment.Center)
                        ) {
                            Box(
                                contentAlignment = Alignment.CenterEnd,
                                modifier = Modifier
                                    .size(
                                        height = 56.dp,
                                        width = if (isRecording) 150.dp else 20.dp
                                    )
                                    .pointerInput(isRecordingLock.value) {
                                        if (!isRecordingLock.value) {
                                            detectDragGestures(
                                                onDragStart = {
                                                    isDragging = true
                                                    activeAxis = null // Сбрасываем ось при начале нового жеста
                                                },
                                                onDragEnd = {
                                                    println("Drag ended")
                                                    when {
                                                        offset.x <= -200f -> {
                                                            // Завершение свайпа влево
                                                            println("Swipe left complete, resetting button")
                                                            viewModel.setIsRecording(false)
                                                            audioRecorder.stopRecording(false)
                                                            isStartRecording = false
                                                            offset = Offset.Zero
                                                            scope.launch {
                                                                swipeOffset.snapTo(0f) // Сбрасываем смещение
                                                            }
                                                        }
                                                        offset.y <= -25f -> {
                                                            // Завершение свайпа вверх
                                                            println("Swipe up complete, locking recording")
                                                            isRecordingLock.value = true
                                                            isStartRecording = false
                                                            offset = Offset.Zero
                                                            lockOffsetY.value = -50f // Смещаем кнопку lock вверх
                                                        }
                                                        else -> {
                                                            println("Swipe not complete, resetting position")
                                                            scope.launch {
                                                                offset = Offset.Zero // Возвращаем кнопку на место
                                                                swipeOffset.animateTo(0f)
                                                                lockOffsetY.value = -50f // Сбрасываем положение lock

                                                            }
                                                            val seconds = recordingTime % 60
                                                            if (seconds > 1) {
                                                                isStartRecording = false
                                                                isStopAndSendVoice.value = true
                                                            }
                                                            viewModel.setIsRecording(false)
                                                            recordingTime = 0
                                                            isDragging = false
                                                        }
                                                    }
                                                    if (offset == Offset.Zero) {
                                                        activeAxis = null
                                                    }
                                                    isDragging = false
                                                    activeAxis = null // Сбрасываем ось после завершения свайпа
                                                },
                                                onDrag = { change, dragAmount ->
                                                    change.consume()
                                                    // Определяем ось на основе первого значительного движения
                                                    if (activeAxis == null) {
                                                        activeAxis = when {
                                                            abs(dragAmount.x) > abs(dragAmount.y) -> "x" // Горизонтальный свайп
                                                            else -> "y" // Вертикальный свайп
                                                        }
                                                    }

                                                    // Обрабатываем только движение по активной оси
                                                    if (activeAxis == "x") {
                                                        // Обновляем смещение для кнопки записи
                                                        offset = Offset(
                                                            x = (offset.x + dragAmount.x).coerceAtLeast(-200f).coerceAtMost(0f),
                                                            y = offset.y
                                                        )
                                                        scope.launch {
                                                            swipeOffset.snapTo(
                                                                (swipeOffset.value + dragAmount.x / 4).coerceIn(-50f, 0f) // Ограничиваем диапазон
                                                            )
                                                        }
                                                    } else if (activeAxis == "y") {
                                                        // Обновляем положение кнопки lock на основе свайпа вверх
                                                        offset = Offset(
                                                            x = offset.x,
                                                            y = (offset.y + dragAmount.y).coerceAtLeast(-25f).coerceAtMost(0f)
                                                        )
                                                        if (offset.y < 25f) {
                                                            lockOffsetY.value =
                                                                offset.y.coerceAtMost(0f) - 50f
                                                        }
                                                    }
                                                    if (offset == Offset.Zero) {
                                                        activeAxis = null
                                                    }
                                                }
                                            )
                                        }
                                    }
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onTap = {
                                                println("Tap detected")
                                                val seconds = recordingTime % 60
                                                if (seconds > 1) {
                                                    isStartRecording = false
                                                    isStopAndSendVoice.value = true
                                                }
                                                viewModel.setIsRecording(false)
                                                recordingTime = 0
                                                isDragging = false
                                                isRecordingLock.value = false
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
                                val infiniteTransition = rememberInfiniteTransition()

                                // Анимация прозрачности волны
                                val waveAlpha by infiniteTransition.animateFloat(
                                    initialValue = 0.1f,
                                    targetValue = 0.5f,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(durationMillis = 1200, easing = LinearOutSlowInEasing),
                                        repeatMode = RepeatMode.Reverse
                                    )
                                )

                                // Анимация масштаба волны
                                val waveScale by infiniteTransition.animateFloat(
                                    initialValue = 1.07f,
                                    targetValue = 1.15f,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(durationMillis = 1200, easing = LinearOutSlowInEasing),
                                        repeatMode = RepeatMode.Reverse
                                    )
                                )

                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(56.dp)
                                        .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) } // Используем ваш offset
                                ) {
                                    if (isRecording) {
                                        // Волна вокруг кнопки
                                        Box(
                                            modifier = Modifier
                                                .size(56.dp) // Размер волны равен размеру кнопки
                                                .scale(waveScale) // Анимация масштаба волны
                                                .background(
                                                    color = Color(0xFFCAB7A3).copy(alpha = waveAlpha),
                                                    shape = RoundedCornerShape(16.dp) // Квадратная форма волны
                                                )
                                        )
                                    }

                                    // Фиксированная кнопка записи

                                        Box(
                                            modifier = Modifier
                                                .size(56.dp)
                                                .background(
                                                    color = if (isRecording) Color(0xFFCAB7A3) else Color.Transparent,
                                                    shape = RoundedCornerShape(16.dp)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
//                                            AnimatedContent(
//                                                targetState = isRecordingLock.value,
//                                                transitionSpec = {
//                                                    (slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }) + fadeIn())
//                                                        .togetherWith(slideOutHorizontally(targetOffsetX = { fullWidth -> -fullWidth }) + fadeOut())
//                                                },
//                                                modifier = Modifier.size(30.dp)
//                                            ) { isLocked ->
//                                            Box() {
                                                if (!isRecordingLock.value) {
                                                    Image(
                                                        painter = painterResource(Res.drawable.chat_micro),
                                                        contentDescription = null,
                                                        contentScale = ContentScale.Crop,
                                                        colorFilter = if (!isRecording) ColorFilter.tint(colors.primary) else ColorFilter.tint(
                                                            Color.White
                                                        ),
                                                        modifier = Modifier.background(Color.Transparent)
                                                    )
                                                } else {
                                                    Image(
                                                        painter = painterResource(Res.drawable.chat_send_arrow),
                                                        contentDescription = null,
                                                        contentScale = ContentScale.Crop,
                                                        colorFilter = if (!isRecording) ColorFilter.tint(colors.primary) else ColorFilter.tint(
                                                            Color.White
                                                        ),
                                                        modifier = Modifier.background(Color.Transparent)
                                                    )
//                                                }
//                                            }
                                        }
                                    }
                                }
                            }
                        }


                    }


                    Crossfade(targetState = showMenu) { isMenuVisible ->
                        if (isMenuVisible) {
                            Row(
                                modifier = Modifier
                                    .height(56.dp)
                                    .fillMaxWidth()
                                    .padding(horizontal = 70.dp)
                                    .padding(top = 20.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                menuItems.forEachIndexed { index, editOption ->
                                    Column(
                                        modifier = Modifier
                                            .size(width = 80.dp, height = 45.dp)
                                            .pointerInput(Unit) {
                                                editOption.onClick()
                                                showMenu = false
                                            },
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Image(
                                            painter = painterResource(editOption.imagePath),
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            colorFilter = ColorFilter.tint(colors.primary)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = editOption.text,
                                            fontSize = 16.sp,
                                            lineHeight = 16.sp,
                                            fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                                            fontWeight = FontWeight(500),
                                            color = colors.primary,
                                            letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
    }
}


fun sendVoiceMessage() {


}