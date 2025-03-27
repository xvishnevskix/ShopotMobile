package org.videotrade.shopot.presentation.components.Chat

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.chat_file_message
import shopot.composeapp.generated.resources.chat_micro
import shopot.composeapp.generated.resources.file_message
import shopot.composeapp.generated.resources.sticker_menu

@Composable
fun ChatStatus(
    userId: String,
    viewModel: ChatViewModel
) {
    val colors = MaterialTheme.colorScheme
    val userStatuses = viewModel.userStatuses.collectAsState()
    println("asdasasfasdasdafgasdasd ${userId}")
    val status = userStatuses.value[userId]
    println("asdasasfasdasdafgasdasd ${status}")

    val statusText = when (status) {
        "ONLINE" -> "В сети"
        "OFFLINE" -> "Не в сети"
        "TYPING" -> "Печатает"
        "SENDING_FILE" -> "Отправляет файл"
        "CHOOSING_STICKER" -> "Выбирает стикер"
        "RECORDING_VOICE" -> "Записывает голосовое"
        else -> ""
    }

    LaunchedEffect(status) {
        println("Composable ChatStatus recomposed: $userId -> $status")
    }


    AnimatedVisibility(
        visible = statusText != null,
        enter = fadeIn(tween(300)),
        exit = fadeOut(tween(300))
    ) {
        if (userStatuses.value.isNotEmpty()) {
            Row(verticalAlignment = Alignment.CenterVertically) {

                AnimatedContent(
                    targetState = statusText,
                    transitionSpec = {
                        fadeIn(tween(300)) togetherWith fadeOut(tween(300))
                    },
                    label = "status-text"
                ) { animatedText ->
                    Text(
                        text = animatedText ,
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        lineHeight = 16.sp,
                        fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                        fontWeight = FontWeight(500),
                        color = Color(0xFFCAB7A3),
                        letterSpacing = TextUnit(0F, TextUnitType.Sp),
                    )
                }

                if (status == "TYPING") {
                    Spacer(modifier = Modifier.width(6.dp))
                    TypingIndicator()
                }

                if (status != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                    StatusIcon(status)
                }
            }
        } else {
            Text(
                text = "Не в сети",
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                lineHeight = 16.sp,
                fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                fontWeight = FontWeight(500),
                color = colors.secondary,
                letterSpacing = TextUnit(0F, TextUnitType.Sp),
            )
        }
    }
}

@Composable
fun TypingIndicator() {
    val transition = rememberInfiniteTransition()

    val delays = listOf(0, 100, 200) // волна
    val animatedOffsets = delays.map { delay ->
        transition.animateFloat(
            initialValue = 0f,
            targetValue = -2.5f, // поднимается вверх
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 500,
                    delayMillis = delay,
                    easing = EaseInOutSine
                ),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.padding(start = 2.dp, top = 4.dp)
    ) {
        animatedOffsets.forEachIndexed { index, offsetY ->
            Box(
                modifier = Modifier
                    .offset(y = offsetY.value.dp)
                    .size(4.dp)
                    .background(Color(0xFFCAB7A3), CircleShape)
            )
            if (index != animatedOffsets.lastIndex) {
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}




@Composable
fun StatusIcon(status: String) {
    val res = when (status) {
        "RECORDING_VOICE" -> Res.drawable.chat_micro
        "SENDING_FILE" -> Res.drawable.chat_file_message
        "CHOOSING_STICKER" -> Res.drawable.sticker_menu
        else -> null
    }

    // Запускаем бесконечную анимацию свечения
    val transition = rememberInfiniteTransition()
    val alpha by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        )
    )

    res?.let {
        Image(
            painter = painterResource(it),
            contentDescription = null,
            modifier = Modifier
                .size(16.dp)
                .alpha(alpha), // ⬅️ Плавное изменение прозрачности
            colorFilter = ColorFilter.tint(Color(0xFFCAB7A3))
        )
    }
}
