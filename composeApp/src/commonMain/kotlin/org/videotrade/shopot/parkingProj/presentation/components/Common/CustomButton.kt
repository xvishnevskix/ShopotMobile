package videotrade.parkingProj.presentation.components.Common.Common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope


enum class ButtonStyle {
    Primary,
    Link
}

@Composable
fun CustomButton(
    text: String,
    onClick: (CoroutineScope) -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = Dp.Unspecified,
    height: Dp = 48.dp,
    style: ButtonStyle = ButtonStyle.Primary,
    disabled: Boolean = false,
    isLoading: Boolean = false,
    textType: TextType = when (style) {
        ButtonStyle.Primary -> TextType.WHITE
        ButtonStyle.Link -> TextType.BLUE
    }
) {
    val scope = rememberCoroutineScope()
    val colors = MaterialTheme.colorScheme
    val buttonShape = RoundedCornerShape(12.dp)

    val background = when (style) {
        ButtonStyle.Primary -> Color(0xFF72A32F)
        ButtonStyle.Link -> Color.Transparent
    }

    val contentPadding = when (style) {
        ButtonStyle.Primary -> PaddingValues(horizontal = 24.dp, vertical = 12.dp)
        ButtonStyle.Link -> PaddingValues(0.dp)
    }

    val shadowModifier = if (style == ButtonStyle.Primary) {
        Modifier.shadow(elevation = 6.dp, shape = buttonShape)
    } else Modifier

    Box(
        modifier = modifier
            .then(shadowModifier)
            .fillMaxWidth()
            .then(
                if (width != Dp.Unspecified) Modifier.width(width) else Modifier
            )
            .height(height)
            .clip(buttonShape)
            .background(background)
            .clickable(enabled = !disabled && !isLoading) { onClick(scope) }
            .pointerHoverIcon(PointerIcon.Hand),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            CustomText(
                text = text,
                type = textType,
                modifier = Modifier.padding(contentPadding)
            )
        }
    }
}



