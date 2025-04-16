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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.menu_delete

@Composable
fun SwipeToDeleteContainer(
    modifier: Modifier = Modifier,
    threshold: Float = 200f,
    onSwipeDelete: () -> Unit,
    isVisible: Boolean = true,
    content: @Composable () -> Unit,

) {
    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val deleteVisible = remember { mutableStateOf(false) }

    if (isVisible) {
        Box(
            modifier = modifier
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            scope.launch {
                                if (offsetX.value <= -threshold + 5f) {
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
                            .border(width = 1.dp, color = Color(0x33373533))
                            .alpha(alpha)
                    )
                    Spacer(modifier = Modifier.width((30 * alpha).dp))

                    Image(
                        painter = painterResource(Res.drawable.menu_delete),
                        contentDescription = "Delete",
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .size(24.dp)
                            .clickable {
                                onSwipeDelete()
                                scope.launch { offsetX.animateTo(0f) }
                            },
                        alpha = alpha
                    )
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