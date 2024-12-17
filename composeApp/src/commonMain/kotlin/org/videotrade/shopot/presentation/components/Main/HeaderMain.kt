package org.videotrade.shopot.presentation.components.Main

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import coil3.compose.AsyncImage
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.presentation.components.Call.CallBar
import org.videotrade.shopot.presentation.components.Common.ButtonStyle
import org.videotrade.shopot.presentation.components.Common.CustomButton
import org.videotrade.shopot.presentation.components.Common.ReconnectionBar
import org.videotrade.shopot.presentation.tabs.ContactsTab
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.add_main
import shopot.composeapp.generated.resources.pepe
import shopot.composeapp.generated.resources.search_icon

@Composable
fun HeaderMain(isSearching: MutableState<Boolean>, onStoryClick: () -> Unit) {
    val interactionSource =
        remember { MutableInteractionSource() }  // Создаем источник взаимодействия
    val colors = MaterialTheme.colorScheme
    val tabNavigator = LocalTabNavigator.current



    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,

            ) {


                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        stringResource(MokoRes.strings.chats),
                        fontSize = 24.sp,
                        lineHeight = 24.sp,
                        fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                        fontWeight = FontWeight(500),
                        textAlign = TextAlign.Center,
                        color = colors.primary,
                        letterSpacing = TextUnit(0F, TextUnitType.Sp)

                    )
                    Spacer(modifier = Modifier.width(11.dp))
                    Row {
                        StoryCircle(
                            isSeen = false,
                            onClick = onStoryClick
                        )

                    }
                }


        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            Crossfade(targetState = isSearching.value) { searching ->
                if (!searching) {
                    Box(modifier = Modifier.padding(horizontal = 5.dp).pointerInput(Unit) {
                        isSearching.value = true
                    }) {
                        Image(
                            painter = painterResource(Res.drawable.search_icon),
                            contentDescription = "Search",
                            modifier = Modifier

                                .size(18.dp)
                                ,
                            colorFilter =  ColorFilter.tint(colors.primary)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(11.dp))

            Box(modifier = Modifier.padding(horizontal = 5.dp).pointerInput(Unit) {
                tabNavigator.current = ContactsTab
            }) {
                Image(
                    painter = painterResource(Res.drawable.add_main),
                    contentDescription = "Search",
                    modifier = Modifier
                        .size(18.dp)
                    ,
                    colorFilter =  ColorFilter.tint(colors.primary)
                )
            }
        }

        }
        Box(Modifier.padding(top = 2.dp)) {
            ReconnectionBar()
            CallBar()
        }


    }

}


@Composable
fun StoryCircle(
    isSeen: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF145A32), // Тёмно-зелёный
            Color(0xFF32D74B), // Основной зелёный
            Color(0xFF145A32),  // Повтор тёмно-зелёного

        )
    )
    val borderBrush = if (isSeen) Brush.linearGradient() else gradientBrush// Цвет внешнего кружка

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(36.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() },// Размер общего кружка
    ) {
        // Внешний круг с границей
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    BorderStroke(2.dp, borderBrush), // Толщина и цвет границы
                    shape = CircleShape
                )
                .padding(4.dp) // Внутренний отступ для изображения
        ) {
            // Внутренний круг с изображением
            Image(
                painter = painterResource(Res.drawable.pepe),
                contentDescription = "Story Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(CircleShape) // Скругление до формы круга
                    .fillMaxSize()     // Заполнение всего доступного пространства
            )
        }
    }
}



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StoryViewer(
    stories: List<DrawableResource>, // Список ресурсов для историй
    onClose: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { stories.size })
    val scope = rememberCoroutineScope()

    // Хранение прогресса для каждой истории
    val progressList = remember { mutableStateListOf(*Array(stories.size) { 0f }) }
    var isPaused by remember { mutableStateOf(false) }
    val storyDuration = 5000L

    // Управление прогрессом текущей истории
    LaunchedEffect(pagerState.currentPage, isPaused) {
        if (!isPaused) {
            val progressStep = 30f / storyDuration // Шаг прогресса: меньше шаг для длинных историй

            // Сбрасываем правые истории
            for (i in pagerState.currentPage + 1 until progressList.size) {
                progressList[i] = 0f
            }

            // Прогресс текущей истории
            while (progressList[pagerState.currentPage] < 1f) {
                delay(30) // Шаг времени обновления прогресса
                if (!isPaused) {
                    progressList[pagerState.currentPage] += progressStep
                }
            }

            // Переход вперёд
            if (pagerState.currentPage < stories.size - 1) {
                scope.launch {
                    pagerState.scrollToPage(pagerState.currentPage + 1)
                }
            } else {
                onClose() // Закрыть сторис
            }
        }
    }

    Dialog(onDismissRequest = onClose) {
        Box(
            modifier = Modifier
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
                Box(modifier = Modifier.fillMaxSize()) {
                    // Фоновое изображение
                    Image(
                        painter = painterResource(stories[page]),
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
                                progress = progress.coerceIn(0f, 1f),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 1.dp)
                                    .clip(RoundedCornerShape(50)),
                                color = if (index <= pagerState.currentPage) Color.White else Color.Gray
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
                                        progressList[pagerState.currentPage - 1] = 0f // Сбрасываем прогресс предыдущего
                                        pagerState.scrollToPage(pagerState.currentPage - 1) // Переход назад
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
                                    if (pagerState.currentPage < stories.size - 1) {
                                        progressList[pagerState.currentPage] = 1f // Завершаем текущий прогресс
                                        pagerState.scrollToPage(pagerState.currentPage + 1) // Переход вперёд
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
                    text = stringResource(MokoRes.strings.next),
                    style = ButtonStyle.Gradient,
                    onClick = {
                        scope.launch {
                            if (pagerState.currentPage < stories.size - 1) {
                                progressList[pagerState.currentPage] = 1f // Завершаем текущий прогресс
                                pagerState.scrollToPage(pagerState.currentPage + 1) // Переход вперёд
                            } else {
                                onClose()
                            }
                        }
                    }
                )
            }
        }
    }
}













