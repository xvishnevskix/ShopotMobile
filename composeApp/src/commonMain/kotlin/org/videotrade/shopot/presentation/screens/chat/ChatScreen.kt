package org.videotrade.shopot.presentation.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import org.videotrade.shopot.domain.model.UserItem
import org.videotrade.shopot.presentation.components.Chat.Chat
import org.videotrade.shopot.presentation.components.Chat.ChatFooter
import org.videotrade.shopot.presentation.components.Chat.ChatHeader
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.koin.compose.koinInject
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.presentation.components.Chat.MessageBox

class ChatScreen(private val chat: UserItem) : Screen {

    @Composable
    override fun Content() {
        val viewModel: ChatViewModel = koinInject()

        var selectedMessage by remember { mutableStateOf<MessageItem?>(null) }

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
                            onMessageClick = { selectedMessage = it }
                        )
                    }
                }
            }

            selectedMessage?.let { message ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable { selectedMessage = null } // Закрываем при клике на затемненный фон
                    ,
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(16.dp)
                    ) {
                        // Показываем выбранное сообщение без размытия
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.Transparent
                        ) {
                            MessageBox(message = message, onClick = { /* Ничего не делаем */ })
                        }
                    }
                }
            }
        }
    }
}