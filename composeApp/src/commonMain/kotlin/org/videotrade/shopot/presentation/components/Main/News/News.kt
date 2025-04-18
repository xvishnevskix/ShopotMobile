package org.videotrade.shopot.presentation.components.Main.News

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.rememberAsyncImagePainter
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.api.EnvironmentConfig.SERVER_URL
import org.videotrade.shopot.domain.model.NewsItem
import org.videotrade.shopot.multiplatform.openUrl
import org.videotrade.shopot.presentation.components.Common.ButtonStyle
import org.videotrade.shopot.presentation.components.Common.CustomButton
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.logo_circle_gr
import shopot.composeapp.generated.resources.sticker1
import kotlin.time.toDuration

@Composable
fun StoryCircle(
    isSeen: Boolean,
    imageId: String?,
    onClick: () -> Unit
) {
    val gradientBrush = Brush.linearGradient(
        colors = if (isSeen) {
            listOf(Color.Gray, Color.LightGray) // Цвет для просмотренных историй
        } else {
            listOf(Color(0xFF145A32), Color(0xFF32D74B), Color(0xFF145A32)) // Цвет для непросмотренных
        }
    )
    println("imageId imageId${imageId}")
    val imagePainter = rememberAsyncImagePainter(
        model = if (imageId.isNullOrBlank()) null else "${SERVER_URL}news/plain/$imageId",
        placeholder = painterResource(Res.drawable.logo_circle_gr),
        error = painterResource(Res.drawable.logo_circle_gr)
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(36.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    BorderStroke(2.dp, gradientBrush),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(4.dp)
        ) {
            Image(
                painter = imagePainter,
                contentDescription = "Story Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .fillMaxSize()
            )
        }
    }
}



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StoryViewer(
    news: NewsItem, // Одна новость
    onClose: () -> Unit,
    newsViewModel: NewsViewModel
) {
    val pagerState = rememberPagerState(pageCount = { news.imageIds.size })
    val scope = rememberCoroutineScope()

    // Прогресс для каждой истории
    val progressList = remember { mutableStateListOf(*Array(news.imageIds.size) { 0f }) }
    var isPaused by remember { mutableStateOf(false) }
    val storyDuration = news.duration // Продолжительность каждой истории

    // Управление прогрессом текущей истории
    LaunchedEffect(pagerState.currentPage, isPaused) {

        newsViewModel.markNewsAsViewed(news.id)

        if (!isPaused) {
            val progressStep = 30f / storyDuration

            // Сбрасываем правые истории
            for (i in pagerState.currentPage + 1 until progressList.size) {
                progressList[i] = 0f
            }

            // Прогресс текущей истории
            while (progressList[pagerState.currentPage] < 1f) {
                delay(30)
                if (!isPaused) {
                    progressList[pagerState.currentPage] += progressStep
                }
            }

            // Переход вперёд
            if (pagerState.currentPage < news.imageIds.size - 1) {
                scope.launch {
                    pagerState.scrollToPage(pagerState.currentPage + 1)
                }
            } else {
                onClose() // Закрыть сторис
            }
        }
    }

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .padding(4.dp)
                .clip(RoundedCornerShape(16.dp))
                .fillMaxSize()
                .background(Color.Black)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            isPaused = true
                            tryAwaitRelease()
                            isPaused = false
                        }
                    )
                }
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                userScrollEnabled = false
            ) { page ->
                val imageId = news.imageIds[page]
                val imagePainter = if (imageId.isNullOrBlank()) {
                    painterResource(Res.drawable.sticker1) // Фон по умолчанию
                } else {
                    rememberAsyncImagePainter("${SERVER_URL}news/plain/$imageId")
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    // Фоновое изображение
                    Image(
                        painter = imagePainter,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Прогресс-бары
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 8.dp)
                            .fillMaxWidth()
                            .height(4.dp)
                            .align(Alignment.TopCenter)
                    ) {
                        progressList.forEachIndexed { index, progress ->
                            LinearProgressIndicator(
                                progress = { progress.coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 1.dp)
                                    .clip(RoundedCornerShape(50)),
                                color = if (index <= pagerState.currentPage) Color.White else Color.Gray,
                            )
                        }
                    }

                    // Левая область для перехода назад
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(75.dp)
                            .align(Alignment.CenterStart)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                scope.launch {
                                    if (pagerState.currentPage > 0) {
                                        progressList[pagerState.currentPage - 1] = 0f
                                        pagerState.scrollToPage(pagerState.currentPage - 1)
                                    }
                                }
                            }
                    )

                    // Правая область для перехода вперёд
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(75.dp)
                            .align(Alignment.CenterEnd)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                scope.launch {
                                    if (pagerState.currentPage < news.imageIds.size - 1) {
                                        progressList[pagerState.currentPage] = 1f
                                        pagerState.scrollToPage(pagerState.currentPage + 1)
                                    } else {
                                        onClose()
                                    }
                                }
                            }
                    )

                    // Кнопка закрытия
                    Text(
                        "✕",
                        fontSize = 20.sp,
                        color = Color.White,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 8.dp)
                            .padding(16.dp)
                            .clickable { onClose() }
                    )
                }
            }

            // Кнопка по центру внизу
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.BottomCenter),
                contentAlignment = Alignment.Center
            ) {
                CustomButton(
                    text = news.buttonText,
                    style = ButtonStyle.Gradient,
                    onClick = {
                        scope.launch {
                            if (news.actionUrl == "") {
                                if (pagerState.currentPage < news.imageIds.size - 1) {
                                    progressList[pagerState.currentPage] = 1f
                                    pagerState.scrollToPage(pagerState.currentPage + 1)
                                } else {
                                    onClose()
                                    news.actionUrl
                                }
                            } else {
                                openUrl(news.actionUrl)
                            }
                        }
                    }
                )
            }
        }
    }
}


