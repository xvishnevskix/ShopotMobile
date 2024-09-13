package org.videotrade.shopot.presentation.components.Main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.main.MainViewModel
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Medium
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.smart_encryption
import shopot.composeapp.generated.resources.smart_lock

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainContentComponent(mainViewModel: MainViewModel, commonViewModel: CommonViewModel) {
    val chatState = mainViewModel.chats.collectAsState(initial = listOf()).value
    val navigator = LocalNavigator.currentOrThrow
    val scope = rememberCoroutineScope()
    
    var refreshing by remember { mutableStateOf(false) }
    
    val refreshState = rememberPullRefreshState(
        refreshing = refreshing,
        onRefresh = {
            scope.launch {
                // Имитируем обновление данных
                refreshing = true
                mainViewModel.getChatsInBack()
                refreshing = false
            }
        }
    )

//    LaunchedEffect(Unit) {
//        mainViewModel.getChatsInBack()
//    }
    
        SafeArea {
            Box(modifier = Modifier.fillMaxSize()) {
            
            Column(modifier = Modifier.fillMaxSize()) {
                HeaderMain()
                
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        stringResource(MokoRes.strings.chats),
                        modifier = Modifier.padding(bottom = 15.dp, top = 5.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
                        letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                        lineHeight = 20.sp,
                        color = Color(0xFF000000)
                    )
                    
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .pullRefresh(refreshState)
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .offset {
                                    IntOffset(
                                        x = 0,
                                        y = (refreshState.progress * 200).toInt()
                                    )
                                },
                            verticalArrangement = Arrangement.Center,
                        ) {
                            items(chatState) { item ->
                                UserComponentItem(item, commonViewModel, mainViewModel)
                            }
                            
                            if (chatState.isNotEmpty()) {
                                item {
                                    Column(
                                        modifier = Modifier.padding(top = 40.dp, bottom = 10.dp)
                                            .fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Image(
                                                modifier = Modifier.size(27.dp),
                                                painter = painterResource(Res.drawable.smart_lock),
                                                contentDescription = null,
                                            )
                                        }
                                        
                                        Row (
                                            horizontalArrangement = Arrangement.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                stringResource(MokoRes.strings.encryption_info_1),
                                                textAlign = TextAlign.Center,
                                                fontSize = 10.sp,
                                                fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                                                letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                                                lineHeight = 20.sp,
                                                color = Color(0xFF000000),
                                            )
                                            Text(
                                                "  " + stringResource(MokoRes.strings.encryption_info_2),
                                                textAlign = TextAlign.Start,
                                                fontSize = 10.sp,
                                                fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                                                letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                                                lineHeight = 20.sp,
                                                color = Color(0xFF219653),
                                                textDecoration = TextDecoration.Underline,
                                            )
                                        }
                                    }
                                }
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
                                                stringResource(MokoRes.strings.create_new_chat),
                                                modifier = Modifier,
                                                textAlign = TextAlign.Center,
                                                fontSize = 20.sp,
                                                fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Medium)),
                                                letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                                                lineHeight = 20.sp,
                                                color = Color(0xFF979797)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        PullRefreshIndicator(refreshState, Modifier.align(Alignment.TopCenter))
                        
                    }
                }
            }
        }
        
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PullRefreshIndicator(state: PullRefreshState, modifier: Modifier = Modifier) {
    val progress = state.progress
    
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(56.dp)
            .padding(16.dp)
    ) {
        CircularProgressIndicator(
            progress = progress
        )
    }
}