package org.videotrade.shopot.presentation.screens.chat


import ViewerHeader
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import cafe.adriel.voyager.core.screen.Screen
import org.videotrade.shopot.api.formatTimeOnly
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.multiplatform.VideoPlayer

class VideoViewerScreen(
    private val messageSenderName: String? = null,
    private val message: MessageItem,
    private val filePath: String,
) : Screen {
    @Composable
    override fun Content() {
        val colors = MaterialTheme.colorScheme
        val isHeaderVisible = remember { mutableStateOf(true) }


        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
        ) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                isHeaderVisible.value = !isHeaderVisible.value
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    VideoPlayer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.9f), // Займёт 90% высоты экрана, оставляя небольшой отступ сверху и снизу
                        filePath = filePath,
                    )
                }
            }

            // Хэдер, который отображается поверх изображения
            AnimatedVisibility(
                visible = isHeaderVisible.value,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.TopCenter) // Размещаем хэдер поверх изображения
            ) {
                ViewerHeader("$messageSenderName", formatTimeOnly(message.created))
            }
        }
    }
}