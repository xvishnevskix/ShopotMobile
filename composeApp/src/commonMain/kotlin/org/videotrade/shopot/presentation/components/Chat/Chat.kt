package org.videotrade.shopot.presentation.components.Chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import shopot.composeapp.generated.resources.Res
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
//        .clickable(
////                    interactionSource = remember { MutableInteractionSource() },
////                    indication = null // Убирает эффект нажатия
//        ) {
//            println("LLLL")
//            focusManager.clearFocus() // Ваше действие при нажатии
//        }
    
    LazyColumn(
        state = listState,
        reverseLayout = true,
        modifier = modifier.background(Color.White)

    ) {
        itemsIndexed(messagesState) { _, message ->
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

//            println("messageSenderName $messageSenderName ${message.content}")
            MessageBox(
                viewModel = viewModel,
                message = message,
                profile = profile,
                messageSenderName = messageSenderName,  // Передаем значение напрямую
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




