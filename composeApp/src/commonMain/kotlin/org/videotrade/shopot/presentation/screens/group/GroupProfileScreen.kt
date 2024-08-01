package org.videotrade.shopot.presentation.screens.group

import Avatar
import GroupLongButton
import GroupShortButton
import GroupUserCard
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.presentation.components.ProfileComponents.GroupProfileHeader
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Medium
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.add_user
import shopot.composeapp.generated.resources.call
import shopot.composeapp.generated.resources.edit_pencil
import shopot.composeapp.generated.resources.notification
import shopot.composeapp.generated.resources.search_icon
import shopot.composeapp.generated.resources.video_icon


class GroupProfileScreen : Screen {
    
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()
        val pagerState = rememberPagerState(pageCount = { Tabs.entries.size })
        val selectedTabIndex = remember {
            derivedStateOf { pagerState.currentPage }
        }
        
        
        
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopStart
        ) {
            
            Column {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(bottomEnd = 46.dp, bottomStart = 46.dp))
                        .background(Color(0xFFF3F4F6))
                        .padding(16.dp)
                ) {
                    GroupProfileHeader(stringResource(MokoRes.strings.edit))
                    Avatar(
                        icon = null,
                        size = 116.dp
                    )
                    Text(
                        "Работа над проектом",
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
                        letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(top = 16.dp, bottom = 9.dp),
                        color = Color(0xFF000000)
                    )
                    Text(
                        "6 участников",
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                        letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                        lineHeight = 20.sp,
                        color = Color(0xFF979797)
                    )
                    
                    Row(
                        modifier = Modifier
                            .padding(start = 5.dp, end = 5.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        GroupShortButton(
                            drawableRes = Res.drawable.video_icon,
                            width = 22.5.dp,
                            height = 15.dp,
                            text = "Видеочат"
                        )
                        GroupShortButton(
                            drawableRes = Res.drawable.call,
                            width = 16.dp,
                            height = 16.dp,
                            text = "Звонок"
                        )
                        GroupShortButton(
                            drawableRes = Res.drawable.notification,
                            width = 18.dp,
                            height = 15.dp,
                            text = "Уведомления"
                        )
                        GroupShortButton(
                            drawableRes = Res.drawable.search_icon,
                            width = 16.85.dp,
                            height = 16.85.dp,
                            text = "Поиск"
                        )
                    }
                    Row(
                        modifier = Modifier
                            .padding(top = 15.dp, bottom = 20.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        GroupLongButton(
                            drawableRes = Res.drawable.add_user,
                            width = 13.dp,
                            height = 10.dp,
                            text = "Добавить"
                        )
                        GroupLongButton(
                            drawableRes = Res.drawable.edit_pencil,
                            width = 13.dp,
                            height = 13.dp,
                            text = stringResource(MokoRes.strings.edit)
                        )
                    }
                    
                    
                }
                
                
                Scaffold(
                
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = it.calculateTopPadding()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    
                    ) {
                        TabRow(
                            selectedTabIndex = selectedTabIndex.value,
                            modifier = Modifier.fillMaxWidth(0.95F),
                            indicator = @Composable { tabPositions ->
                                TabRowDefaults.SecondaryIndicator(
                                    modifier = Modifier
                                        .tabIndicatorOffset(tabPositions[selectedTabIndex.value])
                                        .clip(RoundedCornerShape(8.dp)),
                                    height = 3.dp,
                                    color = Color(0xFF29303C),
                                    
                                    )
                            }
                        
                        ) {
                            Tabs.entries.forEachIndexed { index, currentTab ->
                                Tab(
                                    modifier = Modifier.fillMaxWidth().padding(0.dp),
                                    selected = selectedTabIndex.value == index,
                                    selectedContentColor = Color(0xFF29303C),
                                    unselectedContentColor = Color(0xFFA9A8AA),
                                    onClick = {
                                        scope.launch {
                                            pagerState.animateScrollToPage(currentTab.ordinal)
                                        }
                                    },
                                    
                                    text = {
                                        Text(
                                            modifier = Modifier.wrapContentWidth(),
                                            text = currentTab.title,
                                            textAlign = TextAlign.Start,
                                            fontSize = 12.sp,
                                            fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Medium)),
                                            letterSpacing = TextUnit(-1.3F, TextUnitType.Sp),
                                            lineHeight = 15.sp,
                                            softWrap = false
                                        )
                                    },
                                )
                            }
                        }
                        
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(top = 10.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                
                                contentAlignment = Alignment.TopCenter,
                            ) {
//                                Text( text = Tabs.entries[selectedTabIndex.value].text)
                                
                                if (Tabs.entries[selectedTabIndex.value].text == "Участники") {
                                    LazyColumn(
                                        verticalArrangement = Arrangement.Top,
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        item {
                                            GroupUserCard()
                                            GroupUserCard()
                                            GroupUserCard()
                                            GroupUserCard()
                                            GroupUserCard()
                                            GroupUserCard()
                                            GroupUserCard()
                                            GroupUserCard()
                                            GroupUserCard()
                                            GroupUserCard()
                                            GroupUserCard()
                                        }
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier.fillMaxHeight(0.7F),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = Tabs.entries[selectedTabIndex.value].text)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


private enum class Tabs(
    val text: String,
    val title: String
) {
    Users(
        title = "Участники",
        text = "Участники",
    ),
    Media(
        title = "Медиа",
        text = "Здесь будут ваши медиафайлы"
    ),
    Files(
        title = "Файлы",
        text = "Здесь будут ваши файлы"
    ),
    Voice(
        title = "Голос",
        text = "Здесь будут ваши голосовые сообщения"
    ),
    Links(
        title = "Ссылки",
        text = "Здесь будут ваши ссылки"
    ),
    
    
}