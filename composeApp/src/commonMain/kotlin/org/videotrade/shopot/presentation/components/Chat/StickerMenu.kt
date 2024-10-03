package org.videotrade.shopot.presentation.components.Chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Medium



//data class StickerPack(
//    val packName: String,
//    val stickers: List<Sticker>
//)

//val stickerPacks = listOf(
//    StickerPack(
//        packName = "Набор стикеров 1",
//        stickers = listOf(
//            Sticker(
//                name = "Стикер 1",
//                imageRes = Res.drawable.sticker1, // Убедитесь, что изображение добавлено в drawable
//                path = "sticker.webp",
//                emoji = listOf("😊", "😎")
//            ),
//            Sticker(
//                name = "Стикер 2",
//                imageRes = Res.drawable.sticker1, // Убедитесь, что изображение добавлено в drawable
//                path = "sticker2.webp",
//                emoji = listOf("😂", "😜")
//            ),
//            Sticker(
//                name = "Стикер 3",
//                imageRes = Res.drawable.sticker1, // Убедитесь, что изображение добавлено в drawable
//                path = "sticker3.webp",
//                emoji = listOf("😉", "😍")
//            ),
//            Sticker(
//                name = "Стикер 4",
//                imageRes = Res.drawable.sticker1, // Убедитесь, что изображение добавлено в drawable
//                path = "sticker4.webp",
//                emoji = listOf("😇", "🤓")
//            )
//            ,
//            Sticker(
//                name = "Стикер 1",
//                imageRes = Res.drawable.sticker1, // Убедитесь, что изображение добавлено в drawable
//                path = "sticker.webp",
//                emoji = listOf("😊", "😎")
//            ),
//            Sticker(
//                name = "Стикер 2",
//                imageRes = Res.drawable.sticker1, // Убедитесь, что изображение добавлено в drawable
//                path = "sticker2.webp",
//                emoji = listOf("😂", "😜")
//            ),
//            Sticker(
//                name = "Стикер 3",
//                imageRes = Res.drawable.sticker1, // Убедитесь, что изображение добавлено в drawable
//                path = "sticker3.webp",
//                emoji = listOf("😉", "😍")
//            ),
//            Sticker(
//                name = "Стикер 4",
//                imageRes = Res.drawable.sticker1, // Убедитесь, что изображение добавлено в drawable
//                path = "sticker4.webp",
//                emoji = listOf("😇", "🤓")
//            )
//        )
//    ),
//    StickerPack(
//        packName = "Набор стикеров 1",
//        stickers = listOf(
//            Sticker(
//                name = "Стикер 1",
//                imageRes = Res.drawable.sticker1, // Убедитесь, что изображение добавлено в drawable
//                path = "sticker.webp",
//                emoji = listOf("😊", "😎")
//            ),
//            Sticker(
//                name = "Стикер 2",
//                imageRes = Res.drawable.sticker1, // Убедитесь, что изображение добавлено в drawable
//                path = "sticker2.webp",
//                emoji = listOf("😂", "😜")
//            ),
//            Sticker(
//                name = "Стикер 3",
//                imageRes = Res.drawable.sticker1, // Убедитесь, что изображение добавлено в drawable
//                path = "sticker3.webp",
//                emoji = listOf("😉", "😍")
//            ),
//            Sticker(
//                name = "Стикер 4",
//                imageRes = Res.drawable.sticker1, // Убедитесь, что изображение добавлено в drawable
//                path = "sticker4.webp",
//                emoji = listOf("😇", "🤓")
//            )
//            ,
//            Sticker(
//                name = "Стикер 1",
//                imageRes = Res.drawable.sticker1, // Убедитесь, что изображение добавлено в drawable
//                path = "sticker.webp",
//                emoji = listOf("😊", "😎")
//            ),
//            Sticker(
//                name = "Стикер 2",
//                imageRes = Res.drawable.sticker1, // Убедитесь, что изображение добавлено в drawable
//                path = "sticker2.webp",
//                emoji = listOf("😂", "😜")
//            ),
//            Sticker(
//                name = "Стикер 3",
//                imageRes = Res.drawable.sticker1, // Убедитесь, что изображение добавлено в drawable
//                path = "sticker3.webp",
//                emoji = listOf("😉", "😍")
//            ),
//            Sticker(
//                name = "Стикер 4",
//                imageRes = Res.drawable.sticker1, // Убедитесь, что изображение добавлено в drawable
//                path = "sticker4.webp",
//                emoji = listOf("😇", "🤓")
//            )
//        )
//    ),
//    StickerPack(
//        packName = "Набор стикеров 2",
//        stickers = listOf(
//            Sticker(
//                name = "Стикер 1",
//                imageRes = Res.drawable.sticker1, // Убедитесь, что изображение добавлено в drawable
//                path = "sticker5.webp",
//                emoji = listOf("😉", "😅")
//            )
//        )
//    )
//)

