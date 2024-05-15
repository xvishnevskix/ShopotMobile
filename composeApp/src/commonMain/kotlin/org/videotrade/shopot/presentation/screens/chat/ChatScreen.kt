package org.videotrade.shopot.presentation.screens.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import org.koin.compose.koinInject
import org.videotrade.shopot.domain.model.UserItem
import org.videotrade.shopot.presentation.components.Chat.Chat
import org.videotrade.shopot.presentation.components.Chat.ChatFooter
import org.videotrade.shopot.presentation.components.Chat.ChatHeader

class ChatScreen(private val chat: UserItem) : Screen {
    
    @Composable
    override fun Content() {
        val viewModel: ChatViewModel = koinInject()
        
        Scaffold(
            topBar = { ChatHeader(chat) },
            bottomBar = { ChatFooter(viewModel) },
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding() // Обеспечивает отступ сверху
        ) {innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
            ) {
                Chat(chat, viewModel, Modifier.fillMaxSize())
            }
        }
        
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatHeaderr(chat: UserItem) {
    TopAppBar(
        title = { Text(text = "dadada") },
        navigationIcon = {
            IconButton(onClick = { /* Handle back press */ }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding() // Поддерживает статус-бар
    )
}
