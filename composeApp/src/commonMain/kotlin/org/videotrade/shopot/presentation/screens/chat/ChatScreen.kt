package org.videotrade.shopot.presentation.screens.chat


import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.TopAppBar
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
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
        val boxSelectedMessageHeight = viewModel.boxHeight.collectAsState()
        val colors = MaterialTheme.colorScheme
        val selectedMessage = remember { mutableStateOf<MessageItem?>(null) }
        var selectedMessageY = remember { mutableStateOf(0) }
        var hiddenMessageId = remember { mutableStateOf<String?>(null) }

        val isMessageUpdated = remember { mutableStateOf(false) }


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






            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors.background)
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

                val density = LocalDensity.current
                val screenHeightInPx = maxHeight.value * density.density // Пример, если maxHeight в Dp

//                // Преобразуем пиксели в Dp
//                val screenHeightInDp = with(density) {
//                    screenHeightInPx.toDp()
//                }

//                println("screenHeightInPx ${screenHeightInPx}")
//
//                println("screenHeightInPx Box height in pixels: ${boxSelectedMessageHeight.value}")
//
//                println("screenHeightInPx selectedMessageY ${selectedMessageY.value}")

                SafeArea(isBlurred = selectedMessage.value != null, 0.dp) {
                    Column(modifier = Modifier.fillMaxSize().background(colors.background)) {
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
                            containerColor = colors.background,

                            modifier = Modifier
                                .fillMaxSize().background(colors.background)
                        ) { innerPadding ->
                            Chat(
                                viewModel,
                                profile,
                                chat,
                                Modifier.fillMaxSize().background(colors.background)
                                    .padding(innerPadding),
                                onMessageClick = { message, y ->
                                    // Сбрасываем текущее состояние для предотвращения конфликта
                                    selectedMessage.value = null
                                    selectedMessageY.value = 0
                                    isMessageUpdated.value = false

                                    scope.launch {

                                        kotlinx.coroutines.delay(16)

                                        // Устанавливаем новое состояние
                                        val calculatedY = when {
                                            boxSelectedMessageHeight.value + y + 180 > screenHeightInPx -> {
                                                (screenHeightInPx - boxSelectedMessageHeight.value - 180).toInt()
                                            }
                                            y < 0 -> 150
                                            else -> y + 100
                                        }

                                        selectedMessage.value = message
                                        selectedMessageY.value = calculatedY
                                        hiddenMessageId.value = message.id
                                        isMessageUpdated.value = true
                                    }
                                    // Сбрасываем текущее состояние для предотвращения конфликта
                                    selectedMessage.value = null
                                    selectedMessageY.value = 0
                                    isMessageUpdated.value = false

                                    scope.launch {

                                        kotlinx.coroutines.delay(16)

                                        // Устанавливаем новое состояние
                                        val calculatedY = when {
                                            boxSelectedMessageHeight.value + y > screenHeightInPx -> {
                                                (screenHeightInPx - boxSelectedMessageHeight.value - 180).toInt()
                                            }
                                            y < 0 -> 150
                                            else -> y + 100
                                        }

                                        selectedMessage.value = message
                                        selectedMessageY.value = calculatedY
                                        hiddenMessageId.value = message.id
                                        isMessageUpdated.value = true
                                    }
                                },
                                hiddenMessageId = hiddenMessageId.value
                            )
                        }


                    }
                    //пересылка сообщений
                    BottomSheetModal(scaffoldForwardState)
                    //стикеры
                    BottomSheetScaffold(
                        modifier = Modifier.background(colors.surface),
                        containerColor = colors.surface,
                        sheetContainerColor = colors.surface,
                        sheetShape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                        sheetDragHandle = {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(36.dp)
                                    .background(color = colors.surface, shape = RoundedCornerShape(topEnd = 30.dp, topStart = 30.dp))
                                    .align(Alignment.Center)
                                ,
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    Modifier.padding(top = 11.dp).height(3.5.dp).width(35.dp).background(color = colors.secondary, shape = RoundedCornerShape(12.dp))
                                )
                            }
                        },
                        sheetContentColor = colors.surface,
                        sheetShadowElevation = 16.dp,
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

                if (isMessageUpdated.value && selectedMessage.value != null) {
                    val overlayPosition = selectedMessageY.value
                    val isWithinBounds = overlayPosition >= 0 &&
                            overlayPosition + boxSelectedMessageHeight.value <= screenHeightInPx

                    if (isWithinBounds) {
                        BlurredMessageOverlay(
                            chat = chat,
                            profile = profile,
                            viewModel = viewModel,
                            selectedMessage = selectedMessage.value,
                            selectedMessageY = selectedMessageY.value,
                            onDismiss = {
                                selectedMessage.value = null
                                hiddenMessageId.value = null
                            }
                        )
                    } else {

                        scope.launch {
                            selectedMessageY.value = when {
                                overlayPosition < 0 -> 0 // Если выходит за верхнюю границу
                                overlayPosition + boxSelectedMessageHeight.value > screenHeightInPx -> {
                                    (screenHeightInPx - boxSelectedMessageHeight.value).toInt() -180 // нижняя граница
                                }
                                else -> overlayPosition
                            }
                            isMessageUpdated.value = true // Перезапуск
                        }
                    }
                }
                Crossfade(targetState = selectedMessage.value) { message ->
                    if (message != null && isMessageUpdated.value) {
                        val overlayPosition = selectedMessageY.value
                        val isWithinBounds = overlayPosition >= 0 &&
                                overlayPosition + boxSelectedMessageHeight.value + 180 <= screenHeightInPx

                        if (isWithinBounds) {
                            BlurredMessageOverlay(
                                chat = chat,
                                profile = profile,
                                viewModel = viewModel,
                                selectedMessage = message,
                                selectedMessageY = selectedMessageY.value,
                                onDismiss = {
                                    selectedMessage.value = null
                                    hiddenMessageId.value = null
                                }
                            )
                        } else {
                            scope.launch {
                                selectedMessageY.value = when {
                                    overlayPosition < 0 -> 0 // Если выходит за верхнюю границу
                                    overlayPosition + boxSelectedMessageHeight.value > screenHeightInPx -> {
                                        (screenHeightInPx - boxSelectedMessageHeight.value).toInt() - 180 // нижняя граница
                                    }
                                    else -> overlayPosition
                                }
                                isMessageUpdated.value = true // Перезапуск
                            }
                        }
                    }
            }
        }
    }}


