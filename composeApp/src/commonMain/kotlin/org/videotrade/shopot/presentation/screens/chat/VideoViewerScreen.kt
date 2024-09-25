package org.videotrade.shopot.presentation.screens.chat

import PhotoViewerHeader
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
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
import cafe.adriel.voyager.core.screen.Screen
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.seiko.imageloader.rememberImagePainter
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.api.EnvironmentConfig.serverUrl
import org.videotrade.shopot.presentation.components.Common.SafeArea
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.pepe
import shopot.composeapp.generated.resources.person

class VideoViewerScreen(
//    private val imageFilePath: String?,
    private val messageSenderName: String? = null,
//    private val icon: String? = null
) : Screen {
    @Composable
    override fun Content() {


        var isHeaderVisible = remember { mutableStateOf(true) }

//        val imagePainter = if (imageFilePath != null) {
//            rememberAsyncImagePainter(imageFilePath)
//        } else {
//            rememberImagePainter("${serverUrl}file/plain/$icon")
//        }


        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF29303c))
        ) {

            Image(
                painter = painterResource(Res.drawable.pepe),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()

                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                isHeaderVisible.value = !isHeaderVisible.value
                            }
                        )
                    }

            )

            // Хэдер, который отображается поверх изображения
            AnimatedVisibility(
                visible = isHeaderVisible.value,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.TopCenter) // Размещаем хэдер поверх изображения
            ) {
                PhotoViewerHeader("$messageSenderName", "")
            }
        }
    }
}