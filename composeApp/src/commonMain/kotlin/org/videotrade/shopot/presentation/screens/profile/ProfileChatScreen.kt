package org.videotrade.shopot.presentation.screens.profile

import Avatar
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
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
import getImageStorage
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.presentation.components.ProfileComponents.ProfileChatHeader
import org.videotrade.shopot.presentation.screens.chat.PhotoViewerScreen
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Res


class ProfileChatScreen(private val chat: ChatItem) : Screen {
    
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()
        val pagerState = rememberPagerState(pageCount = { ProfileMediaTabs.entries.size })
        val selectedTabIndex = remember {
            derivedStateOf { pagerState.currentPage }
        }
        println("sssssss ${chat.userId}")
        
        val tabs = ProfileMediaTabs.entries.map { tab ->
            org.videotrade.shopot.presentation.screens.group.TabInfo(
                title = stringResource(tab.titleResId),
                text = stringResource(tab.textResId)
            )
        }
        
        val imagePainter = getImageStorage(chat.icon, chat.icon, false)
        
        
        Box(
            modifier = Modifier.fillMaxSize().background(Color.White),
            contentAlignment = Alignment.TopStart
        ) {
            
            Column {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    ProfileChatHeader(stringResource(MokoRes.strings.profile))
                    Spacer(modifier = Modifier.height(30.dp))
                    Avatar(
                        icon = chat.icon,
                        size = 128.dp,
                        onClick = {
                            println("AAAAA")
                            
                            scope.launch {
                                imagePainter.value?.let {
                                    navigator.push(
                                        PhotoViewerScreen(
                                            imagePainter,
                                            messageSenderName = "${chat.firstName} ${chat.lastName}",
                                        )
                                    )
                                }
//
                            }
                            
                        }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    val displayName = when {
                        chat.personal -> "${chat.firstName.orEmpty()} ${chat.lastName.orEmpty()}".trim().ifBlank { "+${chat.phone}" }
                        else -> chat.groupName.orEmpty()
                    }
                    
                    Text(
                        text = displayName,
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        lineHeight = 16.sp,
                        fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                        fontWeight = FontWeight(500),
                        color = Color(0xFF373533),
                        letterSpacing = TextUnit(0F, TextUnitType.Sp),
                        modifier = Modifier,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier,
                    ) {
                        chat.phone?.let {
                            Text(
                                it,
                                textAlign = TextAlign.Center,
                                fontSize = 16.sp,
                                lineHeight = 16.sp,
                                fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                fontWeight = FontWeight(400),
                                color = Color(0x80373533),
                                letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                modifier = Modifier.padding(end = if (chat.phone == "") 0.dp else 1.dp),
                            )
                        }
                        
                        Box(
                            modifier = Modifier.padding(start = 18.dp, end = 18.dp)
                                .clip(RoundedCornerShape(50.dp)).width(4.dp)
                                .height(4.dp)
                                .background(color = Color(0x80373533)),
                            contentAlignment = Alignment.Center
                        ) {
                        
                        }
                        
                        if (chat.chatUser?.get(0)?.login != null) {
                            Text(
                                chat.chatUser!![0].login!!,
                                textAlign = TextAlign.Center,
                                fontSize = 16.sp,
                                lineHeight = 16.sp,
                                fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                fontWeight = FontWeight(400),
                                color = Color(0x80373533),
                                letterSpacing = TextUnit(0F, TextUnitType.Sp),
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(28.dp))
                    
                    if (chat.chatUser?.get(0)?.description != null) {
                        Text(
                            chat.chatUser!![0].description!!,
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp,
                            lineHeight = 16.sp,
                            fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                            fontWeight = FontWeight(500),
                            color = Color(0xFFCAB7A3),
                            letterSpacing = TextUnit(0F, TextUnitType.Sp),
                        )
                    }
                    Spacer(modifier = Modifier.height(56.dp))
//                    Text(
//                        "${chat.firstName} ${chat.lastName}",
//                        textAlign = TextAlign.Center,
//                        fontSize = 16.sp,
//                        lineHeight = 16.sp,
//                        fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
//                        fontWeight = FontWeight(500),
//                        color = Color(0xFF373533),
//                        letterSpacing = TextUnit(0F, TextUnitType.Sp),
//                    )
//                    Row(
//                        horizontalArrangement = Arrangement.SpaceAround,
//                        modifier = Modifier.padding(bottom = 24.dp),
//                    ) {
//                        chat.phone?.let {
//                            Text(
//                                it,
//                                textAlign = TextAlign.Center,
//                                fontSize = 16.sp,
//                                lineHeight = 16.sp,
//                                fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
//                                fontWeight = FontWeight(400),
//                                color = Color(0x80373533),
//                                letterSpacing = TextUnit(0F, TextUnitType.Sp),
//                                modifier = Modifier.padding(end = if (chat.phone == "") 0.dp else 1.dp),
//                            )
//                        }
//
//                        if (chat.chatUser?.get(0)?.login != null) {
//                            Text(
//                                chat.chatUser!![0].login!!,
//                                textAlign = TextAlign.Center,
//                                fontSize = 16.sp,
//                                lineHeight = 16.sp,
//                                fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
//                                fontWeight = FontWeight(400),
//                                color = Color(0x80373533),
//                                letterSpacing = TextUnit(0F, TextUnitType.Sp),
//                            )
//                        }
//                    }
//
//
//                    if (chat.chatUser?.get(0)?.description != null) {
//                        Text(
//                            chat.chatUser!![0].description!!,
//                            textAlign = TextAlign.Center,
//                            fontSize = 16.sp,
//                            lineHeight = 16.sp,
//                            fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
//                            fontWeight = FontWeight(500),
//                            color = Color(0xFFCAB7A3),
//                            letterSpacing = TextUnit(0F, TextUnitType.Sp),
//                        )
//                    }


//                        Row(
//                            modifier = Modifier
//                                .padding(start = 5.dp, end = 5.dp, top = 10.dp, bottom = 20.dp)
//                                .fillMaxWidth(),
//                            horizontalArrangement = Arrangement.SpaceBetween
//                        ) {
//                            GroupShortButton(
//                                drawableRes = Res.drawable.video_icon,
//                                width = 22.5.dp,
//                                height = 15.dp,
//                                text = stringResource(MokoRes.strings.video_call),
//                                onClick = {}
//
//                            )
//                            GroupShortButton(
//                                drawableRes = Res.drawable.call,
//                                width = 16.dp,
//                                height = 16.dp,
//                                text = stringResource(MokoRes.strings.call),
//                                onClick = {}
//                            )
//                            GroupShortButton(
//                                drawableRes = Res.drawable.notification,
//                                width = 18.dp,
//                                height = 15.dp,
//                                text = stringResource(MokoRes.strings.notifications),
//                                onClick = {}
//                            )
//                            GroupShortButton(
//                                drawableRes = Res.drawable.search_icon,
//                                width = 16.85.dp,
//                                height = 16.85.dp,
//                                text = stringResource(MokoRes.strings.search),
//                                onClick = {}
//                            )
//                        }
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

//                Scaffold(
//
//                ) {
//                    Column(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .padding(top = 5.dp),
//                        horizontalAlignment = Alignment.CenterHorizontally
//
//                    ) {
//                        TabRow(
//                            selectedTabIndex = selectedTabIndex.value,
//                            modifier = Modifier.fillMaxWidth(0.95F),
//                            indicator = @Composable { tabPositions ->
//                                TabRowDefaults.SecondaryIndicator(
//                                    modifier = Modifier
//                                        .tabIndicatorOffset(tabPositions[selectedTabIndex.value])
//                                        .clip(RoundedCornerShape(8.dp)),
//                                    height = 3.dp,
//                                    color = Color(0xFF29303C),
//
//                                    )
//                            }
//
//                        ) {
//                            ProfileMediaTabs.entries.forEachIndexed { index, currentTab ->
//
//                                val tabInfo = tabs[index]
//
//
//                                Tab(
//                                    modifier = Modifier.fillMaxWidth().padding(0.dp).clip(
//                                        RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
//                                    selected = selectedTabIndex.value == index,
//                                    selectedContentColor = Color(0xFF29303C),
//                                    unselectedContentColor = Color(0xFFA9A8AA),
//
//                                    onClick = {
//                                        scope.launch {
//                                            pagerState.animateScrollToPage(currentTab.ordinal)
//                                        }
//                                    },
//
//                                    text = {
//                                        Text(
//                                            modifier = Modifier.wrapContentWidth(),
//                                            text = tabInfo.title,
//                                            textAlign = TextAlign.Start,
//                                            fontSize = 15.sp,
//                                            fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Medium)),
//                                            letterSpacing = TextUnit(-1.3F, TextUnitType.Sp),
//                                            lineHeight = 15.sp,
//                                            softWrap = false
//                                        )
//                                    },
//                                )
//                            }
//                        }
//
//                        HorizontalPager(
//                            state = pagerState,
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .weight(1f)
//                                .padding(top = 0.dp)
//                        ) {
//                            Box(
//                                modifier = Modifier.fillMaxSize(),
//
//                                contentAlignment = Alignment.TopCenter,
//                            ) {
//                                val selectedTab = tabs[selectedTabIndex.value]
////                                Text( text = Tabs.entries[selectedTabIndex.value].text)
//
//                                if (selectedTab.text == stringResource(MokoRes.strings.media)) {
//                                    LazyColumn(
//                                        verticalArrangement = Arrangement.Top,
//                                        horizontalAlignment = Alignment.CenterHorizontally,
//                                        modifier = Modifier.fillMaxSize()
//                                    ) {
//                                        item {
//
//                                        }
//                                    }
//                                } else {
//                                    Box(
//                                        modifier = Modifier.fillMaxHeight(0.7F),
//                                        contentAlignment = Alignment.Center
//                                    ) {
//                                        Text(
//                                            text = selectedTab.text,
//                                            fontSize = 15.sp,
//                                            fontFamily = FontFamily(Font(Res.font.SFProText_Semibold)),
//                                            letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
//                                            lineHeight = 24.sp,
//                                            textDecoration = TextDecoration.Underline,
//                                            color = Color(0xFF808080)
//                                        )
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
            
            }
        }
        
    }
}


enum class ProfileMediaTabs(
    val textResId: StringResource,
    val titleResId: StringResource
) {
    Media(
        titleResId = MokoRes.strings.media,
        textResId = MokoRes.strings.nothing_here
    ),
    Files(
        titleResId = MokoRes.strings.files,
        textResId = MokoRes.strings.nothing_here
    ),
    Voice(
        titleResId = MokoRes.strings.voice,
        textResId = MokoRes.strings.nothing_here
    ),
    Links(
        titleResId = MokoRes.strings.links,
        textResId = MokoRes.strings.nothing_here
    );
    
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


//private enum class ProfileMediaTabs(
//    val text: String,
//    val title: String
//) {
//    Media(
//        title = "Медиа",
//        text = "Пока тут пусто"
//    ),
//
//    Files(
//        title = "Файлы",
//        text = "Пока тут пусто"
//    ),
//    Voice(
//        title = "Голос",
//        text = "Пока тут пусто"
//    ),
//    Links(
//        title = "Ссылки",
//        text = "Пока тут пусто"
//    ),
//    GIF(
//        title = "GIF",
//        text = "Пока тут пусто"
//    ),
//
//}