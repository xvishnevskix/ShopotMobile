package org.videotrade.shopot.presentation.screens.chat


import ViewerHeader
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.seiko.imageloader.rememberImagePainter
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.api.EnvironmentConfig.serverUrl
import org.videotrade.shopot.api.formatTimeOnly
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.multiplatform.VideoPlayer
import org.videotrade.shopot.presentation.components.Common.SafeArea
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.pepe
import shopot.composeapp.generated.resources.person

class VideoViewerScreen(
    private val messageSenderName: String? = null,
    private val message: MessageItem,
) : Screen {
    @Composable
    override fun Content() {

        val isHeaderVisible = remember { mutableStateOf(true) }


        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF29303c))
        ) {

            Box(modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                isHeaderVisible.value = !isHeaderVisible.value
                            }
                        )
                    },
                contentAlignment = Alignment.Center) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceAround) {
                    VideoPlayer(
                        modifier = Modifier.fillMaxWidth().height(400.dp),
                        url =
                        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")

                    androidx.compose.material.Button(onClick = {  "Hello" }) { androidx.compose.material.Text("") }
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