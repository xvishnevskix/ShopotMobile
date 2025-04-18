package org.videotrade.shopot.presentation.components.Auth

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Res

@Composable
fun Otp(
    otpFields: SnapshotStateList<String>,
    isLoading: Boolean = false,
    hasError: Boolean,
    animationTrigger: Boolean,
    onOtpComplete: (String) -> Unit
) {


    val offsetX = remember { Animatable(0f) }
    val colors = MaterialTheme.colorScheme
    // Цвет бордера: красный, если ошибка, иначе серый
    val borderColor by animateColorAsState(
        targetValue = if (hasError) colors.error else colors.onSecondary,
        animationSpec = tween(durationMillis = 300)
    )

    // Запускаем анимацию тряски, если есть ошибка
    LaunchedEffect(animationTrigger) {
        if (hasError) {
            offsetX.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 500
                    // Двигаем текстовое поле влево и вправо
                    -5f at 0
                    5f at 100
                    -5f at 200
                    5f at 300
                    0f at 400
                }
            )
        }
    }

    // Объединяем введенные данные в одну строку для проверки
    val otpText = otpFields.joinToString("")

    // Проверка, заполнены ли все поля
    LaunchedEffect(otpText) {
        if (otpText.length == 4) {
            onOtpComplete(otpText)
        // Вызываем функцию обратного вызова
        }
    }

    if (isLoading) {
        LoadingDots()
    } else {
        val focusRequesters = List(4) { FocusRequester() }
        val localFocusManager = LocalFocusManager.current

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp).fillMaxWidth(),


        ) {
            otpFields.forEachIndexed { index, _ ->
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .offset(x = offsetX.value.dp)
                        .size(width = 60.dp, height = 56.dp) // Размер для каждого квадрата
                        .clip(RoundedCornerShape(8.dp))
                        .background(colors.background)
                        .border(1.dp, color = borderColor, RoundedCornerShape(16.dp)), // Добавляем границу
                    contentAlignment = Alignment.Center
                ) {
                    BasicTextField(
                        cursorBrush = SolidColor(colors.primary),
                        value = otpFields[index],
                        onValueChange = { input ->
                            val filteredInput = input.filter { it.isDigit() }
                            if (filteredInput.length <= 1) {
                                otpFields[index] = filteredInput
                                if (filteredInput.isNotEmpty() && index < focusRequesters.size - 1) {
                                    focusRequesters[index + 1].requestFocus()
                                }
                            }
                        },
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 24.sp,
                            lineHeight = 24.sp,
                            fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),

                            textAlign = TextAlign.Center,
                            color = colors.primary
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.NumberPassword,
                            imeAction = ImeAction.None
                        ),
                        modifier = Modifier
                            .padding(start = 16.dp,top = 16.dp,end = 16.dp,bottom = 16.dp)
                            .focusRequester(focusRequesters[index])
                            .fillMaxSize()
                            .onKeyEvent { event ->
                                if (event.key == Key.Backspace && otpFields[index].isEmpty()) {
                                    if (index > 0) {
                                        focusRequesters[index - 1].requestFocus()
                                    }
                                    true
                                } else {
                                    false
                                }
                            },
                        visualTransformation = VisualTransformation.None
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
            }
        }

        DisposableEffect(Unit) {
            focusRequesters[0].requestFocus()
            onDispose { }
        }
    }
}

@Composable
fun LoadingDots() {
    val colors = MaterialTheme.colorScheme
    val infiniteTransition = rememberInfiniteTransition()
    val animations = (0..3).map { index ->
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 15f,
            animationSpec = infiniteRepeatable(
                animation = tween(300, easing = LinearEasing, delayMillis = index * 150),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(16.dp).fillMaxWidth(),

        verticalAlignment = Alignment.CenterVertically
    ) {
        animations.forEach { animatedValue ->
            val offsetY by animatedValue
            Spacer(modifier = Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .size(width = 60.dp, height = 56.dp) // Размер для каждого квадрата
                    .clip(RoundedCornerShape(8.dp))
                    .background(colors.background)
                    .border(1.dp, Color(0xFFDDDDDD), RoundedCornerShape(16.dp)), // Добавляем границу
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(10.dp)) {
                    drawCircle(
                        color = colors.secondary,
                        radius = size.minDimension / 2,
                        center = center.copy(y = center.y - offsetY)
                    )
                }
            }
            Spacer(modifier = Modifier.width(4.dp))
        }
    }
}
