package org.videotrade.shopot.presentation.components.Chat

import Avatar
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import coil3.compose.rememberAsyncImagePainter
import com.seiko.imageloader.rememberImagePainter
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.api.EnvironmentConfig
import org.videotrade.shopot.api.EnvironmentConfig.SERVER_URL
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.domain.model.StickerPack
import org.videotrade.shopot.multiplatform.FileProviderFactory
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Montserrat_Medium
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Medium
import shopot.composeapp.generated.resources.check_mark
import shopot.composeapp.generated.resources.person
import shopot.composeapp.generated.resources.sticker1




@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StickerMenuContent(chat: ChatItem, onStickerClick: (String) -> Unit) {
    val tabTitles = listOf(
//        stringResource(MokoRes.strings.recent),
        stringResource(MokoRes.strings.favorite),
        stringResource(MokoRes.strings.store)

    )
    val pagerState = rememberPagerState(pageCount = { tabTitles.size })
    val selectedTabIndex = remember {
        derivedStateOf { pagerState.currentPage }
    }
    val viewModel: ChatViewModel = koinInject()
    val colors = MaterialTheme.colorScheme



    Column(modifier = Modifier
        .fillMaxHeight(0.5f)
        .fillMaxWidth()
        .background(colors.surface)
        .windowInsetsPadding(WindowInsets.navigationBars) // This line adds padding for the navigation bar
        
    )
    {
        val coroutineScope = rememberCoroutineScope()


        TabRow(
            selectedTabIndex = selectedTabIndex.value,
            modifier = Modifier.fillMaxWidth(),
            indicator = @Composable { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[selectedTabIndex.value])
                        .clip(RoundedCornerShape(8.dp)),
                    height = 3.dp,
                    color = Color(0xFFCAB7A3)
                )
            }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex.value == index,
                    selectedContentColor = colors.primary,
                    unselectedContentColor = colors.secondary,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.surface)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                ) {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        lineHeight = 16.sp,
                        fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                        fontWeight = FontWeight(400),

                        letterSpacing = TextUnit(0F, TextUnitType.Sp),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth().fillMaxHeight()
        ) { page ->
            when (page) {
//                0 -> RecentStickersContent(stickerPacks, viewModel)
                0 -> FavoriteStickersContent(viewModel, chat, onStickerClick)
                1 -> StoreStickersContent(viewModel, chat, onStickerClick)
            }
        }
    }
}

@Composable
fun StickerItem(stickerId: String, viewModel: ChatViewModel = koinInject(), chat: ChatItem, onClick: (String) -> Unit) {
    val colors = MaterialTheme.colorScheme
    val imagePainter = if (stickerId.isNullOrBlank()) {
        painterResource(Res.drawable.sticker1)
    } else {
        rememberAsyncImagePainter("${SERVER_URL}file/plain/$stickerId")
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(4.dp).clickable {
            viewModel.sendStickerMessage(chat, stickerId)
            onClick(stickerId)
        }
    ) {
        println("Я СТИКЕР $stickerId")
        println("Я СТИКЕР $imagePainter")
        println("Я СТИКЕР ${SERVER_URL}file/plain/$stickerId")
        Image(
            painter = imagePainter,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(64.dp)
        )
    }
}

@Composable
fun RecentStickersContent(stickerPacks: List<StickerPack>, viewModel: ChatViewModel = koinInject(), chat: ChatItem) {
    val colors = MaterialTheme.colorScheme
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(vertical = 4.dp, horizontal = 16.dp)
    ) {
        items(stickerPacks) { pack ->

            Column {
                Text(
                    text = pack.name,
                    fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
                    textAlign = TextAlign.Center,
                    fontSize = 19.sp,
                    lineHeight = 20.sp,
                    letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                    color = colors.primary,
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .padding(bottom = 10.dp)
                )

                pack.fileIds.chunked(5).forEach { rowStickers ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        rowStickers.forEach { sticker ->
//                                StickerItem(sticker, viewModel, chat)
                        }
                    }
                }

            }
        }
    }
}

