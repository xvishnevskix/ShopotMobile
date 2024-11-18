package org.videotrade.shopot.presentation.components.Chat

import FileMessage
import SelectedFileMessage
import SelectedMessageImage
import SelectedMessageText
import SelectedVideoMessage
import SelectedVoiceMessage
import VideoMessage
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.api.formatTimeOnly
import org.videotrade.shopot.api.formatTimestamp
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.chat_copy
import shopot.composeapp.generated.resources.chat_delete
import shopot.composeapp.generated.resources.chat_forward
import shopot.composeapp.generated.resources.chat_reply
import shopot.composeapp.generated.resources.double_message_check
import shopot.composeapp.generated.resources.menu_copy
import shopot.composeapp.generated.resources.menu_delete
import shopot.composeapp.generated.resources.message_double_check
import shopot.composeapp.generated.resources.message_single_check
import shopot.composeapp.generated.resources.single_message_check

@Composable
fun MessageBox(
    viewModel: ChatViewModel,
    message: MessageItem,
    profile: ProfileDTO,
    messageSenderName: String,
    onClick: () -> Unit,
    onPositioned: (LayoutCoordinates) -> Unit,
    isVisible: Boolean,
    chat: ChatItem,
    answerMessageId: MutableState<String?>,
    coroutineScope: CoroutineScope,
    listState: LazyListState,
    messagesState: List<MessageItem>
) {
    val isReadByMe = remember { mutableStateOf(false) }
    var swipeOffset by remember { mutableStateOf(0f) }
    val iconOpacity by animateFloatAsState(targetValue = if (swipeOffset > 0) swipeOffset / 75f else 0f)
    val animatedOffset by animateFloatAsState(targetValue = swipeOffset)
    val isHighlighted = message.id == answerMessageId.value
    val backgroundColor = animateColorAsState(
        targetValue = if (isHighlighted) Color(0xFF2A293C).copy(alpha = 0.2f) else Color.Transparent,
        animationSpec = tween(durationMillis = 500)
    )
    val focusManager = LocalFocusManager.current

//    LaunchedEffect(Unit) {
//        message.phone?.let {
//            val findContact = viewModel.findContactByPhone(it)
//
//            if (findContact != null) {
//                messageSenderName.value = "${findContact.firstName} ${findContact.lastName}"
//            }
//        }
//    }


    LaunchedEffect(viewModel.messages.value) {
        if (message.fromUser == profile.id) {
            if (message.anotherRead) {
                isReadByMe.value = true
            }
        } else {
            if (!message.iread) {
                viewModel.sendReadMessage(message.id)
            }
        }
    }

    //покраска сообщенния при переходе на него
    LaunchedEffect(answerMessageId.value) {
        if (isHighlighted) {
            delay(2000)
            answerMessageId.value = null
        }
    }

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color = backgroundColor.value )
            .onGloballyPositioned(onPositioned)
            .alpha(if (isVisible) 1f else 0f).pointerInput(message) {
                detectTapGestures(
                    onTap = {
                        println("AAAAAAAAA")
                    }
                )
            }
            .pointerInput(message) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (swipeOffset > 60) {
                            viewModel.selectMessage(chat.chatId, message, messageSenderName)
                        }
                        swipeOffset = 0f
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume() // Поглощение жеста
                        swipeOffset =
                            (swipeOffset + dragAmount / 2).coerceIn(0f, 75f) // изменение скорости
                    }
                )
            }
    ) {


        BoxWithConstraints(
            contentAlignment = Alignment.CenterStart
        ) {
            if (animatedOffset > 0) {
                Box(
                    modifier = Modifier
                        .zIndex(2f)
                        .offset(x = animatedOffset.dp / 4)
//                        .padding(5.dp)
                        .alpha(iconOpacity)
                        .clip(RoundedCornerShape(50.dp))
                        .size(35.dp).background(
                            Color(0xFF2A293C)
                                .copy(alpha = 0.1f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.chat_reply),
                        contentDescription = null,
                        modifier = Modifier
                            .size(23.dp),
                        tint = Color.Black
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {


                Box(
                    contentAlignment = if (message.fromUser == profile.id) Alignment.CenterEnd else Alignment.CenterStart,
                    modifier = Modifier
                        .offset(x = animatedOffset.dp)
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .pointerInput(message) {
                            detectTapGestures(
                                onTap = {
                                    // Action for normal tap
                                    println("onTap")

                                    focusManager.clearFocus() // Your action on tap
                                },
                                onLongPress = {
                                    println("onLongPress")

                                    onClick()
                                }
                            )
                        }
//                        .clickable(
//                            interactionSource = remember { MutableInteractionSource() },
//                            indication = null // Убирает эффект нажатия
//                        ) {
//
//                            focusManager.clearFocus() // Ваше действие при нажатии
//                        }
                ) {
                    Surface(
                        modifier = Modifier
                            .wrapContentSize()
                            .widthIn(max = 340.dp),
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomEnd = if (message.fromUser == profile.id) 0.dp else 16.dp,
                            bottomStart = if (message.fromUser == profile.id) 16.dp else 0.dp,
                        ),


//                        shadowElevation = if (message.attachments?.isNotEmpty() == true && message.attachments!![0].type == "sticker") 0.dp else 4.dp,


                        color = if (message.attachments?.isNotEmpty() == true && message.attachments!![0].type == "sticker") {
                            Color.Transparent  // Прозрачный цвет для стикеров
                        } else {
                            if (message.fromUser == profile.id) Color(0xFFCAB7A3)  // Цвет для сообщений от текущего пользователя
                            else Color(0xFFF7F7F7)  // Цвет для сообщений от других пользователей
                        }



                    ) {
                        var messageFormatWidth by remember { mutableStateOf(0) }
//                        var selectedMessageWidth by remember { mutableStateOf(0) }

                        Column(
                            horizontalAlignment =

                            //изменение для самого бокса сообщений
                            if (message.fromUser == profile.id) Alignment.Start else Alignment.Start,
                        ) {



                            // Ответ на сообщение
                            message.answerMessage?.let {

                                Row(
                                    modifier = Modifier
//                                        .onGloballyPositioned { coordinates ->
//                                            selectedMessageWidth = coordinates.size.width
//                                        }
                                        .widthIn(
                                            min = (messageFormatWidth * 0.38f).dp,

                                        )
                                        .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                                        .height(56.dp)
                                        .background(
                                            color = if (message.fromUser == profile.id) Color(0x4DFFFFFF) else Color(0xFFFFFFFF),
                                            shape = RoundedCornerShape(size = 8.dp))
                                        .wrapContentHeight()
                                        .clickable {
//                                            answerMessageId.value = it.id // Устанавливаем ID выделенного сообщения

                                            coroutineScope.launch {


//                                                listState.animateScrollToItem(messagesState.indexOfFirst { msg -> msg.id == it.id })

//                                                var messageIndex: Int
//                                                var attemptCount = 0
//                                                val maxAttempts = 5
//
//                                                do {
//
//                                                    messageIndex = messagesState.indexOfFirst { msg -> msg.id == it.id }
//                                                    if (messageIndex == -1 && attemptCount < maxAttempts) {
//
//                                                        viewModel.getMessagesBack(chat.chatId)
//                                                        attemptCount++
//                                                        delay(500)
//
//                                                        messageIndex = messagesState.indexOfFirst { msg -> msg.id == it.id }
//                                                        println("messageIndex = ${messageIndex}")
//                                                    }
//                                                } while (messageIndex == -1 && attemptCount < maxAttempts)
//
//                                                if (messageIndex != -1) {
//                                                    listState.animateScrollToItem(messageIndex)
//                                                    println("messageIndex = ${messageIndex}")
//                                                }
                                            }
                                        },

                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement =
                                    if (message.fromUser == profile.id) Arrangement.Start else Arrangement.Start
                                ) {
                                    Box(modifier = Modifier

                                        .width(6.dp)
                                        .height(56.dp)
                                        .clip(RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp))
                                        .background(color = if (message.fromUser == profile.id) Color.White else Color(0xFFCAB7A3))) {

                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        SelectedMessageFormat(
                                            it,
                                            profile,
                                            viewModel
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                }
                            }

//                            // Проверка на персональный чат
//                            if (!chat.personal) {
//                                if (message.fromUser != profile.id) {
//                                    Text(
//                                        text = messageSenderName,
//                                        style = TextStyle(
//                                            color = Color.Gray,
//                                            fontSize = 12.sp,
//                                            fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
//                                        ),
//                                        modifier = Modifier.padding(
//                                            start = 25.dp,
//                                            end = 25.dp,
//                                            top = 7.dp,
//                                            bottom = 0.dp
//                                        ),
//                                    )
//                                }
//                            }
                            // Пересланное сообщение
                            if (message.forwardMessage == true) {
                            Row(
                                modifier = Modifier.padding(
                                    start = 16.dp,
                                    end = 16.dp,
                                    top = 16.dp,
                                ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {



                                    Image(
                                        modifier = Modifier.size(width = 16.5.dp, height = 11.5.dp),
                                        painter = painterResource(Res.drawable.chat_forward),
                                        contentDescription = null,
                                        colorFilter = if (message.fromUser == profile.id) ColorFilter.tint(Color(0xFFF7F7F7)) else ColorFilter.tint(Color(0x80373533))
                                    )

                                    Spacer(modifier = Modifier.width(7.dp))
                                    Text(
                                        stringResource(MokoRes.strings.forwarded_message),
                                        style = TextStyle(
                                            fontSize = 16.sp,
                                            lineHeight = 16.sp,
                                            fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                            fontWeight = FontWeight(400),
                                            color = if (message.fromUser == profile.id) Color(0xFFF7F7F7) else Color(0x80373533),
                                            letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                        ),
                                    )
                                }
                            }

                            // Проверка на персональный чат и наличие имени отправителя
                            if (!chat.personal && messageSenderName.isNotBlank()) {
                                Box(
                                    modifier = Modifier.padding(
                                        start = 16.dp,
                                        end = 16.dp,
                                        top = 16.dp,
                                    ),
                                ) {
                                    Text(
                                        text = messageSenderName,
                                        style = TextStyle(
                                            fontSize = 16.sp,
                                            lineHeight = 16.sp,
                                            fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                            fontWeight = FontWeight(400),
                                            color = if (message.fromUser == profile.id) Color(0xFFF7F7F7) else Color(0xFFCAB7A3),
                                            letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                        ),
                                        modifier = Modifier.padding(

                                        ),
                                    )
                                }
                            }


                            Box(
                                modifier = Modifier
                                    .onGloballyPositioned { coordinates ->
                                        messageFormatWidth = coordinates.size.width
                                    }
                                    .wrapContentSize()
                            ) {
                                MessageFormat(message, profile, onClick, messageSenderName, chat)
                            }
                        }
                    }
                }


            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            horizontalArrangement = if (message.fromUser == profile.id) Arrangement.End else Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier

                .fillMaxWidth().clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null // Убирает эффект нажатия
                ) {
                    focusManager.clearFocus() // Ваше действие при нажатии
                }
        ) {

            if (message.created.isNotEmpty())
                Text(
                    text = formatTimeOnly(message.created),
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 16.sp,
                        fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                        fontWeight = FontWeight(400),
                        color = Color(0x80373533),
                        letterSpacing = TextUnit(0F, TextUnitType.Sp),
                    ),
                    modifier = Modifier.padding(),
                )


            if (message.fromUser == profile.id)
                if (message.anotherRead) {
                    Image(
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .size(width = 17.7.dp, height = 8.5.dp),
                        painter = painterResource(Res.drawable.message_double_check),
                        contentDescription = null,
                    )
                } else {
                    Image(
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .size(width = 12.7.dp, height = 8.5.dp),
                        painter = painterResource(Res.drawable.message_single_check),
                        contentDescription = null,
                    )
                }



        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}


@Composable
fun MessageFormat(
    message: MessageItem,
    profile: ProfileDTO,
    onMessageClick: () -> Unit,
    messageSenderName: String? = null,
    chat: ChatItem? = null
) {
    if (message.attachments == null || message.attachments?.isEmpty() == true) {
        MessageText(message, profile)
//        FileMessage(message, )
    } else {

        when (message.attachments!![0].type) {

            "audio/mp4" -> {
                VoiceMessage(
                    message,
                    message.attachments!!
                )
            }

            "image" -> {
                MessageImage(
                    message, profile,
                    message.attachments!!,
                    messageSenderName
                )

            }
            
            "video" -> {
                VideoMessage(
                    message,
                    message.attachments!!,
                    messageSenderName
                )
            }

            "sticker" -> {
                StickerMessage(
                    message,
                    message.attachments!![0].fileId
                    )
            }

            else -> {
                FileMessage(
                    message,
                    message.attachments!!
                )
            }
        }


    }

}


@Composable
fun SelectedMessageFormat(
    message: MessageItem,
    profile: ProfileDTO? = null,
    viewModel: ChatViewModel,
    isFromFooter: Boolean = false
) {

    val messageAnswerName =
        message.phone?.let { phone ->
            if (message.fromUser == profile?.id) {
                stringResource(MokoRes.strings.you)
            } else {
                val findContact = viewModel.findContactByPhone(phone)
                if (findContact != null) {
                    "${findContact.firstName} ${findContact.lastName}"
                } else {
                    "+$phone"
                }
            }
        } ?: ""

    val colorTitle = if (isFromFooter) Color(0xFFCAB7A3) else Color(0xFF373533)

    if (message.attachments == null || message.attachments?.isEmpty() == true) {
        if (profile != null) {
            SelectedMessageText(message, messageAnswerName, colorTitle)
        }
    } else {

        when (message.attachments!![0].type) {
            "audio/mp4" -> {
                SelectedVoiceMessage(message, messageAnswerName, colorTitle)
            }

            "image" -> {
                SelectedMessageImage(message.attachments!!, messageAnswerName, colorTitle)
//                SelectedStickerMessage(message.attachments!!, messageAnswerName)

            }

            "video" -> {
                SelectedVideoMessage(message.attachments!!, messageAnswerName, colorTitle)

            }
            
            "sticker" -> {
                SelectedStickerMessage(message.attachments!!, messageAnswerName, colorTitle)
            }

            else -> {
                SelectedFileMessage(message, messageAnswerName, colorTitle)
            }
        }


    }

}