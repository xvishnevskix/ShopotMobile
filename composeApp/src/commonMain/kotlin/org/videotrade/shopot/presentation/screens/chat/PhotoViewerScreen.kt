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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import cafe.adriel.voyager.core.screen.Screen
import coil3.compose.rememberAsyncImagePainter
import org.videotrade.shopot.presentation.components.Common.SafeArea

class PhotoViewerScreen(private val imageFilePath: String,private val messageSenderName: String? = null) : Screen {
    @Composable
    override fun Content() {
        var scale by remember { mutableStateOf(1f) }
        var offset by remember { mutableStateOf(Offset.Zero) }
        var imageSize by remember { mutableStateOf(IntSize.Zero) }
        var isHeaderVisible by remember { mutableStateOf(true) }
        
        val imagePainter = rememberAsyncImagePainter(imageFilePath)


        SafeArea{
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF29303c))
            ) {

                Image(
                    painter = imagePainter,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offset.x,
                            translationY = offset.y
                        )
                        .fillMaxSize()
                        .onGloballyPositioned { layoutCoordinates ->
                            imageSize = layoutCoordinates.size
                        }
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    isHeaderVisible = !isHeaderVisible
                                }
                            )
                        }
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                scale = (scale * zoom).coerceIn(1f, 5f)


                                val maxX = (imageSize.width * (scale - 1)) / 2f
                                val maxY = (imageSize.height * (scale - 1)) / 2f

                                offset = Offset(
                                    x = (offset.x + pan.x).coerceIn(-maxX, maxX),
                                    y = (offset.y + pan.y).coerceIn(-maxY, maxY)
                                )
                            }
                        }
                )

                // Хэдер, который отображается поверх изображения
                AnimatedVisibility(
                    visible = isHeaderVisible,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier.align(Alignment.TopCenter) // Размещаем хэдер поверх изображения
                ) {
                    PhotoViewerHeader("$messageSenderName", "")
                }
            }
        }

    }
}