@Composable
fun FavoriteStickersContent(viewModel: ChatViewModel = koinInject(), chat: ChatItem, onStickerClick: (String) -> Unit) {
    val colors = MaterialTheme.colorScheme
    val stickerPacks = viewModel.stickerPacks.collectAsState()
    val isLoading = viewModel.isLoading.collectAsState()
    val listState = rememberLazyListState()


    val favoriteStickerPacks = viewModel.favoriteStickerPacks.collectAsState()

    // Запрашиваем избранные пакеты, если их еще нет
    LaunchedEffect(Unit) {
        viewModel.getFavoritePacks()
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .map { visibleItems ->
                if (visibleItems.isNotEmpty()) {
                    visibleItems.last().index
                } else null
            }
            .distinctUntilChanged()
            .collect { lastVisibleItemIndex ->
                if (lastVisibleItemIndex != null && lastVisibleItemIndex >= stickerPacks.value.size - 1) {
                    viewModel.downloadStickerPacks()
                }
            }
    }

    Box(modifier = Modifier.fillMaxSize().background(colors.surface)) {

        // Показать индикатор загрузки, если пакеты еще не загружены
        if (isLoading.value && favoriteStickerPacks.value.isEmpty()) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = colors.primary
            )
        } else if (!isLoading.value && favoriteStickerPacks.value.isEmpty()) {
            // Показать сообщение, если нет избранных пакетов
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(MokoRes.strings.add_stickers_from_the_store),
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                    fontWeight = FontWeight(400),
                    color = colors.primary,
                    letterSpacing = TextUnit(0F, TextUnitType.Sp),
                )
            }
        }

        // Если есть избранные пакеты, отображаем их
        if (favoriteStickerPacks.value.isNotEmpty()) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(colors.surface)
                    .padding(vertical = 4.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(favoriteStickerPacks.value) { pack -> // Изменение: используем favoriteStickerPacks
                    Column {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 5.dp)
                                .padding(bottom = 10.dp)
                        ) {
                            Text(
                                text = pack.name,
                                fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
                                textAlign = TextAlign.Center,
                                fontSize = 19.sp,
                                lineHeight = 20.sp,
                                letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                                color = colors.primary,
                                modifier = Modifier
                                    .padding(horizontal = 5.dp)
                                    .padding(bottom = 10.dp)
                            )

                            Box(
                                modifier = Modifier.size(20.dp),
                                contentAlignment = Alignment.TopCenter
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = colors.secondary,
                                    modifier = Modifier
                                        .clickable {
                                            viewModel.removePackFromFavorites(pack.packId)
                                        }
                                )
                            }
                        }

                        // Отображаем стикеры из пакета
                        pack.fileIds.chunked(5).forEach { rowStickers ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                rowStickers.forEach { sticker ->
                                    if (sticker != null) {
                                        StickerItem(sticker, viewModel, chat) {
                                            onStickerClick(sticker)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Показать индикатор загрузки при прокрутке
                if (isLoading.value && favoriteStickerPacks.value.isNotEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = colors.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StoreStickersContent(viewModel: ChatViewModel = koinInject(), chat: ChatItem, onStickerClick: (String) -> Unit) {
    val colors = MaterialTheme.colorScheme
    val stickerPacks = viewModel.stickerPacks.collectAsState()
    val isLoading = viewModel.isLoading.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.downloadStickerPacks(reset = true)
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .map { visibleItems ->
                if (visibleItems.isNotEmpty()) {
                    visibleItems.last().index
                } else null
            }
            .distinctUntilChanged()
            .collect { lastVisibleItemIndex ->
                if (lastVisibleItemIndex != null && lastVisibleItemIndex >= stickerPacks.value.size - 1) {
                    viewModel.downloadStickerPacks()
                }
            }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        if (isLoading.value && stickerPacks.value.isEmpty()) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = colors.primary
            )
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(vertical = 4.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(stickerPacks.value) { pack ->
                Column {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 5.dp)
                            .padding(bottom = 10.dp)
                    ) {
                        Text(
                            text = pack.name,
                            fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
                            textAlign = TextAlign.Center,
                            fontSize = 19.sp,
                            lineHeight = 20.sp,
                            letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                            color = colors.primary
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .size(20.dp)
                                .background(Color(0xFFBBA796)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (pack.favorite) {
                                Image(
                                    painter = painterResource(Res.drawable.check_mark),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(15.dp)
                                        .clickable {
                                            viewModel.removePackFromFavorites(pack.packId)
                                        },
                                    colorFilter = ColorFilter.tint(Color.White)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clickable {
                                            viewModel.addPackToFavorites(pack.packId)
                                        }
                                )
                            }
                        }
                    }

                    // Горизонтально прокручиваемый Row для каждого пакета стикеров
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(pack.fileIds) { sticker ->
                            if (sticker != null) {
                                StickerItem(sticker, viewModel, chat) {
                                    onStickerClick(sticker)
                                }
                            }
                        }
                    }
                }
            }

            if (isLoading.value && stickerPacks.value.isNotEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = colors.primary
                        )
                    }
                }
            }
        }
    }
}