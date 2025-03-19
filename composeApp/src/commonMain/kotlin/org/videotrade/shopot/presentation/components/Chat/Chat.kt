package org.videotrade.shopot.presentation.components.Chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.api.formatDateOnly
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.arrow_left
import shopot.composeapp.generated.resources.auth_logo
import shopot.composeapp.generated.resources.chat_copy
import shopot.composeapp.generated.resources.chat_delete
import shopot.composeapp.generated.resources.chat_forward
import shopot.composeapp.generated.resources.logo
import shopot.composeapp.generated.resources.smart_lock


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
    val totalMessages = messagesState.size
    val initialIndex = if (chat.unread > 0) {
        maxOf(0, totalMessages + chat.unread)
    } else {
        0
    }
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialIndex
    )
    val coroutineScope = rememberCoroutineScope()
    val colors = MaterialTheme.colorScheme
    var isScrolling by remember { mutableStateOf(false) }
    var shouldShowHeader by remember { mutableStateOf(false) }
    val answerMessageId = remember { mutableStateOf<String?>(null) }

    var isLoading by remember { mutableStateOf(true) }
    var isVisible by remember { mutableStateOf(false) }
    var numberOfDays by remember { mutableStateOf(0) }
    val largestNumberMessages = if (chat.unread > 23 ) chat.unread else 23


    var shouldShowScrollToBottom by remember { mutableStateOf(false) }
    var lastScrollDirection by remember { mutableStateOf(0) } // 1 - вниз, -1 - вверх
    var previousIndex by remember { mutableStateOf(listState.firstVisibleItemIndex) }
    var hasCheckedInitialScroll by remember { mutableStateOf(false) }



    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { index ->
                // Определяем направление скролла
                lastScrollDirection = when {
                    index > previousIndex -> -1   // Скроллим вниз
                    index < previousIndex ->  1 // Скроллим вверх
                    else -> lastScrollDirection
                }
                previousIndex = index
                if (!hasCheckedInitialScroll) {
                    if (index > 0) {
                        shouldShowScrollToBottom = listState.firstVisibleItemIndex > 0
                        hasCheckedInitialScroll = true // Отключаем повторную проверку
                    }
                } else {
                    shouldShowScrollToBottom = index > 1 && lastScrollDirection == 1

                }
                // Показываем кнопку, если:
            }
    }


    Box( ) {
        if (messagesState.isNotEmpty()) {
            val groupedMessages = messagesState.groupBy { message ->
                message.created.subList(0, 3)
            }



            numberOfDays = groupedMessages.values.size

            LaunchedEffect(listState) {
                snapshotFlow { listState.isScrollInProgress }
                    .collect { scrolling ->
                        isScrolling = scrolling
                        if (scrolling) {
                            shouldShowHeader = true
                            delay(2000)
                        } else {
                            shouldShowHeader = false
                        }
                    }
            }



            LaunchedEffect(listState) {
                snapshotFlow { listState.layoutInfo.visibleItemsInfo }
                    .debounce(100)
                    .distinctUntilChanged()
                    .collect { visibleItems ->
                        if (viewModel.messages.value.size > largestNumberMessages) {

                            val totalItems = viewModel.messages.value.size + numberOfDays

                            if (visibleItems.isNotEmpty() && visibleItems.last().index == totalItems - 1) {

                                coroutineScope.launch {
                                    isLoading = true
                                    delay(800)
                                    viewModel.getMessagesBack(chat.chatId)

                                }
                            } else {
                                delay(300)
                                isLoading = false
                            }
                        }
                    }
            }


            LazyColumn(
                state = listState,
                reverseLayout = true,
                modifier = modifier.background(colors.background).padding(horizontal = 8.dp)
            ) {
                groupedMessages.forEach { (date, messages) ->

                    stickyHeader {
                        val alpha by animateFloatAsState(
                            targetValue = if (isScrolling) 1f else 0f,
                            animationSpec = tween(durationMillis = 500)
                        )

                        DateHeader(
                            date = date,
                            modifier = Modifier.alpha(alpha)
                        )
                    }

                    itemsIndexed(messages, key = { index, message -> "${message.id}-$index" }) { index, message ->
//                itemsIndexed(messages, key = { index, message -> "${message.id}-$index" }) { index, message ->




                        var messageY by remember { mutableStateOf(0) }
                        val isVisible = remember { message.id != hiddenMessageId }

                        // Определяем имя отправителя сообщения
                        val messageSenderName = if (message.fromUser == profile.id) {
                            stringResource(MokoRes.strings.you)
                        } else {
                            message.phone?.let {
                                val findContact = viewModel.findContactByPhone(it)
                                if (findContact != null) {
                                    "${findContact.firstName} ${findContact.lastName}"
                                } else {
                                    "+${message.phone}"
                                }
                            } ?: ""
                        }



                        MessageBox(
                            viewModel = viewModel,
                            message = message,
                            profile = profile,
                            messageSenderName = messageSenderName,
                            onClick = {
                                onMessageClick(message, messageY)
                            },
                            onPositioned = { coordinates ->
                                messageY = coordinates.positionInParent().y.toInt()
                            },
                            isVisible = isVisible,
                            chat = chat,
                            answerMessageId = answerMessageId,
                            coroutineScope = coroutineScope,
                            listState = listState,
                            messagesState = messagesState
                        )
                    }

                }
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFFCAB7A3),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
            }




        } else {
            EmptyChat()
        }


        val offsetY by animateDpAsState(
            targetValue = if (shouldShowScrollToBottom) (-160).dp else (0).dp, // Поднимаем кнопку при появлении
            animationSpec = tween(durationMillis = 500)
        )

        AnimatedVisibility(
            visible = shouldShowScrollToBottom,
            enter = fadeIn(animationSpec = tween(200)) + slideInVertically { it }, // Появление снизу вверх
            exit = fadeOut(animationSpec = tween(500)) + slideOutVertically { height -> height * 2 }, // Исчезновение вверх
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            Box(
                modifier = Modifier
                    .zIndex(1f)
                    .align(Alignment.BottomEnd)
                    .offset(x = (-8).dp, y = offsetY) // 🔹 Плавное смещение вверх-вниз
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null // Убирает эффект нажатия
                    ) {
                        coroutineScope.launch {
                            listState.animateScrollToItem(0)
                        }
                    }
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 40.dp, height = 40.dp)
                        .clip(CircleShape)
                        .background(colors.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        modifier = Modifier
                            .rotate(270f)
                            .size(width = 9.dp, height = 18.dp),
                        painter = painterResource(Res.drawable.arrow_left),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        colorFilter = ColorFilter.tint(colors.background)
                    )
                }
            }
        }
    }
}




