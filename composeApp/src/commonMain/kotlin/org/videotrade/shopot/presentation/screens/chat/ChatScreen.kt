package org.videotrade.shopot.presentation.screens.chat

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import org.videotrade.shopot.domain.model.UserItem
import org.videotrade.shopot.presentation.components.Chat.Chat
import org.videotrade.shopot.presentation.components.Chat.ChatFooter
import org.videotrade.shopot.presentation.components.Chat.ChatHeader
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.koin.compose.koinInject
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.presentation.components.Chat.BlurredMessageOverlay
import org.videotrade.shopot.presentation.components.Chat.MessageBlurBox
import org.videotrade.shopot.presentation.components.Chat.MessageBox

class ChatScreen(private val chat: UserItem) : Screen {

    @Composable
    override fun Content() {
        val viewModel: ChatViewModel = koinInject()

        var selectedMessage by remember { mutableStateOf<MessageItem?>(null) }
        var selectedMessageY by remember { mutableStateOf(0) }

        Box(modifier = Modifier.fillMaxSize()) {
            SafeArea(isBlurred = selectedMessage != null) {
                Column(modifier = Modifier.fillMaxSize()) {
                    ChatHeader(chat)

                    Scaffold(
                        bottomBar = {
                            ChatFooter(viewModel)
                        }
                    ) { innerPadding ->
                        Chat(
                            viewModel, Modifier
                                .weight(1f)
                                .padding(innerPadding),
                            onMessageClick = { message, y ->
                                selectedMessage = message
                                selectedMessageY = y + 145
                            }
                        )
                    }
                }
            }

            BlurredMessageOverlay(
                selectedMessage = selectedMessage,
                selectedMessageY = selectedMessageY,
                onDismiss = { selectedMessage = null }
            )
        }
    }
}