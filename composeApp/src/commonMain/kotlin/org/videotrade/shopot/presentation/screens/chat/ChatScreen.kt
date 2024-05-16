package org.videotrade.shopot.presentation.screens.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.delay
import org.koin.compose.koinInject
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.domain.model.UserItem
import org.videotrade.shopot.presentation.components.Chat.BlurredMessageOverlay
import org.videotrade.shopot.presentation.components.Chat.Chat
import org.videotrade.shopot.presentation.components.Chat.ChatFooter
import org.videotrade.shopot.presentation.components.Chat.ChatHeader
import org.videotrade.shopot.presentation.components.Common.SafeArea

class ChatScreen(
    private val chat: UserItem = UserItem(
        "1",
        false,
        "",
        "",
        "",
        "",
        0,
        "",
        "",
        "306e5bbb-e2db-4480-9f85-ca0a4b1b7a0b",
    )
) : Screen {
    
    @Composable
    override fun Content() {
        val viewModel: ChatViewModel = koinInject()
        val profile = viewModel.profile.collectAsState(initial = ProfileDTO("1")).value
        
        
        LaunchedEffect(key1 = viewModel) {
            viewModel.wsConnect()
            
            
            delay(2000)
            viewModel.getMessagesBack(chat.chatId)
        }
        
        
        var selectedMessage by remember { mutableStateOf<MessageItem?>(null) }
        var selectedMessageY by remember { mutableStateOf(0) }
        var hiddenMessageId by remember { mutableStateOf<String?>(null) }
        
        Box(modifier = Modifier.fillMaxSize()) {
            SafeArea(isBlurred = selectedMessage != null) {
                Column(modifier = Modifier.fillMaxSize()) {
                    
                    Scaffold(
                        topBar = {
                            ChatHeader(chat)
                        
                        },
                        bottomBar = {
                            ChatFooter(viewModel)
                        }
                    ) { innerPadding ->
                        if (profile != null) {
                            Chat(
                                viewModel,
                                
                                profile,
                                
                                Modifier
                                    .weight(1f)
                                    .padding(innerPadding),
                                onMessageClick = { message, y ->
                                    selectedMessage = message
                                    selectedMessageY = y + 150
                                    hiddenMessageId = message.id
                                },
                                hiddenMessageId = hiddenMessageId
                            )
                        }
                    }
                }
            }
            
            if (profile != null) {
                BlurredMessageOverlay(
                    profile,
                    selectedMessage = selectedMessage,
                    selectedMessageY = selectedMessageY,
                    onDismiss = {
                        selectedMessage = null
                        hiddenMessageId = null
                    }
                )
            }
        }
    }
}