package org.videotrade.shopot.presentation.components.Common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.Font
import org.koin.compose.koinInject
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.presentation.components.Main.ForwardingComponentItem
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.main.MainViewModel
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Medium

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun BottomSheetModal(scaffoldState: BottomSheetScaffoldState) {
    val chatViewModel: ChatViewModel = koinInject()
    val mainViewModel: MainViewModel = koinInject()
    val commonViewModel: CommonViewModel = koinInject()
    val colors = MaterialTheme.colorScheme
    
    val chatState = mainViewModel.chats.collectAsState(initial = listOf()).value
    
    LaunchedEffect(scaffoldState.bottomSheetState) {
        snapshotFlow { scaffoldState.bottomSheetState.currentValue }
            .collectLatest { state ->
                if (state == SheetValue.PartiallyExpanded) {
                    chatViewModel.setScaffoldState(false)
                }
            }
    }
    
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContainerColor = colors.surface,
        sheetContent = {
            // Контент модального окна
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.surface)
                    .height(800.dp) // Высота окна
                    .padding(16.dp)
            ) {
//                BasicTextField(
//                    value = text,
//                    onValueChange = { newText -> text = newText },
//                )
//                Spacer(modifier = Modifier.height(40.dp))
                
                
                LazyColumn(
                    modifier = Modifier,
                    verticalArrangement = Arrangement.Center,
                ) {
                    items(chatState) { item ->
                        ForwardingComponentItem(
                            item,
                            commonViewModel,
                            mainViewModel,
                            chatViewModel,
                            scaffoldState,
                        )
                    }
                    
                    if (chatState.isNotEmpty()) {
                    
                    } else {
                        item {
                            Column(
                                modifier = Modifier.padding(bottom = 10.dp)
                                    .fillMaxSize()
                                    .size(600.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Row {
                                    Text(
                                        stringResource(MokoRes.strings.create_your_first_chat),
                                        modifier = Modifier,
                                        fontSize = 16.sp,
                                        lineHeight = 16.sp,
                                        fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                                        fontWeight = FontWeight(500),
                                        color = colors.primary,
                                        letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                    )
                                }
                            }
                        }
                    }
                }
                
            }
        },
        sheetPeekHeight = 0.dp // Начальная высота (0 для скрытия)
    ) {
        // Основной контент приложения (опционально)
    }
    
    
}