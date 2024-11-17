package org.videotrade.shopot.presentation.components.Main

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.api.formatTimestamp
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.components.Contacts.ContactsSearch
import org.videotrade.shopot.presentation.components.Contacts.MakeGroup
import org.videotrade.shopot.presentation.screens.chat.ChatScreen
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.main.MainViewModel
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Medium
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.auth_logo
import shopot.composeapp.generated.resources.group
import shopot.composeapp.generated.resources.message_double_check
import shopot.composeapp.generated.resources.message_single_check
import shopot.composeapp.generated.resources.smart_encryption
import shopot.composeapp.generated.resources.smart_lock

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainContentComponent(mainViewModel: MainViewModel, commonViewModel: CommonViewModel) {
    val chatState = mainViewModel.chats.collectAsState(initial = listOf()).value
    val navigator = LocalNavigator.currentOrThrow
    val scope = rememberCoroutineScope()

    val isLoading by mainViewModel.isLoadingChats.collectAsState()
    var fakeLoading by remember { mutableStateOf(true) }
    var refreshing by remember { mutableStateOf(false) }

    val isSearching = remember { mutableStateOf(false) }
    val searchQuery = remember { mutableStateOf("") }

    val filteredChats = if (searchQuery.value.isEmpty()) {
        chatState
    } else {
        chatState.filter {
            (it.firstName?.contains(searchQuery.value, ignoreCase = true) == true)
                    || (it.lastName?.contains(searchQuery.value, ignoreCase = true) == true)
                    || (it.phone?.contains(searchQuery.value) == true)
        }
    }
    
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
    LaunchedEffect(chatState) {
        fakeLoading = true
        delay(300)
        fakeLoading = false
    }
    
        SafeArea(backgroundColor = if (isLoading) Color.White else Color(0xFFf9f9f9)) {
            Box(modifier = Modifier.background(color = if (isLoading) Color.White else Color(0xFFf9f9f9)).fillMaxSize()) {
            
            Column(modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.Start
                ) {
                HeaderMain(isSearching)


                Crossfade(targetState = isSearching.value) { searching ->
                    if (searching) {
                        Column {
                            Spacer(modifier = Modifier.height(16.dp))
                            ContactsSearch(searchQuery, isSearching, padding = 0.dp)
                        }
                    }
                }
                
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .pullRefresh(refreshState)
                    ) {
                        if (fakeLoading || isLoading) {
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(12) {
                                    ChatSkeleton()
                                }
                            }
                        } else {
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
                                items(filteredChats) { item ->
                                    Column {
                                        UserComponentItem(item, commonViewModel, mainViewModel)
                                        Spacer(modifier = Modifier.background(Color(0xFFF3F4F6)).height(16.dp))
                                    }
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

                                            Spacer(modifier = Modifier.height(10.dp))

                                            Row (
                                                horizontalArrangement = Arrangement.Center,
                                                modifier = Modifier.padding(bottom = 50.dp).fillMaxWidth().padding(bottom = 50.dp)
                                            ) {
                                                Text(
                                                    stringResource(MokoRes.strings.encryption_info_1),
                                                    fontSize = 9.5.sp,
                                                    lineHeight = 10.sp,
                                                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                                                    color = Color(0xFF373533),
                                                    letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                                                )
                                                Text(
                                                    " " + stringResource(MokoRes.strings.encryption_info_2),
                                                    textAlign = TextAlign.Start,
                                                    fontSize = 9.5.sp,
                                                    lineHeight = 10.sp,
                                                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                                                    color = Color(0xFFCAB7A3),
                                                    textDecoration = TextDecoration.Underline,
                                                    letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                                                )
                                            }
                                        }
                                    }

                                } else {
                                    item {
                                        Column(
                                            modifier = Modifier.background(Color(0xFFf9f9f9)).padding(bottom = 20.dp)
                                                .fillMaxSize()
                                                .size(600.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Column(
                                                modifier = Modifier.background(Color(0xFFf9f9f9)).width(324.dp)
                                                    .height(324.dp)
                                                    .background(color = Color(0xFFf9f9f9), shape = RoundedCornerShape(size = 16.dp))
                                                ,
                                                verticalArrangement = Arrangement.Center,
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Image(
                                                    modifier = Modifier
                                                        .size(width = 195.dp, height = 132.dp),
                                                    painter = painterResource(Res.drawable.auth_logo),
                                                    contentDescription = null,
                                                    contentScale = ContentScale.Crop
                                                )
                                                Spacer(modifier = Modifier.height(56.dp))
                                                Text(
                                                    stringResource(
                                                        MokoRes.strings.greeting
                                                    ),
                                                    fontSize = 24.sp,
                                                    lineHeight = 24.sp,
                                                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                                                    fontWeight = FontWeight(500),
                                                    textAlign = TextAlign.Center,
                                                    color = Color(0xFF373533),
                                                    letterSpacing = TextUnit(0F, TextUnitType.Sp)
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                    "Создайте свой первый чат",
                                                    fontSize = 15.sp,
                                                    lineHeight = 15.sp,
                                                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                                    fontWeight = FontWeight(400),
                                                    letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                                    textAlign = TextAlign.Center,
                                                    color = Color(0x80373533),
                                                    maxLines = 3,
                                                )

                                            }
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

val shimmerColorShades = listOf(
    Color(0xFFEDDCCC),
    Color(0xFFF7F7F7),
    Color(0xFFEDDCCC),
)

@Composable
fun ChatSkeleton() {
    // Бесконечная анимация перелива
    val transition = rememberInfiniteTransition()
    val shimmerTranslateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 2000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1800, // Скорость перелива
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )

    // Градиент для эффекта перелива
    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFF7F7F7),
            Color(0xFFEDDCCC),
            Color(0xFFF7F7F7),

        ),
        start = Offset.Zero,
        end = Offset(x = shimmerTranslateAnim, y = shimmerTranslateAnim) // Плавное перемещение по X и Y
    )

    Row(
        modifier = Modifier
            .background(Color.Transparent)
            .fillMaxWidth()
            .clickable {},
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.Top
        ) {
            // Круглый элемент скелетона
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color(0xFFF7F7F7))
                    .size(56.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.Top
            ) {
                // Прямоугольный элемент для текста
                Box(
                    modifier = Modifier
                        .width(50.dp)
                        .height(8.dp)
                        .background(shimmerBrush, shape = RoundedCornerShape(size = 30.dp))
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row {
                    // Длинный прямоугольник
                    Box(
                        modifier = Modifier
                            .width(163.dp)
                            .height(8.dp)
                            .background(shimmerBrush, shape = RoundedCornerShape(size = 100.dp))
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    // Короткий прямоугольник
                    Box(
                        modifier = Modifier
                            .width(50.dp)
                            .height(8.dp)
                            .background(shimmerBrush, shape = RoundedCornerShape(size = 30.dp))
                    )
                }
            }
        }

        // Короткий прямоугольник справа
        Box(
            modifier = Modifier
                .width(30.dp)
                .height(8.dp)
                .background(shimmerBrush, shape = RoundedCornerShape(size = 30.dp))
        )
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
            progress = progress,
            color = Color(0xFFCAB7A3)
        )
    }
}