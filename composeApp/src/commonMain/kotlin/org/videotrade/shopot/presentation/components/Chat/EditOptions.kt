package org.videotrade.shopot.presentation.components.Chat

import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.text.AnnotatedString
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.chat_forward
import shopot.composeapp.generated.resources.menu_copy
import shopot.composeapp.generated.resources.menu_delete

data class EditOption(
    val text: String,
    val imagePath: DrawableResource,
    val onClick:
        (
        viewModule: ChatViewModel,
        message: MessageItem,
        clipboardManager: ClipboardManager,

    ) -> Unit,
    val chatId: String = "",
    val messageSenderName: String = "",
    val modifier: Modifier = Modifier,
    val color: Color = Color(0xFF373533),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun getChatEditOptions(
    scaffoldState: BottomSheetScaffoldState? = null,
    chatId: String,
    messageSenderName: String,
    onDismiss: () -> Unit,
    message: MessageItem? = null,
): List<EditOption> {

    val coroutineScope = rememberCoroutineScope()

    return buildList {
        add(
            EditOption(
                text = stringResource(MokoRes.strings.reply),
                imagePath = Res.drawable.chat_forward,
                onClick = { viewModel, message, _ ->
                    viewModel.selectMessage(chatId, message, messageSenderName)
                    onDismiss()
                },
                modifier = Modifier.graphicsLayer(scaleX = -1f)
            )
        )
        if (message != null) {
            if (message.attachments == null || message.attachments?.isEmpty() == true) {
                add(
                    EditOption(
                        text = stringResource(MokoRes.strings.copy),
                        imagePath = Res.drawable.menu_copy,
                        onClick = { _, message, clipboardManager ->
                            message.content?.let { clipboardManager.setText(AnnotatedString(it)) }
                            onDismiss()
                        }
                    )
                )
            }
        }

        add(
            EditOption(
                text = stringResource(MokoRes.strings.forward),
                imagePath = Res.drawable.chat_forward,
                onClick = { viewModel, message, clipboardManager ->
                    coroutineScope.launch {
                        viewModel.setForwardMessage(message)
                        viewModel.setScaffoldState(true)
                        onDismiss()
                    }
                }
            )
        )
        if (messageSenderName == stringResource(MokoRes.strings.you)) {
            add(
                EditOption(
                    text = stringResource(MokoRes.strings.delete),
                    imagePath = Res.drawable.menu_delete,
                    onClick = { viewModel, message, _ ->
                        viewModel.showDeleteConfirmation(message)
                    },
                    color = Color(0xFFFF3B30)
                )
            )
        }
    }
}