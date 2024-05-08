package org.videotrade.shopot.presentation.screens.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import org.videotrade.shopot.domain.model.UserItem
import org.videotrade.shopot.presentation.components.Chat.Chat
import org.videotrade.shopot.presentation.components.Chat.ChatFooter
import org.videotrade.shopot.presentation.components.Chat.ChatHeader
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.koin.compose.koinInject

class ChatScreen(private val chat: UserItem) : Screen {
    
    
    @Composable
    override fun Content() {
        val viewModel: ChatViewModel = koinInject()
        
        SafeArea {
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
                            .padding(innerPadding)
                    )
                }
                
                
            }
        }
    }
}