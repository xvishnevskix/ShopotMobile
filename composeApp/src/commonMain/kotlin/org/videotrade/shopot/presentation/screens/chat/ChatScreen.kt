package org.videotrade.shopot.presentation.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import org.koin.compose.koinInject
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.presentation.components.Chat.BlurredMessageOverlay
import org.videotrade.shopot.presentation.components.Chat.Chat
import org.videotrade.shopot.presentation.components.Chat.ChatFooter
import org.videotrade.shopot.presentation.components.Chat.ChatHeader
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.screens.chats.ChatsScreen
import org.videotrade.shopot.presentation.screens.main.MainViewModel
import org.videotrade.shopot.multiplatform.FileData
import org.videotrade.shopot.multiplatform.hideKeyboardOnTap


class ChatScreen(
//    private val chat: ChatItem
) : Screen {
    
    @Composable
    override fun Content() {
        val viewModel: ChatViewModel = koinInject()
        val mainViewModel: MainViewModel = koinInject()
        val profile = viewModel.profile.collectAsState(initial = ProfileDTO()).value
        val chat = viewModel.currentChat.collectAsState().value
        
        if (chat == null) {
            mainViewModel.navigator.value?.push(ChatsScreen())
            return
        }
        
        
        LaunchedEffect(key1 = viewModel) {
            
            viewModel.getProfile()
            viewModel.getMessagesBack(chat.id)
            
        }
        
        DisposableEffect(Unit) {
            onDispose {
                
                if (
                    viewModel.isRecording.value
                ) {
                    viewModel.audioRecorder.value.stopRecording(false)
                }
                
                mainViewModel.setCurrentChat("")
            }
        }
        
        
        var selectedMessage by remember { mutableStateOf<MessageItem?>(null) }
        var selectedMessageY by remember { mutableStateOf(0) }
        var hiddenMessageId by remember { mutableStateOf<String?>(null) }
        
        Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
            SafeArea(isBlurred = selectedMessage != null, 7.dp) {
                Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
                    
                    Scaffold(
                        topBar = {
                            ChatHeader(chat, viewModel, profile)
                            
                        },
                        bottomBar = {
                            ChatFooter(chat, viewModel)
                        },
                        
                        modifier = Modifier
                            .fillMaxSize()
                    ) { innerPadding ->
                        Chat(
                            viewModel,
                            profile,
                            chat,
                            Modifier.fillMaxSize().hideKeyboardOnTap().background(Color.White)
                                .padding(innerPadding)
                               
                            ,
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
            
            BlurredMessageOverlay(
                profile,
                viewModel,
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