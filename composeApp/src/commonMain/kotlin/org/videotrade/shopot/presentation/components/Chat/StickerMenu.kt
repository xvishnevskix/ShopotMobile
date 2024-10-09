package org.videotrade.shopot.presentation.components.Chat

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import shopot.composeapp.generated.resources.Montserrat_Medium
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Medium
import shopot.composeapp.generated.resources.check_mark
import shopot.composeapp.generated.resources.sticker1




val stickerPacksTest = listOf(
    StickerPackTest(
        name = "Набор стикеров 1",
        id = "",
        favorite = false,
        stickers = listOf(
            Sticker(
                id = "Стикер 1",
                fileUrl = "",
                fileName = "",
            ),


            )
    ),
)

data class StickerPackTest(
    val id: String,
    val favorite: Boolean,
    val name: String,
    val stickers: List<Sticker>,

    )

@Serializable
data class Sticker(
    val id: String,         // UUID, представленный как String
    val fileUrl: String,     // URL файла (это поле нужно извлечь из объекта File)
    val fileName: String?,   // Название файла (может быть nullable)
//    val data: ByteArray?
)

@Serializable
data class StickerPack(
    val id: String,
    val name: String,
    val favorite: Boolean,

)

@Serializable
data class FavoritePack(
    val userId: String,
    val packId: String

)

@Serializable
data class StickerPackResponse(
    val content: List<StickerPack>,
    val pageable: StickerPageable,
    val last: Boolean,
    val totalElements: Int,
    val totalPages: Int,
    val size: Int,
    val number: Int,
    val sort: StickerSort,
    val first: Boolean,
    val numberOfElements: Int,
    val empty: Boolean
)

@Serializable
data class StickerPageable(
    val pageNumber: Int,
    val pageSize: Int,
    val offset: Long,
    val paged: Boolean,
    val unpaged: Boolean,
    val sort: StickerSort
)

@Serializable
data class StickerSort(
    val empty: Boolean,
    val sorted: Boolean,
    val unsorted: Boolean
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StickerMenuContent() {
    val tabTitles = listOf(stringResource(MokoRes.strings.recent), stringResource(MokoRes.strings.favorite), stringResource(MokoRes.strings.store))
    val pagerState = rememberPagerState(pageCount = { tabTitles.size })
    val selectedTabIndex = remember {
        derivedStateOf { pagerState.currentPage }
    }

    val viewModel: ChatViewModel = koinInject()


    val stickerPacks by viewModel.stickerPacks.collectAsState()
    val stickers by viewModel.stickers.collectAsState()

    LaunchedEffect(Unit) {

        viewModel.downloadStickerPacks()
        viewModel.getStickersForPack("9f48cc7d-d215-429f-9dd8-d719a426835e")

        println("ПАКИИИИ ${stickerPacks}")
        println("СТИИКИИИИИ ${stickers}")

    }



    Column(modifier = Modifier.fillMaxHeight(0.5f).fillMaxWidth()) {
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
                    color = Color(0xFF2A293C)
                )
            }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex.value == index,
                    selectedContentColor = Color(0xFF29303C),
                    unselectedContentColor = Color(0xFFA9A8AA),
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                ) {
                    Text(
                        text = title,
                        modifier = Modifier,
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                        fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Medium)),
                        letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                        lineHeight = 20.sp
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
                0 -> RecentStickersContent(stickerPacks)
                1 -> FavoriteStickersContent(stickerPacks.filter { it.favorite })
                2 -> StoreStickersContent(stickerPacks, viewModel)
            }
        }
    }
}

@Composable
fun StickerItem() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(4.dp)
    ) {
        Image(
            painter = painterResource(Res.drawable.sticker1),
            contentDescription = null,
            modifier = Modifier
                .size(64.dp) // Установите нужный размер стикера
        )
    }
}

@Composable
fun RecentStickersContent(stickerPacks: List<StickerPack>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(vertical = 4.dp, horizontal = 16.dp)
    ) {
        items(stickerPacks) { pack ->
            Column {
                Text(
                    text = "${pack.name}",
                    fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
                    textAlign = TextAlign.Center,
                    fontSize = 19.sp,
                    lineHeight = 20.sp,
                    letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                    color = Color(0xFF000000),
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .padding(bottom = 10.dp)
                )
                stickerPacksTest.forEach { pack ->
                    pack.stickers.chunked(5).forEach { rowStickers ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            rowStickers.forEach { sticker ->
                                StickerItem()
                            }
                        }
                    }
                }
//                StickerItem()
            }
        }
    }
}

@Composable
fun FavoriteStickersContent(favoritePacks: List<StickerPack>) {


    if (favoritePacks.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(vertical = 4.dp, horizontal = 16.dp)
        ) {
            items(favoritePacks) { pack ->
                Column {
                    Text(
                        text = "${pack.name}",
                        fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
                        textAlign = TextAlign.Center,
                        fontSize = 19.sp,
                        lineHeight = 20.sp,
                        letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                        color = Color(0xFF000000),
                        modifier = Modifier
                            .padding(horizontal = 5.dp)
                            .padding(bottom = 10.dp)
                    )
                    stickerPacksTest.forEach { pack ->
                        pack.stickers.chunked(5).forEach { rowStickers ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                rowStickers.forEach { sticker ->
                                    StickerItem()
                                }
                            }
                        }
                    }
//                    StickerItem()
                }
            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = stringResource(MokoRes.strings.add_stickers_from_the_store))
        }
    }
}

@Composable
fun StoreStickersContent(stickerPacks: List<StickerPack>, viewModel: ChatViewModel = koinInject()) {
    val profile = viewModel.profile.collectAsState().value

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(vertical = 4.dp, horizontal = 16.dp)
    ) {
        items(stickerPacks) { pack ->
            Column {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                    ,

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp)
                        .padding(bottom = 10.dp)
                ) {
                    Text(
                        text = "${pack.name}",
                        fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
                        textAlign = TextAlign.Center,
                        fontSize = 19.sp,
                        lineHeight = 20.sp,
                        letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                        color = Color(0xFF000000)
                    )


                    Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).size(20.dp).background(Color(0xFF2A293C)),
                        contentAlignment = Alignment.Center
                    ){

                        if (pack.favorite) {
                            Image(
                                painter = painterResource(Res.drawable.check_mark),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(15.dp),
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
                                        viewModel.addPackToFavorites(pack.id, profile.id)
                                    }
                            )
                        }





                    }
                }

                stickerPacksTest.forEach { pack ->
                    pack.stickers.chunked(5).forEach { rowStickers ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            rowStickers.forEach { sticker ->
                                StickerItem()
                            }
                        }
                    }
                }
                //                    StickerItem()
            }
        }

    }
}