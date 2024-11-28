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
import androidx.compose.material3.MaterialTheme
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
import org.videotrade.shopot.presentation.components.ProfileComponents.ProfileHeader
import org.videotrade.shopot.presentation.screens.chat.PhotoViewerScreen
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular


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
        val colors = MaterialTheme.colorScheme

        val tabs = ProfileMediaTabs.entries.map { tab ->
            org.videotrade.shopot.presentation.screens.group.TabInfo(
                title = stringResource(tab.titleResId),
                text = stringResource(tab.textResId)
            )
        }
        
        val imagePainter = getImageStorage(chat.icon, chat.icon, false)
        
        
        Box(
            modifier = Modifier.fillMaxSize().background(colors.background),
            contentAlignment = Alignment.TopStart
        ) {
            
            Column {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.background)
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
                    Text(
                        "${chat.firstName} ${chat.lastName}",
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        lineHeight = 16.sp,
                        fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                        fontWeight = FontWeight(500),
                        color = colors.primary,
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
                                color = colors.secondary,
                                letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                modifier = Modifier.padding(end = if (chat.phone == "") 0.dp else 1.dp),
                            )
                        }

                        Box(
                            modifier = Modifier.padding(start = 18.dp, end = 18.dp).clip(RoundedCornerShape(50.dp)).width(4.dp)
                                .height(4.dp)
                                .background(color = colors.secondary),
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
                                color = colors.secondary,
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
//
                }
                
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                }

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