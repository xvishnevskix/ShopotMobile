package videotrade.parkingProj.presentation.components.Common.Common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role.Companion.Switch
import androidx.compose.ui.unit.dp

@Composable
fun CustomSwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    isFirst: Boolean = false,
    isLast: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val topBorderModifier = if (isFirst) Modifier
        .drawBehind {
            drawLine(
                color = Color(0xFFE2E2E2),
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = 1.dp.toPx()
            )
        } else Modifier

    val bottomBorderModifier = if (isLast) Modifier
        .drawBehind {
            drawLine(
                color = Color(0xFFE2E2E2),
                start = Offset(0f, size.height),
                end = Offset(size.width, size.height),
                strokeWidth = 1.dp.toPx()
            )
        } else Modifier

    Column(
        modifier = modifier
            .height(48.dp)
            .fillMaxWidth()
            .background(Color.White)
            .then(topBorderModifier)
            .then(bottomBorderModifier)
            .clickable { onCheckedChange(!checked) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp,)

            ,
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CustomText(
                text = title,
                type = TextType.PRIMARY,
                fontStyle = FontStyleType.Regular
            )

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF65AF4C),
                    uncheckedThumbColor = Color.White
                )
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
