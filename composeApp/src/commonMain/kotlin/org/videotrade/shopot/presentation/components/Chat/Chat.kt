package org.videotrade.shopot.presentation.components.Chat

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.Font
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.api.formatDateOnly
import org.videotrade.shopot.api.formatTimestamp
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.chat_copy
import shopot.composeapp.generated.resources.chat_delete


data class EditOption(
    val text: String,
    val imagePath: DrawableResource,
    val onClick: (viewModule: ChatViewModel, message: MessageItem, clipboardManager: ClipboardManager) -> Unit,
)

@Composable
fun getEditOptions(): List<EditOption> {
    return listOf(
        EditOption(
            text = stringResource(MokoRes.strings.delete),
            imagePath = Res.drawable.chat_delete,
            onClick = { viewModel, message, _ ->
                viewModel.deleteMessage(message)
            }
        ),
        EditOption(
            text = stringResource(MokoRes.strings.copy),
            imagePath = Res.drawable.chat_copy,
            onClick = { _, message, clipboardManager ->
                message.content?.let { clipboardManager.setText(AnnotatedString(it)) }
            }
        )
    )
}

@OptIn(FlowPreview::class, ExperimentalFoundationApi::class)
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
    val reversedMessagesState = messagesState.reversed()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()


    val groupedMessages = reversedMessagesState.groupBy { message ->
        // Группируем по дате (год, месяц, день), игнорируя время
        val dateOnly = message.created.subList(0, 3)
        formatDateOnly(dateOnly) // Функция, форматирующая только дату
    }

    var headerVisible by remember { mutableStateOf(true) }
    var isScrolling by remember { mutableStateOf(false) }
    val headerAlpha by animateFloatAsState(
        targetValue = if (headerVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 500) // Анимация исчезновения
    )

    LaunchedEffect(reversedMessagesState) {
        if (reversedMessagesState.isNotEmpty()) {
            coroutineScope.launch {
                // Прокручиваем к последнему сообщению (индекс последнего сообщения)
                listState.scrollToItem(reversedMessagesState.size - 1)
            }
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .distinctUntilChanged()
            .collect { scrolling ->
                isScrolling = scrolling
                if (scrolling) {
                    headerVisible = true // Появляется при скролле
                } else {
                    // Плавное исчезновение заголовка через 1 секунду, если заголовок прилип
                    val headerStuck = listState.layoutInfo.visibleItemsInfo.any { it.index == 0 }
                    if (!headerStuck) {
                        delay(1000)
                        headerVisible = false
                    }
                }
            }
    }

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


//        .clickable(
////                    interactionSource = remember { MutableInteractionSource() },
////                    indication = null // Убирает эффект нажатия
//        ) {
//            println("LLLL")
//            focusManager.clearFocus() // Ваше действие при нажатии
//        }

    LazyColumn(
        state = listState,
        reverseLayout = false,
        modifier = modifier.background(Color.White)
    ) {
        groupedMessages.forEach { (date, messages) ->
            stickyHeader {
                // Заголовок с датой с анимацией появления/исчезновения
                Box(
                    modifier = Modifier
                        .padding(vertical = 2.dp)
                        .fillMaxWidth()
                        .graphicsLayer(alpha = headerAlpha), // Применение анимации видимости
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(15.dp))
                            .widthIn(min = 60.dp, max = 140.dp)
                            .height(30.dp)
                            .background(Color.Black.copy(alpha = 0.5f))
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = date,
                            style = TextStyle(
                                color = Color.White,
                                fontSize = 14.sp,
                                fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                            ),
                            color = Color.White
                        )
                    }
                }
            }

            itemsIndexed(messages) { _, message ->
                var messageY by remember { mutableStateOf(0) }
                val isVisible = message.id != hiddenMessageId

                // Инициализируем messageSenderName внутри itemsIndexed
//            val messageSenderName = if (!chat.personal && message.fromUser != profile.id) {
//                println("messageSenderName1 ${message.content} ${message.phone}")
//
//                message.phone?.let {
//                    val findContact = viewModel.findContactByPhone(it)
//                    if (findContact != null) {
//                        "${findContact.firstName} ${findContact.lastName}"
//                    } else {
//                        "+${message.phone}"
//                    }
//                } ?: ""
//            } else {
//                ""
//            }

                val messageSenderName = if (message.fromUser != profile.id) {
                    println("messageSenderName1 ${message.content} ${message.phone}")

                    message.phone?.let {
                        val findContact = viewModel.findContactByPhone(it)
                        if (findContact != null) {
                            "${findContact.firstName} ${findContact.lastName}"
                        } else {
                            "+${message.phone}"
                        }
                    } ?: ""
                } else {
                    ""
                }

                // Отображение сообщения
                MessageBox(
                    viewModel = viewModel,
                    message = message,
                    profile = profile,
                    messageSenderName = messageSenderName,
                    onClick = { onMessageClick(message, messageY) },
                    onPositioned = { coordinates ->
                        messageY = coordinates.positionInParent().y.toInt()
                    },
                    isVisible = isVisible,
                    chat = chat
                )
                MessageBox(
                    viewModel = viewModel,
                    message = message,
                    profile = profile,
                    messageSenderName = messageSenderName,
                    onClick = { onMessageClick(message, messageY) },
                    onPositioned = { coordinates ->
                        messageY = coordinates.positionInParent().y.toInt()
                    },
                    isVisible = isVisible,
                    chat = chat
                )
            }
        }
    }
}




