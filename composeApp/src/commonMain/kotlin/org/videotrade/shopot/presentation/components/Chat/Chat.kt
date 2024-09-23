package org.videotrade.shopot.presentation.components.Chat

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.Font
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.api.formatDateOnly
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.chat_copy
import shopot.composeapp.generated.resources.chat_delete
import shopot.composeapp.generated.resources.chat_forward


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

    if (messagesState.isNotEmpty()) {
        val groupedMessages = messagesState.groupBy { message ->
            message.created.subList(0, 3)
        }

        val numberOfDays = groupedMessages.size
        println("Количество уникальных дней: $numberOfDays")

        LaunchedEffect(listState) {
            snapshotFlow { listState.layoutInfo.visibleItemsInfo }
                .debounce(100) // задержка в 100 миллисекунд
                .distinctUntilChanged()
                .collect { visibleItems ->
                    if (viewModel.messages.value.size > 23) {
                        println("hfpvth ${visibleItems.last().index}")
                        println("hfpvth последний ${viewModel.messages.value.size - 1}")
                        println("hfpvth groupedMessages.size ${viewModel.messages.value.size - 1 + numberOfDays}")
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
            modifier = modifier.background(Color.White)
        ) {

            println("hfpvth ${viewModel.messages.value.size}")
            // Добавляем заголовки с датами и сообщения
            groupedMessages.forEach { (date, messages) ->

                stickyHeader() {
                    // Отображение заголовка с датой
                    DateHeader(date = date)
                }

                items(messages) { message ->
                    var messageY by remember { mutableStateOf(0) }
                    val isVisible = message.id != hiddenMessageId

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
                        chat = chat
                    )
                }


            }
        }
    }
}





