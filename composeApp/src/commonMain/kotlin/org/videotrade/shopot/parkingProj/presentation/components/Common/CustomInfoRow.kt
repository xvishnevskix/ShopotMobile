package org.videotrade.shopot.parkingProj.presentation.components.Common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.arrow_left
import shopot.composeapp.generated.resources.check_mark

@Composable
fun CustomInfoRow(
    leftText: String,
    rightText: String? = null,
    isFirst: Boolean = false,
    isLast: Boolean = false,
    type: TextType = TextType.PRIMARY,
    rightTextType: TextType = TextType.SECONDARY,
    showArrow: Boolean = true,
    showCheckmark: Boolean = false,
    onClick: () -> Unit,
    maxLines: Int = 1,
) {
    fun Modifier.drawTopBorder(width: Dp, color: Color): Modifier = this.then(
        Modifier.drawBehind {
            drawLine(
                color = color,
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = width.toPx()
            )
        }
    )

    fun Modifier.drawBottomBorder(width: Dp, color: Color): Modifier = this.then(
        Modifier.drawBehind {
            drawLine(
                color = color,
                start = Offset(0f, size.height),
                end = Offset(size.width, size.height),
                strokeWidth = width.toPx()
            )
        }
    )

    val borderModifier = when {
        isFirst && isLast -> Modifier
            .drawTopBorder(1.dp, Color(0xFFE2E2E2))
            .drawBottomBorder(1.dp, Color(0xFFE2E2E2))

        isFirst -> Modifier.drawTopBorder(1.dp, Color(0xFFE2E2E2))
        isLast -> Modifier.drawBottomBorder(1.dp, Color(0xFFE2E2E2))
        else -> Modifier
    }

    val height = if (maxLines == 1) 48.dp else 22.dp * maxLines + 16.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .then(borderModifier)
            .clickable { onClick() }
            .background(Color.White),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CustomText(
                text = leftText,
                type = type,
                fontStyle = FontStyleType.Regular,
                fontSize = 16.sp,
                maxLines = maxLines
            )

            when {
                showCheckmark -> {
                    Image(
                        painter = painterResource(Res.drawable.check_mark),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(Color(0xFF007AFF)),
                        modifier = Modifier.size(18.dp)
                    )
                }

                rightText != null -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CustomText(
                            text = rightText,
                            type = rightTextType,
                            fontStyle = FontStyleType.Regular
                        )
                        if (showArrow) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Image(
                                painter = painterResource(Res.drawable.arrow_left),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                colorFilter = ColorFilter.tint(Color(0xFFC7C7CC)),
                                modifier = Modifier.rotate(180F).size(7.dp, 14.dp)
                            )
                        }
                    }
                }

                else -> {
                    Image(
                        painter = painterResource(Res.drawable.arrow_left),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        colorFilter = ColorFilter.tint(Color(0xFFC7C7CC)),
                        modifier = Modifier.rotate(180F).size(7.dp, 14.dp)
                    )
                }
            }
        }

        if (!isLast) {
            Divider(
                modifier = Modifier.padding(start = 16.dp),
                thickness = 1.dp,
                color = Color(0xFFE2E2E2)
            )
        }
    }
}


