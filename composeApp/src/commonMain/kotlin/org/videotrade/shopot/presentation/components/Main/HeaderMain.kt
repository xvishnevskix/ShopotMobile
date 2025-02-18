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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import org.videotrade.shopot.domain.model.NewsItem
import org.videotrade.shopot.presentation.components.Call.CallBar
import org.videotrade.shopot.presentation.components.Common.ButtonStyle
import org.videotrade.shopot.presentation.components.Common.CustomButton
import org.videotrade.shopot.presentation.components.Common.ReconnectionBar
import org.videotrade.shopot.presentation.components.Main.News.StoryCircle
import org.videotrade.shopot.presentation.tabs.ContactsTab
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.add_main
import shopot.composeapp.generated.resources.logo_circle_gr
import shopot.composeapp.generated.resources.pepe
import shopot.composeapp.generated.resources.search_icon

@Composable
fun HeaderMain(
    isSearching: MutableState<Boolean>,
    news: List<NewsItem>,
    onStoryClick: (NewsItem) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val tabNavigator = LocalTabNavigator.current

    println("Search state: ${isSearching.value}")

    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.weight(1f)
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

                // Отображаем кружочек для каждой новости
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
//                    if (news.isNotEmpty()) {
//                        LazyRow(
//                            modifier = Modifier.fillMaxWidth(),
//                            horizontalArrangement = Arrangement.spacedBy(4.dp),
//                        ) {
//                            items(news) { newsItem ->
//                                StoryCircle(
//                                    isSeen = newsItem.viewed,
//                                    imageId = newsItem.imageIds.firstOrNull(),
//                                    onClick = {
//                                        onStoryClick(newsItem)
//                                    }
//                                )
//                            }
//                        }
//                    }
                }
                Spacer(modifier = Modifier.width(11.dp))
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Crossfade(targetState = isSearching.value) { searching ->
                    if (!searching) {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 5.dp)
                                .clickable { isSearching.value = true }
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.search_icon),
                                contentDescription = "Search",
                                modifier = Modifier.size(18.dp),
                                colorFilter = ColorFilter.tint(colors.primary)
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
                        modifier = Modifier.size(18.dp),
                        colorFilter = ColorFilter.tint(colors.primary)
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















