package org.videotrade.shopot.presentation.components.Chat

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.api.formatTimestamp
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.double_message_check
import shopot.composeapp.generated.resources.edit_pencil
import shopot.composeapp.generated.resources.single_message_check
import shopot.composeapp.generated.resources.voice_message_play_dark
import shopot.composeapp.generated.resources.voice_message_play_white
import kotlin.random.Random


//@Composable
//fun Chat(
//    viewModel: ChatViewModel, modifier: Modifier
//) {
//    val messagesState = viewModel.messages.collectAsState(initial = listOf()).value
//
//    val listState = rememberLazyListState()
//
////    LaunchedEffect(messagesState.size) {
////        if (messagesState.isNotEmpty()) {
////            listState.animateScrollToItem(messagesState.lastIndex)
////        }
////    }
//
//    LazyColumn(
//        state = listState,
//        reverseLayout = true, // Makes items start from the bottom
//        modifier = modifier
//    ) {
//        itemsIndexed(messagesState) { index, message ->
//            MessageBox(message)
//        }
//    }
//
//
//}


//@Composable
//fun MessageBox(message: MessageItem) {
//
//
//
//
//    Column {
//        Box(
////        contentAlignment = if (true) Alignment.CenterStart else Alignment.CenterEnd,
//            contentAlignment = if (message.fromUser == profile.id) Alignment.CenterEnd else Alignment.CenterStart,
//            modifier = Modifier
//                .padding(start = 2.dp ,end = 2.dp)
//                .fillMaxWidth()
//                .padding(vertical = 4.dp,)
//        ) {
//
//            if (message.fromUser == profile.id) {
//                Surface(
//                    modifier = Modifier
//                        .wrapContentSize(),
//                    shape = RoundedCornerShape(
//                        topStart = 20.dp,
//                        topEnd = 20.dp,
//                        bottomEnd = 0.dp,
//                        bottomStart = 20.dp
//                    ),
//                    shadowElevation = 4.dp,
//                    color = Color(0xFF2A293C)
//                ) {
//                    Text(
//                        text = message.content,
//                        style = MaterialTheme.typography.bodyLarge,
//                        modifier = Modifier.padding(start = 25.dp, end = 25.dp, top = 13.dp, bottom = 12.dp),
//                        textAlign = TextAlign.Start,
//                        fontSize = 16.sp,
//                        fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
//                        letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
//                        lineHeight = 20.sp,
//                        color = Color(0xFFFFFFFF),
//                    )
//
//                }
//            } else {
//                Surface(
//                    modifier = Modifier
//                        .wrapContentSize(),
//                    shape = RoundedCornerShape(
//                        topStart = 20.dp,
//                        topEnd = 20.dp,
//                        bottomEnd = 20.dp,
//                        bottomStart = 0.dp
//                    ),
//                    shadowElevation = 4.dp,
//                    color = Color(0xFFF3F4F6)
//                ) {
//                    Text(
//                        text = message.content,
//                        style = MaterialTheme.typography.bodyLarge,
//                        modifier = Modifier.padding(start = 25.dp, end = 25.dp, top = 13.dp, bottom = 12.dp),
//                        textAlign = TextAlign.Start,
//                        fontSize = 16.sp,
//                        fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
//                        letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
//                        lineHeight = 20.sp,
//                        color = Color(0xFF29303C),
//                    )
//
//                }
//            }
//        }
//
//        Row(
//            horizontalArrangement = if (message.fromUser == profile.id) Arrangement.End else Arrangement.Start,
//            modifier = Modifier
//                .padding(start = 2.dp ,end = 2.dp)
//                .fillMaxWidth()
//        ) {
//            Image(
//                modifier = Modifier.padding(top = 2.dp, end = 4.dp).size(14.dp),
//                painter = painterResource(Res.drawable.double_message_check),
//                contentDescription = null,
//            )
////                Image(
////                    modifier = Modifier.size(14.dp),
////                    painter = painterResource(Res.drawable.single_message_check),
////                    contentDescription = null,
////                )
//            Text(
//                text = "11:17",
//                style = MaterialTheme.typography.bodyLarge,
//                modifier = Modifier.padding(),
//                textAlign = TextAlign.End,
//                fontSize = 16.sp,
//                fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
//                letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
//                lineHeight = 20.sp,
//                color = Color(0xFF979797),
//            )
//        }
//    }
//}

data class EditOption(
    val text: String,
    val imagePath: DrawableResource,
    val onClick: (viewModule: ChatViewModel, message: MessageItem, clipboardManager: ClipboardManager) -> Unit,
)

val editOptions = listOf(
    EditOption(
        text = "Удалить",
        imagePath = Res.drawable.edit_pencil,
        onClick = { viewModule, message, _ ->
            viewModule.deleteMessage(message)
        }
    ),
    EditOption(
        text = "Копировать",
        imagePath = Res.drawable.edit_pencil,
        onClick = { _, message, clipboardManager ->
            message.content?.let { clipboardManager.setText(AnnotatedString(it)) }
        }
    ),
)

@OptIn(FlowPreview::class)
@Composable
fun Chat(
    viewModel: ChatViewModel,
    profile: ProfileDTO,
    chat: ChatItem,
    modifier: Modifier,
    onMessageClick: (MessageItem, Int) -> Unit,
    hiddenMessageId: String?
) {
    val messagesState = viewModel.messages.collectAsState(initial = listOf()).value
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .debounce(100) // добавляем задержку в 1 секунду
            .distinctUntilChanged()
            .collect { visibleItems ->
                if (viewModel.messages.value.size > 23) {
                    if (visibleItems.isNotEmpty() && visibleItems.last().index == viewModel.messages.value.size - 1) {
                        coroutineScope.launch {
                             viewModel.getMessagesBack(chat.chatId)
                        }
                    }
                }
            }
    }
    
    LazyColumn(
        state = listState,
        reverseLayout = true,
        modifier = modifier,
    ) {
        itemsIndexed(messagesState) { _, message ->
            var messageY by remember { mutableStateOf(0) }
            val isVisible = message.id != hiddenMessageId
            MessageBox(
                viewModel = viewModel,
                message = message,
                profile = profile,
                onClick = { onMessageClick(message, messageY) },
                onPositioned = { coordinates ->
                    messageY = coordinates.positionInParent().y.toInt()
                },
                isVisible = isVisible
            )
            VoiceMessageBox(

                duration = "00:10",


                viewModel = viewModel,
                message = message,
                profile = profile,
                onClick = { onMessageClick(message, messageY) },
                onPositioned = { coordinates ->
                    messageY = coordinates.positionInParent().y.toInt()
                },
                isVisible = isVisible
            )
        }
    }
}
@Composable
fun MessageFormat(message: MessageItem, profile: ProfileDTO) {
    if (message.attachments == null || message.attachments?.isEmpty() == true) {
        MessageText(message, profile)
    } else {
        MessageImage(message, profile)
    }

    //           TODO     VoiceMessageFormat(message, profile)
}


