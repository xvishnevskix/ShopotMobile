package org.videotrade.shopot.presentation.components.Chat

import FileMessage
import SelectedFileMessage
import SelectedMessageImage
import SelectedMessageText
import SelectedVideoMessage
import SelectedVoiceMessage
import VideoMessage
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import dev.icerock.moko.resources.compose.stringResource
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
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.chat_copy
import shopot.composeapp.generated.resources.chat_delete
import shopot.composeapp.generated.resources.chat_forward
import shopot.composeapp.generated.resources.chat_reply
import shopot.composeapp.generated.resources.double_message_check
import shopot.composeapp.generated.resources.single_message_check


data class EditOption(
    val text: String,
    val imagePath: DrawableResource,
    val onClick: (viewModule: ChatViewModel, message: MessageItem, clipboardManager: ClipboardManager) -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun getEditOptions(scaffoldState: BottomSheetScaffoldState? = null): List<EditOption> {

    val coroutineScope = rememberCoroutineScope()

    return listOf(

        EditOption(
            text = stringResource(MokoRes.strings.copy),
            imagePath = Res.drawable.chat_copy,
            onClick = { _, message, clipboardManager ->
                message.content?.let { clipboardManager.setText(AnnotatedString(it)) }
            }
        ),
        EditOption(
            text = stringResource(MokoRes.strings.forward),
            imagePath = Res.drawable.chat_forward,
            onClick = { viewModel, message, clipboardManager ->
                coroutineScope.launch {
                    viewModel.setForwardMessage(message)
                    viewModel.setScaffoldState(true)
                }
            }
        ),
        EditOption(
            text = stringResource(MokoRes.strings.delete),
            imagePath = Res.drawable.chat_delete,
            onClick = { viewModel, message, _ ->
                viewModel.deleteMessage(message)
            }
        ),
    )
}


@Composable
fun MessageBox(
    viewModel: ChatViewModel,
    message: MessageItem,
    profile: ProfileDTO,
    messageSenderName: String,
    onClick: () -> Unit,
    onPositioned: (LayoutCoordinates) -> Unit,
    isVisible: Boolean,
    chat: ChatItem
) {
    val isReadByMe = remember { mutableStateOf(false) }
    var swipeOffset by remember { mutableStateOf(0f) }
    val iconOpacity by animateFloatAsState(targetValue = if (swipeOffset > 0) swipeOffset / 75f else 0f)
    val animatedOffset by animateFloatAsState(targetValue = swipeOffset)

//    LaunchedEffect(Unit) {
//        message.phone?.let {
//            val findContact = viewModel.findContactByPhone(it)
//
//            if (findContact != null) {
//                messageSenderName.value = "${findContact.firstName} ${findContact.lastName}"
//            }
//        }
//    }
    val focusManager = LocalFocusManager.current

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

    Column(
        modifier = Modifier
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
                        .padding(5.dp)
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
                            .size(23.dp)
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
                            topStart = 20.dp,
                            topEnd = 20.dp,
                            bottomEnd = if (message.fromUser == profile.id) 0.dp else 20.dp,
                            bottomStart = if (message.fromUser == profile.id) 20.dp else 0.dp,
                        ),
                        shadowElevation = 4.dp,
                        color = if (message.fromUser == profile.id) Color(0xFF2A293C) else Color(0xFFF3F4F6)
                    ) {
                        var messageFormatWidth by remember { mutableStateOf(0) }
//                        var selectedMessageWidth by remember { mutableStateOf(0) }

                        Column(
                            horizontalAlignment = if (message.fromUser == profile.id) Alignment.End else Alignment.Start,
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
                                        .padding(top = 4.dp, start = 4.dp, end = 4.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color.White)
                                        .wrapContentHeight(),
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = if (message.fromUser == profile.id) Arrangement.End else Arrangement.Start
                                ) {
                                    SelectedMessageFormat(
                                        it,
                                        profile,
                                        viewModel
                                    )
                                }
                            }

                            // Проверка на персональный чат
                            if (!chat.personal) {
                                if (message.fromUser != profile.id) {
                                    Text(
                                        text = messageSenderName,
                                        style = TextStyle(
                                            color = Color.Gray,
                                            fontSize = 12.sp,
                                            fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                                        ),
                                        modifier = Modifier.padding(
                                            start = 25.dp,
                                            end = 25.dp,
                                            top = 7.dp,
                                            bottom = 0.dp
                                        ),
                                    )
                                }
                            }

                            // Пересланное сообщение
                            if (message.forwardMessage == true) {
                                Text(
                                    stringResource(MokoRes.strings.forwarded_message),
                                    style = TextStyle(
                                        color = Color.Gray,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                                    ),
                                    modifier = Modifier.padding(
                                        start = 25.dp,
                                        end = 25.dp,
                                        top = 7.dp,
                                        bottom = 0.dp
                                    ),
                                )
                            }

                            // Проверка на персональный чат и наличие имени отправителя
                            if (!chat.personal && messageSenderName.isNotBlank()) {
                                Text(
                                    text = messageSenderName,
                                    style = TextStyle(
                                        color = Color.Gray,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                                    ),
                                    modifier = Modifier.padding(
                                        start = 25.dp,
                                        end = 25.dp,
                                        top = 7.dp,
                                        bottom = 0.dp
                                    ),
                                )
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
        Row(
            horizontalArrangement = if (message.fromUser == profile.id) Arrangement.End else Arrangement.Start,
            modifier = Modifier
                .padding(start = 2.dp, end = 2.dp)
                .fillMaxWidth().clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null // Убирает эффект нажатия
                ) {
                    focusManager.clearFocus() // Ваше действие при нажатии
                }
        ) {
            if (message.fromUser == profile.id)
                if (message.anotherRead) {
                    Image(
                        modifier = Modifier
                            .padding(top = 2.dp, end = 4.dp)
                            .size(14.dp),
                        painter = painterResource(Res.drawable.double_message_check),
                        contentDescription = null,
                    )
                } else {
                    Image(
                        modifier = Modifier
                            .padding(top = 2.dp, end = 4.dp)
                            .size(14.dp),
                        painter = painterResource(Res.drawable.single_message_check),
                        contentDescription = null,
                    )
                }


            if (message.created.isNotEmpty())
                Text(
                    text = formatTimeOnly(message.created),
                    style = TextStyle(
                        color = Color.Gray,
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                    ),
                    modifier = Modifier.padding(),
                )
        }
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
            
            "mp4" -> {
                VideoMessage(
                    message,
                    message.attachments!!,
                    messageSenderName
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

    if (message.attachments == null || message.attachments?.isEmpty() == true) {
        SelectedMessageText(message, messageAnswerName)
    } else {

        when (message.attachments!![0].type) {
            "audio/mp4" -> {
                SelectedVoiceMessage(message, messageAnswerName)
            }

            "image" -> {
                SelectedMessageImage(message.attachments!!, messageAnswerName)

            }

            "video" -> {
                SelectedVideoMessage(message.attachments!!, messageAnswerName)

            }

            else -> {
                SelectedFileMessage(message, messageAnswerName)
            }
        }


    }

}