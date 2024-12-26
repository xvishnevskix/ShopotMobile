package org.videotrade.shopot.presentation.components.Chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.graphics.Color
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
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val colors = MaterialTheme.colorScheme
    var isScrolling by remember { mutableStateOf(false) }
    var shouldShowHeader by remember { mutableStateOf(false) }
    val answerMessageId = remember { mutableStateOf<String?>(null) }

    // TODO надо будет сделать загрузку при запросе сообщений
    var isVisible by remember { mutableStateOf(false) }




    if (messagesState.isNotEmpty()) {
        val groupedMessages = messagesState.groupBy { message ->
            message.created.subList(0, 3)
        }
        val numberOfDays = groupedMessages.size


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
                    if (viewModel.messages.value.size > 23) {
                        if (visibleItems.isNotEmpty() && visibleItems.last().index == viewModel.messages.value.size - 1 + numberOfDays) {
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
                
                items(messages, key = { message -> message.id }) { message ->

                    println("Message ID: ${message.id}")


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
        }


    } else {
        if (isVisible) {
            Column(
                modifier = Modifier.fillMaxSize(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    modifier = Modifier.size(width =  128.dp, height = 86.dp),
                    painter = painterResource(Res.drawable.auth_logo),
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.height(40.dp))
                Text(
                    "Сообщений пока нет...",
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp,
                    lineHeight = 24.sp,
                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                    fontWeight = FontWeight(500),
                    color = Color(0xFF373533),
                    letterSpacing = TextUnit(0F, TextUnitType.Sp),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Отправьте сообщение",
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                    fontWeight = FontWeight(400),
                    color = Color(0x80373533),
                    letterSpacing = TextUnit(0F, TextUnitType.Sp),
                )
            }
        }
    }
}




