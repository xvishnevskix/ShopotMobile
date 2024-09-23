package org.videotrade.shopot.presentation.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import org.videotrade.shopot.presentation.components.Common.BottomSheetModal


class ChatScreen(
) : Screen {
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val viewModel: ChatViewModel = koinInject()
        val mainViewModel: MainViewModel = koinInject()
        val profile = viewModel.profile.collectAsState(initial = ProfileDTO()).value
        val chat = viewModel.currentChat.collectAsState().value
        val isScaffoldState = viewModel.isScaffoldState.collectAsState().value
        val scaffoldState = rememberBottomSheetScaffoldState()
        
        if (chat == null) {
            mainViewModel.navigator.value?.push(ChatsScreen())
            return
        }
        
        LaunchedEffect(isScaffoldState) {
         if(isScaffoldState) {
             scaffoldState.bottomSheetState.expand()
         }
        }
        
        
        LaunchedEffect(key1 = viewModel) {
            viewModel.getProfile()
            viewModel.getMessagesBack(chat.id)
            
        }
        
        DisposableEffect(Unit) {
            onDispose {
                viewModel.clearSelection(chatId = chat.chatId)
                
                if (
                    viewModel.isRecording.value
                ) {
                    viewModel.audioRecorder.value.stopRecording(false)
                }
                viewModel.clearMessages()
                mainViewModel.setCurrentChat("")
            }
        }
        
        
        val selectedMessage = remember { mutableStateOf<MessageItem?>(null) }
        var selectedMessageY by remember { mutableStateOf(0) }
        var hiddenMessageId by remember { mutableStateOf<String?>(null) }
        
        
        Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
            SafeArea(isBlurred = selectedMessage.value != null, 7.dp) {
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
                            Modifier.fillMaxSize().background(Color.White)
                                .padding(innerPadding)
                            ,
                            onMessageClick = { message, y ->
                                selectedMessage.value  = message
                                selectedMessageY = y + 150
                                hiddenMessageId = message.id
                            },
                            hiddenMessageId = hiddenMessageId
                        )
                    }
                    

                }
                BottomSheetModal(scaffoldState)
            }
            
            BlurredMessageOverlay(
                profile,
                viewModel,
                selectedMessage = selectedMessage.value ,
                selectedMessageY = selectedMessageY,
                onDismiss = {
                    selectedMessage.value  = null
                    hiddenMessageId = null
                },
            )
        }
    }
}