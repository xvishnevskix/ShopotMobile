package org.videotrade.shopot.presentation.screens.profile

import Avatar
import GroupShortButton
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.presentation.components.ProfileComponents.ProfileHeader
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Medium
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.SFProText_Semibold
import shopot.composeapp.generated.resources.call
import shopot.composeapp.generated.resources.notification
import shopot.composeapp.generated.resources.search_icon
import shopot.composeapp.generated.resources.video_icon


class ProfileMediaScreen(private val profile: ProfileDTO, private val chat: ChatItem) : Screen {

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()
        val pagerState = rememberPagerState(pageCount = { ProfileMediaTabs.entries.size })
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
                    ProfileHeader("Медиа")
                    Avatar(
                        icon = null,
                        size = 186.dp
                    )
                    Text(
                        "${chat.firstName} ${chat.lastName}",
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
                        letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(top = 16.dp, bottom = 9.dp),
                        color = Color(0xFF000000)
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier.padding(bottom = 24.dp),
                    ) {
                        Text(
                            chat.phone,
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                            letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                            lineHeight = 20.sp,
                            modifier = Modifier.padding(end = 18.dp),
                            color = Color(0xFF979797)
                        )
                        profile.login?.let {
                            Text(
                                it,
                                textAlign = TextAlign.Center,
                                fontSize = 16.sp,
                                fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                                letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                                lineHeight = 20.sp,

                                color = Color(0xFF979797)
                            )
                        }
                    }

                    profile.description?.let {
                        Text(
                            it,
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp,
                            fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
                            letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                            lineHeight = 20.sp,
                            modifier = Modifier.padding(),
                            color = Color(0xFF000000)
                        )
                    }


                        Row(
                            modifier = Modifier
                                .padding(start = 5.dp, end = 5.dp, top = 10.dp, bottom = 20.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            GroupShortButton(
                                drawableRes = Res.drawable.video_icon,
                                width = 22.5.dp,
                                height = 15.dp,
                                text = "Видеочат",
                                onClick = {}

                            )
                            GroupShortButton(
                                drawableRes = Res.drawable.call,
                                width = 16.dp,
                                height = 16.dp,
                                text = "Звонок",
                                onClick = {}
                            )
                            GroupShortButton(
                                drawableRes = Res.drawable.notification,
                                width = 18.dp,
                                height = 15.dp,
                                text = "Уведомления",
                                onClick = {}
                            )
                            GroupShortButton(
                                drawableRes = Res.drawable.search_icon,
                                width = 16.85.dp,
                                height = 16.85.dp,
                                text = "Поиск",
                                onClick = {}
                            )
                        }
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
//                    profile.description?.let {
//                        Text(
//                            it,
//                            textAlign = TextAlign.Center,
//                            fontSize = 14.sp,
//                            fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
//                            letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
//                            lineHeight = 20.sp,
//                            modifier = Modifier.padding(top = 10.dp),
//                            color = Color(0xFF000000)
//                        )
//                    }
//                    Text(
//                        "Июль, 2024",
//                        textAlign = TextAlign.Center,
//                        fontSize = 16.sp,
//                        fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
//                        letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
//                        lineHeight = 20.sp,
//                        modifier = Modifier.padding(top = 5.dp),
//                        color = Color(0xFF979797)
//                    )
                }

                Scaffold(

                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 5.dp),
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
                            ProfileMediaTabs.entries.forEachIndexed { index, currentTab ->
                                Tab(
                                    modifier = Modifier.fillMaxWidth().padding(0.dp).clip(
                                        RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
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
                                            fontSize = 15.sp,
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
                                .padding(top = 0.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),

                                contentAlignment = Alignment.TopCenter,
                            ) {
//                                Text( text = Tabs.entries[selectedTabIndex.value].text)

                                if (ProfileMediaTabs.entries[selectedTabIndex.value].text == "Участники") {
                                    LazyColumn(
                                        verticalArrangement = Arrangement.Top,
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        item {

                                        }
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier.fillMaxHeight(0.7F),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = ProfileMediaTabs.entries[selectedTabIndex.value].text,
                                            fontSize = 15.sp,
                                            fontFamily = FontFamily(Font(Res.font.SFProText_Semibold)),
                                            letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                                            lineHeight = 24.sp,
                                            textDecoration = TextDecoration.Underline,
                                            color = Color(0xFF808080)
                                        )
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



private enum class ProfileMediaTabs(
    val text: String,
    val title: String
) {
    Media(
        title = "Медиа",
        text = "Пока тут пусто"
    ),

    Files(
        title = "Файлы",
        text = "Пока тут пусто"
    ),
    Voice(
        title = "Голос",
        text = "Пока тут пусто"
    ),
    Links(
        title = "Ссылки",
        text = "Пока тут пусто"
    ),
    GIF(
        title = "GIF",
        text = "Пока тут пусто"
    ),

}