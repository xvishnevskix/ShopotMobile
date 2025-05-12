package org.videotrade.shopot.parkingProj.presentation.components.Common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomTextInputRow(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    isFirst: Boolean = false,
    isLast: Boolean = false,
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .then(borderModifier)
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CustomText(
                text = label,
                fontStyle = FontStyleType.Regular,
                fontSize = 16.sp,
                type = TextType.PRIMARY
            )
            CustomTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = placeholder,
                textAlign = TextAlign.Center
            )

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
