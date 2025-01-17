package org.videotrade.shopot.presentation.components.Common

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.dp


@Composable
fun LogoLoading() {

    val offsetY = remember { Animatable(0f) }
    val animationRange = 25f // Амплитуда движения точки вверх-вниз

    LaunchedEffect(Unit) {
        offsetY.animateTo(
            targetValue = animationRange, // Движение вверх
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1200),
                repeatMode = RepeatMode.Reverse // Возврат вниз
            )
        )
    }

    Box(
        modifier = Modifier
            .size(65.dp, 30.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()

        ) {

//            val scaleX = size.width / 195f
//            val scaleY = size.height / 132f

            scale(2.7f, 2.7f) {
                // Палка 1
                val path1 = Path().apply {
                    moveTo(136.5f, 0f)
                    cubicTo(138.98f, 0f, 141f, 1.97f, 141f, 4.4f)
                    lineTo(141f, 52.52f)
                    cubicTo(137.74f, 97.16f, 110.59f, 121.12f, 80.29f, 129.01f)
                    cubicTo(50.55f, 136.75f, 17.05f, 129.11f, 1f, 109.76f)
                    cubicTo(-0.57f, 107.87f, -0.27f, 105.1f, 1.66f, 103.57f)
                    cubicTo(3.59f, 102.04f, 6.43f, 102.33f, 7.99f, 104.21f)
                    cubicTo(21.17f, 120.11f, 50.57f, 127.64f, 77.97f, 120.51f)
                    cubicTo(104.78f, 113.52f, 128.99f, 92.59f, 132f, 52.21f)
                    lineTo(132f, 4.4f)
                    cubicTo(132f, 1.97f, 134.01f, 0f, 136.5f, 0f)
                    close()
                }
                drawPath(path1, Color(0xFF373533), style = Fill)

                // Палка 2
                val path2 = Path().apply {
                    moveTo(163.5f, 0f)
                    cubicTo(165.98f, 0f, 168f, 1.97f, 168f, 4.4f)
                    lineTo(168f, 52.52f)
                    cubicTo(164.74f, 97.16f, 137.59f, 121.12f, 107.29f, 129.01f)
                    cubicTo(77.55f, 136.75f, 44.05f, 129.11f, 28f, 109.76f)
                    cubicTo(26.43f, 107.87f, 26.73f, 105.1f, 28.66f, 103.57f)
                    cubicTo(30.59f, 102.04f, 33.43f, 102.33f, 34.99f, 104.21f)
                    cubicTo(48.17f, 120.11f, 77.57f, 127.64f, 104.97f, 120.51f)
                    cubicTo(131.78f, 113.52f, 155.99f, 92.59f, 159f, 52.21f)
                    lineTo(159f, 4.4f)
                    cubicTo(159f, 1.97f, 161.01f, 0f, 163.5f, 0f)
                    close()
                }
                drawPath(path2, Color(0xFF373533), style = Fill)

                // Палка 3
                val path3 = Path().apply {
                    moveTo(190.5f, 0f)
                    cubicTo(192.98f, 0f, 195f, 1.97f, 195f, 4.4f)
                    lineTo(195f, 52.52f)
                    cubicTo(191.74f, 97.16f, 164.59f, 121.12f, 134.29f, 129.01f)
                    cubicTo(104.55f, 136.75f, 71.05f, 129.11f, 55f, 109.76f)
                    cubicTo(53.43f, 107.87f, 53.73f, 105.1f, 55.66f, 103.57f)
                    cubicTo(57.59f, 102.04f, 60.43f, 102.33f, 61.99f, 104.21f)
                    cubicTo(75.17f, 120.11f, 104.57f, 127.64f, 131.97f, 120.51f)
                    cubicTo(158.78f, 113.52f, 182.99f, 92.59f, 186f, 52.21f)
                    lineTo(186f, 4.4f)
                    cubicTo(186f, 1.97f, 188.01f, 0f, 190.5f, 0f)
                    close()
                }
                drawPath(path3, Color(0xFF373533), style = Fill)

                // Анимируемая точка 4
                translate(top = offsetY.value - 25f) { // начальное смещение сверху
                    val path4 = Path().apply {
                        addOval(
                            androidx.compose.ui.geometry.Rect(
                                left = 100f,
                                top = 5.5f,
                                right = 111f,
                                bottom = 16.5f
                            )
                        )
                    }
                    drawPath(path4, Color(0xFF373533), style = Fill)
                }

                // Статичный круг 5
                val path5 = Path().apply {
                    addOval(
                        androidx.compose.ui.geometry.Rect(
                            left = 100f,
                            top = 35.5f,
                            right = 111f,
                            bottom = 46.5f
                        )
                    )
                }
                drawPath(path5, Color(0xFF373533), style = Fill)
            }
        }

    }
}