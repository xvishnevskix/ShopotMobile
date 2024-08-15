package org.videotrade.shopot.presentation.screens.group

import Avatar
import GroupLongButton
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
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.koin.compose.koinInject
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.presentation.components.ProfileComponents.GroupProfileHeader
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Medium
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.add_user


class GroupProfileScreen(private val profile: ProfileDTO, private val chat: ChatItem) : Screen {
    
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()
        val viewModel: ChatViewModel = koinInject()
        val groupUsers = viewModel.groupUsers.collectAsState().value
        
        val pagerState = rememberPagerState(pageCount = { GroupProfileTabs.entries.size })
        val selectedTabIndex = remember {
            derivedStateOf { pagerState.currentPage }
        }
        
        val tabs = GroupProfileTabs.entries.map { tab ->
            TabInfo(
                title = stringResource(tab.titleResId),
                text = stringResource(tab.textResId)
            )
        }


//        LaunchedEffect(Unit) {
//            viewModel.loadGroupUsers(chat.chatId)
//        }
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
                        "${chat.groupName}",
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
                        letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(top = 16.dp, bottom = 9.dp),
                        color = Color(0xFF000000)
                    )
                    Text(
                        groupUsers.size.toString() + "  " + stringResource(MokoRes.strings.members),
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                        letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                        lineHeight = 20.sp,
                        color = Color(0xFF979797)
                    )

//                    Row(
//                        modifier = Modifier
//                            .padding(start = 5.dp, end = 5.dp)
//                            .fillMaxWidth(),
//                        horizontalArrangement = Arrangement.SpaceBetween
//                    ) {
//                        GroupShortButton(
//                            drawableRes = Res.drawable.video_icon,
//                            width = 22.5.dp,
//                            height = 15.dp,
//                            text = stringResource(MokoRes.strings.video_call),
//                            onClick = {}
//                        )
//                        GroupShortButton(
//                            drawableRes = Res.drawable.call,
//                            width = 16.dp,
//                            height = 16.dp,
//                            text = stringResource(MokoRes.strings.call),
//                            onClick = {}
//                        )
//                        GroupShortButton(
//                            drawableRes = Res.drawable.notification,
//                            width = 18.dp,
//                            height = 15.dp,
//                            text = stringResource(MokoRes.strings.notifications),
//                            onClick = {}
//                        )
//                        GroupShortButton(
//                            drawableRes = Res.drawable.search_icon,
//                            width = 16.85.dp,
//                            height = 16.85.dp,
//                            text = stringResource(MokoRes.strings.search),
//                            onClick = {}
//                        )
//                    }
//                    Row(
//                        modifier = Modifier
//                            .padding(top = 15.dp, bottom = 20.dp)
//                            .fillMaxWidth(),
//                        horizontalArrangement = Arrangement.Center
//                    ) {
//                        GroupLongButton(
//                            drawableRes = Res.drawable.add_user,
//                            width = 13.dp,
//                            height = 10.dp,
//                            text = stringResource(MokoRes.strings.add),
//                            onClick = {}
//                        )
//                        GroupLongButton(
//                            drawableRes = Res.drawable.edit_pencil,
//                            width = 13.dp,
//                            height = 13.dp,
//                            text = stringResource(MokoRes.strings.edit),
//                            onClick = {
//                                navigator.push(GroupEditScreen())
//                            }
//                        )
//                    }
                    
                    
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
                            GroupProfileTabs.entries.forEachIndexed { index, currentTab ->
                                val tabInfo =
                                    tabs[index] // Получаем соответствующий TabInfo для текущего индекса
                                
                                Tab(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(0.dp)
                                        .clip(
                                            RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                                        ),
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
                                            text = tabInfo.title, // Используем строку из TabInfo
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
                                val selectedTab = tabs[selectedTabIndex.value]
                                
                                if (selectedTab.text == stringResource(MokoRes.strings.members)) {
                                    LazyColumn(
                                        verticalArrangement = Arrangement.Top,
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        itemsIndexed(groupUsers) { _, groupUser ->
                                            GroupUserCard(true, groupUser)
                                        }
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier.fillMaxHeight(0.7F),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = selectedTab.text)
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


//private enum class Tabs(
//    val text: String,
//    var title: String
//) {
//    Users(
//        title = "Участники",
//        text = "Участники",
//    ),
//    Media(
//        title = "Медиа",
//        text = "Здесь будут ваши медиафайлы"
//    ),
//    Files(
//        title = "Файлы",
//        text = "Здесь будут ваши файлы"
//    ),
//    Voice(
//        title = "Голос",
//        text = "Здесь будут ваши голосовые сообщения"
//    ),
//    Links(
//        title = "Ссылки",
//        text = "Здесь будут ваши ссылки"
//    );
//}


enum class GroupProfileTabs(
    val textResId: StringResource,
    val titleResId: StringResource
) {
    Users(
        titleResId = MokoRes.strings.members,
        textResId = MokoRes.strings.members
    );
//    Media(
//        titleResId = MokoRes.strings.media,
//        textResId = MokoRes.strings.your_media
//    ),
//    Files(
//        titleResId = MokoRes.strings.files,
//        textResId = MokoRes.strings.your_files
//    ),
//    Voice(
//        titleResId = MokoRes.strings.voice,
//        textResId = MokoRes.strings.your_voice
//    ),
//    Links(
//        titleResId = MokoRes.strings.links,
//        textResId = MokoRes.strings.your_links
//    );
    
    companion object {
        @Composable
        fun createTabs(): List<TabInfo> {
            return entries.map { tab ->
                TabInfo(
                    title = stringResource(tab.titleResId),
                    text = stringResource(tab.textResId)
                )
            }
        }
    }
}

data class TabInfo(
    val title: String,
    val text: String
)