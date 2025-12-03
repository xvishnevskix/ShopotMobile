package org.videotrade.shopot.presentation.screens.profile

import Avatar
import ProfileSettingsButton
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
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import getImageStorage
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.api.navigateToScreen
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.presentation.components.ProfileComponents.ProfileChatHeader
import org.videotrade.shopot.presentation.screens.chat.PhotoViewerScreen
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.contacts.ContactsViewModel
import org.videotrade.shopot.presentation.tabs.ChatsTab
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.chat_nav


class ProfileChatScreen(private val profile: ProfileDTO, private val isCreateChat: Boolean) :
    Screen {
    
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val contactsViewModel: ContactsViewModel = koinInject()
        val commonViewModel: CommonViewModel = koinInject()
        
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
        
        val imagePainter = getImageStorage(profile.icon, profile.icon, false)
        
        
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
                        icon = profile.icon,
                        size = 128.dp,
                        onClick = {
                            println("AAAAA")
                            
                            scope.launch {
                                imagePainter.value?.let {
                                    navigateToScreen(
                                        navigator,
                                        PhotoViewerScreen(
                                            imagePainter,
                                            messageSenderName = "${profile.firstName} ${profile.lastName}",
                                        )
                                    )
                                }
//
                            }
                            
                        }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    val isDeletedUser = profile.firstName.equals(
                        "Unknown",
                        ignoreCase = true
                    ) && profile.lastName.isBlank()
                    val displayName = when {
                        isDeletedUser -> stringResource(MokoRes.strings.deleted_user)
                        else -> "${profile.firstName.orEmpty()} ${profile.lastName.orEmpty()}".trim()
                    }
                    
                    Text(
                        text = displayName,
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
                        profile.phone?.let {
                            Text(
                                it,
                                textAlign = TextAlign.Center,
                                fontSize = 16.sp,
                                lineHeight = 16.sp,
                                fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                fontWeight = FontWeight(400),
                                color = colors.secondary,
                                letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                modifier = Modifier.padding(end = if (profile.phone == "") 0.dp else 1.dp),
                            )
                        }
                        
                        Box(
                            modifier = Modifier.padding(start = 18.dp, end = 18.dp)
                                .clip(RoundedCornerShape(50.dp)).width(4.dp)
                                .height(4.dp)
                                .background(color = colors.secondary),
                            contentAlignment = Alignment.Center
                        ) {
                        
                        }
                        
                        if (profile.login != null) {
                            Text(
                                profile.login,
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
                    
                    if (profile.description != null) {
                        Text(
                            profile.description,
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp,
                            lineHeight = 16.sp,
                            fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                            fontWeight = FontWeight(500),
                            color = Color(0xFFCAB7A3),
                            letterSpacing = TextUnit(0F, TextUnitType.Sp),
                        )
                    }
//                    Spacer(modifier = Modifier.height(56.dp))
//
                }
                
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    
                    if (isCreateChat)
                        ProfileSettingsButton(
                            drawableRes = Res.drawable.chat_nav,
                            width = 18.dp,
                            height = 16.92.dp,
                            mainText = stringResource(MokoRes.strings.create_chat),
                            onClick = {
                                contactsViewModel.createChat(profile.id)
                                
                                commonViewModel.tabNavigator.value?.current = ChatsTab
                            }
                        )
                    Spacer(modifier = Modifier.height(16.dp))
                    
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