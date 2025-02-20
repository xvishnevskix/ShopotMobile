package org.videotrade.shopot.presentation.components.Main

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ChatSkeleton() {
    val colors = MaterialTheme.colorScheme
    // Бесконечная анимация перелива
    val transition = rememberInfiniteTransition()
    val shimmerTranslateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 2000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1800, // Скорость перелива
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )

    // Градиент для эффекта перелива
    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            colors.onBackground,
            colors.onPrimary,
            colors.onBackground,
        ),
        start = Offset.Zero,
        end = Offset(x = shimmerTranslateAnim, y = shimmerTranslateAnim) // Плавное перемещение по X и Y
    )

    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(12) {
            Row(
                modifier = Modifier
                    .background(Color.Transparent)
                    .fillMaxWidth()
                    .clickable {},
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.Top
                ) {
                    // Круглый элемент скелетона
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(colors.onBackground)
                            .size(56.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        modifier = Modifier,
                        verticalArrangement = Arrangement.Top
                    ) {
                        // Прямоугольный элемент для текста
                        Box(
                            modifier = Modifier
                                .width(50.dp)
                                .height(8.dp)
                                .background(shimmerBrush, shape = RoundedCornerShape(size = 30.dp))
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row {
                            // Длинный прямоугольник
                            Box(
                                modifier = Modifier
                                    .width(163.dp)
                                    .height(8.dp)
                                    .background(shimmerBrush, shape = RoundedCornerShape(size = 100.dp))
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            // Короткий прямоугольник
                            Box(
                                modifier = Modifier
                                    .width(50.dp)
                                    .height(8.dp)
                                    .background(shimmerBrush, shape = RoundedCornerShape(size = 30.dp))
                            )
                        }
                    }
                }

                // Короткий прямоугольник справа
                Box(
                    modifier = Modifier
                        .width(30.dp)
                        .height(8.dp)
                        .background(shimmerBrush, shape = RoundedCornerShape(size = 30.dp))
                )
            }
        }
    }

}