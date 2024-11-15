package org.videotrade.shopot.presentation.screens.chat


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.TopAppBar
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.presentation.components.Chat.BlurredMessageOverlay
import org.videotrade.shopot.presentation.components.Chat.Chat
import org.videotrade.shopot.presentation.components.Chat.ChatFooter
import org.videotrade.shopot.presentation.components.Chat.ChatHeader
import org.videotrade.shopot.presentation.components.Chat.StickerMenuContent
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.screens.chats.ChatsScreen
import org.videotrade.shopot.presentation.screens.main.MainViewModel
import org.videotrade.shopot.presentation.components.Common.BottomSheetModal


class ChatScreen(
) : Screen {

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {
        val viewModel: ChatViewModel = koinInject()
        val mainViewModel: MainViewModel = koinInject()
        val profile = viewModel.profile.collectAsState(initial = ProfileDTO()).value
        val chat = viewModel.currentChat.collectAsState().value
        val isScaffoldForwardState = viewModel.isScaffoldForwardState.collectAsState().value
        val isScaffoldStickerState = viewModel.isScaffoldStickerState.collectAsState().value
        val scaffoldForwardState = rememberBottomSheetScaffoldState()
        val scaffoldStickerState = rememberBottomSheetScaffoldState()
        var showStickerMenu = remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()

        if (chat == null) {
            mainViewModel.navigator.value?.push(ChatsScreen())
            return
        }

        LaunchedEffect(isScaffoldForwardState) {
            if (isScaffoldForwardState) {
                scaffoldForwardState.bottomSheetState.expand()
            }

        }

        LaunchedEffect(isScaffoldStickerState) {
            if (isScaffoldStickerState) {
                scaffoldStickerState.bottomSheetState.expand()
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

                mainViewModel.setCurrentChat("")
            }
        }


        val selectedMessage = remember { mutableStateOf<MessageItem?>(null) }
        var selectedMessageY by remember { mutableStateOf(0) }
        var hiddenMessageId by remember { mutableStateOf<String?>(null) }


            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .pointerInput(Unit) {
                        detectTapGestures {
                            scope.launch {
                                if (scaffoldStickerState.bottomSheetState.isVisible) {
                                    scaffoldStickerState.bottomSheetState.partialExpand()
                                    showStickerMenu.value = false
                                }
                            }
                        }
                    }
            ) {
                SafeArea(isBlurred = selectedMessage.value != null, 0.dp) {
                    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
                        Scaffold(
                            topBar = {
                                ChatHeader(chat, viewModel, profile)

                            },
                            bottomBar = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .shadow(
                                            elevation = 20.dp, spotColor = Color.Black, ambientColor = Color.Black,
                                            shape = RectangleShape, // Прямоугольная форма тени
                                            clip = false // Тень выходит за границы элемента
                                        )
                                ) {
                                    ChatFooter(
                                        chat,
                                        viewModel,
                                        showStickerMenu,
                                        onStickerButtonClick = {
                                            showStickerMenu.value = true
                                            scope.launch {
                                                scaffoldStickerState.bottomSheetState.expand()
                                            }
                                        })
                                }
                            },
                            containerColor = Color.White,

                            modifier = Modifier
                                .fillMaxSize().background(Color.White)
                        ) { innerPadding ->
                            Chat(
                                viewModel,
                                profile,
                                chat,
                                Modifier.fillMaxSize().background(Color.White)
                                    .padding(innerPadding),
                                onMessageClick = { message, y ->
                                    selectedMessage.value = message
                                    selectedMessageY = y + 150
                                    hiddenMessageId = message.id
                                },
                                hiddenMessageId = hiddenMessageId
                            )
                        }


                    }
                    //пересылка сообщений
                    BottomSheetModal(scaffoldForwardState)
                    //стикеры
                    BottomSheetScaffold(
                        modifier = Modifier.background(Color(0xFFF7F7F7)),
                        containerColor = Color(0xFFF7F7F7),
                        sheetContainerColor = Color(0xFFF7F7F7),
                        scaffoldState = scaffoldStickerState,
                        sheetContent = {
                            if (showStickerMenu.value) {
                                StickerMenuContent(chat) {
                                    // Функция, которая закроет BottomSheet при выборе стикера
                                    scope.launch {
                                        scaffoldStickerState.bottomSheetState.partialExpand()
                                        showStickerMenu.value = false
                                    }
                                }
                            }
                        },
                        sheetPeekHeight = 0.dp,
                    ) {}
                }

                BlurredMessageOverlay(
                    profile,
                    viewModel,
                    selectedMessage = selectedMessage.value,
                    selectedMessageY = selectedMessageY,
                    onDismiss = {
                        selectedMessage.value = null
                        hiddenMessageId = null
                    },
                )
            }
        }
    }