@Serializable
data class Sticker(
    val name: String?,
//    val imageRes: DrawableResource,
    val path: String?,
    val emoji: List<String>
)

@Serializable
data class StickerPack(
    val packId: String?,
    val packName: String?,
    val stickers: List<Sticker>? = null // Если есть список стикеров внутри
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StickerMenuContent() {
    val tabTitles = listOf("Недавние", "Избранное", "Магазин")
    val pagerState = rememberPagerState(pageCount = { tabTitles.size })
    val selectedTabIndex = remember {
        derivedStateOf { pagerState.currentPage }
    }

    val viewModel: ChatViewModel = koinInject()

    val stickerPacks by viewModel.stickerPacks.collectAsState()

    LaunchedEffect(Unit) {

        viewModel.downloadStickerPacks()

        if (stickerPacks.isNotEmpty()) {
            println("ПАКИИИИ ${stickerPacks[0].packId}")
        }
    }



    Column(modifier = Modifier.fillMaxHeight(0.5f).fillMaxWidth()) {
        val coroutineScope = rememberCoroutineScope()

        Column {
            if (stickerPacks.isEmpty()) {
                Text("Загрузка стикеров...")
            } else {
                LazyColumn {
                    items(stickerPacks) { stickerPack ->
                        Text("Pack Name: ${stickerPack.packName}")
                    }
                }
            }
        }


        // TabRow для переключателя
        TabRow(
            selectedTabIndex = selectedTabIndex.value,
            modifier = Modifier.fillMaxWidth(),
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
                        .clip(
                            RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                        ),
                ) {
                    Text(text = title,
                        modifier = Modifier,
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                        fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Medium)),
                        letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                        lineHeight = 20.sp,
                        )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // HorizontalPager для свайпинга между вкладками
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth().fillMaxHeight()
        ) { page ->
            when (page) {
                0 -> RecentStickersContent()
                1 -> FavoriteStickersContent()
                2 -> StoreStickersContent()
            }
        }
    }
}

@Composable
fun StickerItem(sticker: Sticker) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(4.dp)
    ) {
//        Image(
//            painter = painterResource(sticker.imageRes),
//            contentDescription = null,
//            modifier = Modifier
//                .size(64.dp) // Установите нужный размер стикера
//        )
        sticker.name?.let { Text(text = it, style = androidx.compose.material.MaterialTheme.typography.body2) }
    }
}

@Composable
fun RecentStickersContent() {
    // Здесь можно отобразить "Недавние" стикеры
//    Text(text = "Здесь будут показаны недавние стикеры.")

//        LazyColumn(
//        modifier = Modifier
//            .fillMaxWidth()
//            .fillMaxHeight()
//            .padding(16.dp)
//    ) {
//        items(stickerPacks) { pack ->
//            Text(
//                text = pack.packName,
//                modifier = Modifier.padding(vertical = 8.dp),
//                style = androidx.compose.material.MaterialTheme.typography.h6
//            )
//
//            // Отображение стикеров
//            pack.stickers.chunked(5).forEach { rowStickers ->
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(vertical = 8.dp),
//                    horizontalArrangement = Arrangement.Start
//                ) {
//                    rowStickers.forEach { sticker ->
//                        StickerItem(sticker)
//                    }
//                }
//            }
//        }
//    }
}

@Composable
fun FavoriteStickersContent() {
    // Здесь можно отобразить "Избранные" стикеры
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Здесь будут показаны избранные стикеры.")
    }
}

@Composable
fun StoreStickersContent() {

    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
    Text(text = "Здесь будет магазин стикеров.")
    }
}