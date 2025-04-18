package org.videotrade.shopot.presentation.components.Main

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
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
import org.koin.compose.koinInject
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.domain.model.ContactDTO
import org.videotrade.shopot.domain.model.NewsItem
import org.videotrade.shopot.domain.model.SearchDto
import org.videotrade.shopot.presentation.components.Common.CheckAndShowDialogs
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.components.Contacts.ContactsSearch
import org.videotrade.shopot.presentation.components.Main.News.NewsViewModel
import org.videotrade.shopot.presentation.components.Main.News.StoryViewer
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.contacts.ContactsViewModel
import org.videotrade.shopot.presentation.components.Common.shouldShowRateDialog
import org.videotrade.shopot.presentation.components.Common.shouldShowSurvey
import org.videotrade.shopot.presentation.screens.group.GroupViewModel
import org.videotrade.shopot.presentation.screens.main.MainViewModel
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.auth_logo
import shopot.composeapp.generated.resources.smart_lock

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainContentComponent(mainViewModel: MainViewModel, commonViewModel: CommonViewModel) {
    val chatState = mainViewModel.chats.collectAsState().value
    val navigator = LocalNavigator.currentOrThrow
    val scope = rememberCoroutineScope()
    val colors = MaterialTheme.colorScheme
    val isLoading by mainViewModel.isLoadingChats.collectAsState()
    var refreshing by remember { mutableStateOf(false) }
    val newsViewModel: NewsViewModel = koinInject()
    val contactsViewModel: ContactsViewModel = koinInject()


    val isSearching = remember { mutableStateOf(false) }
    val searchQuery = remember { mutableStateOf("") }

    val newsState = newsViewModel.news.collectAsState().value
    val onceNewsState = newsViewModel.onceNews.collectAsState().value
    var showNewsViewer by remember { mutableStateOf(false) }
    var showNewsOnceViewer by remember { mutableStateOf(false) }
    var selectedNews: NewsItem? by remember { mutableStateOf(null) }
    var selectedOnceNews: NewsItem? by remember { mutableStateOf(null) }
    
    val viewModel: ChatViewModel = koinInject()
    val groupViewModel: GroupViewModel = koinInject()

    val globalResults by mainViewModel.globalSearchResults.collectAsState()

    LaunchedEffect(searchQuery.value) {
        if (searchQuery.value.trim().length >= 3) {
            isSearching.value = true
            delay(500)
            mainViewModel.searchUsers(searchQuery.value)
        } else {
            mainViewModel.clearGlobalResults()
        }
    }

    val filteredChats = if (searchQuery.value.isEmpty()) {
        chatState
    } else {
        chatState.filter {
            (it.firstName?.contains(searchQuery.value, ignoreCase = true) == true)
                    || (it.lastName?.contains(searchQuery.value, ignoreCase = true) == true)
                    || (it.phone?.contains(searchQuery.value) == true)
                    || (it.groupName?.contains(searchQuery.value, ignoreCase = true) == true)
        }
    }
    
    val refreshState = rememberPullRefreshState(
        refreshing = refreshing,
        onRefresh = {
            scope.launch {
                // Имитируем обновление данных
                println("commonViewModel.mainNavigator.value ${commonViewModel.mainNavigator.value}")
                refreshing = true
                mainViewModel.getChatsInBack()
                refreshing = false
            }
        }
    )


    LaunchedEffect(Unit) {
        newsViewModel.getNewsByAppearance("once")
        newsViewModel.getNewsByAppearance("actual")
    }

    var isProcessingUpdate by remember { mutableStateOf(false) }

// Обработка новостей из `once`
//    LaunchedEffect(onceNewsState) {
//        if (!showNewsOnceViewer) {
//            val newsToShow = onceNewsState.find {
//                it.appearance == "once" && !it.viewed &&
//                        (it.version.isEmpty() || it.version == BuildConfig.VERSION_NAME)
//            }
//            if (newsToShow != null) {
//                selectedOnceNews = newsToShow
//                showNewsOnceViewer = true
//            }
//        }
//    }


    
        SafeArea(
            backgroundColor = if (isLoading) colors.background else colors.surface,
            padding = 0.dp
        ) {
            Box(modifier = Modifier.background(color = if (isLoading) colors.background else colors.surface).fillMaxSize()) {
            
            Column(modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.Start
                ) {
                HeaderMain(
                    isSearching = isSearching,
                    news = newsState,
                    onStoryClick = { newsItem ->
                        selectedNews = newsItem
                        showNewsViewer = true
                    }
                )


                Column(
                    modifier = Modifier.animateContentSize().padding(horizontal = 16.dp)
                ) {
                    Crossfade(targetState = isSearching.value) { searching ->
                        if (searching) {
                            println("Search state: $searching")
                            Column(
                                modifier = Modifier.animateContentSize()
                            ) {
                                Spacer(modifier = Modifier.height(16.dp))
                                ContactsSearch(searchQuery, isSearching, padding = 0.dp, mainViewModel)
                            }
                        }
                    }
                }
                
                Column(
                    modifier = Modifier.animateContentSize().weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .pullRefresh(refreshState)
                    ) {
                        if (isLoading) {
                            ChatSkeleton()
                        } else {
                            val maxHeight = if (isSearching.value) ((72.dp) * 6) else Dp.Unspecified
                            //чаты  пользователя
                            Column(
                                modifier = Modifier
                                    .animateContentSize()
                                    .offset {
                                        IntOffset(
                                            x = 0,
                                            y = (refreshState.progress * 200).toInt()
                                        )
                                    }
                            ) {
                                LazyColumn(
                                    modifier = Modifier
                                        .animateContentSize()
                                        .heightIn(max = maxHeight)

                                       ,
                                    verticalArrangement = Arrangement.Center,
                                ) {


                                    items(filteredChats) { item ->
                                        Crossfade(targetState = item) { item ->
                                            Column {
                                                val groupUsers by remember {
                                                    derivedStateOf { groupViewModel.cachedGroupUsers[item.chatId] ?: emptyList() }
                                                }

                                                LaunchedEffect(item.chatId) {
                                                    if (!item.personal && groupUsers.isEmpty()) {
                                                        groupViewModel.getGroupUsers(item.chatId)
                                                    }
                                                }
                                                UserComponentItem(item, commonViewModel, mainViewModel, groupUsers)
                                                Spacer(modifier = Modifier.background(Color(0xFFF3F4F6)).height(16.dp))
                                            }
                                        }

                                    }

                                    item {
                                        if (chatState.isNotEmpty()) {

                                            Crossfade(targetState = isSearching.value) { searching ->
                                                if (!searching) {
                                                    EncryptionInfoCard()
                                                }
                                            }


                                        } else {
                                            Crossfade(targetState = isSearching.value) { searching ->
                                                if (!searching) {
                                                    CreateFirstChatCard()
                                                }
                                            }

                                        }

                                    }


                                }
                                //глобальный
                                Crossfade(targetState = isSearching.value) { searching ->
                                    if (searching) {
                                        Column(
                                            modifier = Modifier.animateContentSize()
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(colors.onBackground)
                                            ) {
                                                Text(
                                                    text = stringResource(MokoRes.strings.global_search),
                                                    textAlign = TextAlign.Start,
                                                    fontSize = 16.sp,
                                                    lineHeight = 16.sp,
                                                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                                    fontWeight = FontWeight(400),
                                                    color = colors.secondary,
                                                    letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                                                )


                                            }
                                            LazyColumn(
                                                modifier = Modifier.animateContentSize(),
                                                verticalArrangement = Arrangement.Center,
                                            ) {

                                                item {
                                                    Spacer(modifier = Modifier.height(16.dp))
                                                }

                                                items(globalResults) { user ->
                                                    Crossfade(targetState = user) {user ->
//
                                                       Column {
                                                           SearchUserItem(user) {

                                                               scope.launch {
                                                                   val contact = user.toContactDTO()
                                                                   contactsViewModel.createChat(contact)
                                                                   isSearching.value = false
                                                                   searchQuery.value = ""
                                                                   mainViewModel.clearGlobalResults()

                                                                   delay(500)
                                                                   mainViewModel.getChatsInBack()
                                                               }

                                                           }
                                                           Spacer(modifier = Modifier.height(16.dp))
                                                       }
                                                    }
                                                }
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
                CheckAndShowDialogs()
        }
        
    }

    if (showNewsOnceViewer && selectedOnceNews != null) {
        StoryViewer(
            news = selectedOnceNews!!,
            onClose = {
                showNewsOnceViewer = false
                newsViewModel.markNewsAsViewed(selectedOnceNews!!.id)
                selectedOnceNews = null
            },
            newsViewModel
        )
    }

    // StoryViewer для `actual` новостей
    if (showNewsViewer && selectedNews != null) {
        StoryViewer(
            news = selectedNews!!,
            onClose = { showNewsViewer = false },
            newsViewModel,
        )
    }

    //оценка приложения
//    shouldShowSurvey()
//    CheckAndShowRateApp()

    //опросник
//    shouldShowRateDialog()
//    CheckAndShowSurvey()
    //опросник и оценка

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

@Composable
fun CreateFirstChatCard() {
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .background(colors.surface)
            .padding(bottom = 20.dp)
            .fillMaxSize()
            .size(600.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier
                .background(colors.surface)
                .width(324.dp)
                .height(324.dp)
                .background(color = colors.surface, shape = RoundedCornerShape(size = 16.dp)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier.size(width = 195.dp, height = 132.dp),
                painter = painterResource(Res.drawable.auth_logo),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.tint(colors.primary)
            )
            Spacer(modifier = Modifier.height(56.dp))
            Text(
                stringResource(MokoRes.strings.greeting),
                fontSize = 24.sp,
                lineHeight = 24.sp,
                fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                fontWeight = FontWeight(500),
                textAlign = TextAlign.Center,
                color = colors.primary,
                letterSpacing = TextUnit(0F, TextUnitType.Sp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                stringResource(MokoRes.strings.create_your_first_chat),
                fontSize = 15.sp,
                lineHeight = 15.sp,
                fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                fontWeight = FontWeight(400),
                letterSpacing = TextUnit(0F, TextUnitType.Sp),
                textAlign = TextAlign.Center,
                color = colors.secondary,
                maxLines = 3,
            )
        }
    }
}


@Composable
fun EncryptionInfoCard() {
    val colors = MaterialTheme.colorScheme

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
            colorFilter =  ColorFilter.tint(colors.primary)
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
            color = colors.primary,
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
    Spacer(modifier = Modifier.height(60.dp))
}
}

fun SearchDto.toContactDTO(): ContactDTO {
    return ContactDTO(
        id = this.userId,
        login = this.login,
        firstName = this.firstName,
        lastName = this.lastName,
        phone = this.phone ?: "", // Устанавливаем пустую строку, если phone == null
        icon = this.icon,
        notificationToken = this.notificationToken
    )
}