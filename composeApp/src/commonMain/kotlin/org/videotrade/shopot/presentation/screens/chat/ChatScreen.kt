package org.videotrade.shopot.presentation.screens.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.domain.model.UserItem
import org.videotrade.shopot.presentation.components.Chat.Chat
import org.videotrade.shopot.presentation.components.Chat.ChatFooter
import org.videotrade.shopot.presentation.components.Chat.ChatHeader
import org.videotrade.shopot.presentation.components.Common.SafeArea

class ChatScreen(private val chat: UserItem) : Screen {
    
    @Composable
    override fun Content() {
        val viewModel: ChatViewModel = koinInject()
        
   
        
        LaunchedEffect(key1 = viewModel) {
            viewModel.wsConnect()
            
            
            delay(2000)
            viewModel.getMessagesBack(chat.chatId)
        }
        
        
        
 
        
        
        SafeArea {
            
     
            
            Scaffold(
                topBar = { ChatHeader(chat) },
                bottomBar = { ChatFooter(viewModel) },
                modifier = Modifier
                    .fillMaxSize()
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                ) {
                    Chat(chat, viewModel)
                }
            }
            
        }
        
        
    }
}






