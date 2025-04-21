package org.videotrade.shopot.presentation.components.Common

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.MokoRes
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.edit_pencil
import shopot.composeapp.generated.resources.menu_delete
import shopot.composeapp.generated.resources.user_settings

@Composable
fun SwipeContainer(
    modifier: Modifier = Modifier,
    threshold: Float = 200f,
    onSwipeDelete: () -> Unit,
    isVisible: Boolean = true,
    isGroup: Boolean = false,
    onSwipeProgress: (Float) -> Unit = {},
    onRoleClick: (Offset) -> Unit = {},
    content: @Composable () -> Unit,


) {
    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val deleteVisible = remember { mutableStateOf(false) }
    val colors = MaterialTheme.colorScheme





    if (isVisible) {
        Box(
            modifier = modifier
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            scope.launch {
                                if (offsetX.value <= -threshold + 5f && !isGroup) {
                                    onSwipeDelete()
                                    offsetX.animateTo(0f)
                                } else if (offsetX.value < -threshold / 2) {
                                    offsetX.animateTo(-threshold)
                                } else {
                                    offsetX.animateTo(0f)
                                }
                            }
                        },
                        onHorizontalDrag = { _, delta ->
                            scope.launch {
                                val newOffset = (offsetX.value + delta).coerceIn(-threshold, 0f)
                                offsetX.snapTo(newOffset)
                                onSwipeProgress((-newOffset / threshold).coerceIn(0f, 1f))
                            }
                        }
                    )
                }
        ) {
            val alpha = (-offsetX.value / threshold).coerceIn(0f, 1f)
            if (alpha > 0f) {
                Row(
                    modifier = Modifier
                        .width(75.dp)
                        .matchParentSize()
                        .zIndex(-1f)
                        .background(Color.Transparent),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(56.dp)
                            .border(width = 1.dp, color = colors.secondary)
                            .alpha(alpha)
                    )
                    if (!isGroup) {
                        Spacer(modifier = Modifier.width((30 * alpha).dp))
                    }

                    if (isGroup) {
                        var buttonOffset = remember { mutableStateOf(Offset.Zero) }

                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .padding(12.dp)
                                .onGloballyPositioned {
                                    val coordinates = it.localToWindow(Offset.Zero)
                                    buttonOffset.value = coordinates
                                }
                                .clickable {
                                    onRoleClick(buttonOffset.value)
                                    scope.launch { offsetX.animateTo(0f) }
                                }
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.user_settings),
                                contentDescription = "Role",
                                modifier = Modifier.size(24.dp),
                                alpha = alpha,
                                colorFilter = ColorFilter.tint(colors.primary)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .padding(12.dp)
                            .clickable {
                                onSwipeDelete()
                                scope.launch { offsetX.animateTo(0f) }
                            }
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.menu_delete),
                            contentDescription = "Delete",
                            modifier = Modifier
                                .size(24.dp)
                               ,
                            alpha = alpha
                        )
                    }

                }
            }

                Box(
                    modifier = Modifier.offset { IntOffset(offsetX.value.toInt(), 0) }
                        .zIndex(1f)
                ) {
                    content()
                }
        }
    } else {
        content()
    }

}


