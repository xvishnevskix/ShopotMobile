package videotrade.parkingProj.presentation.components.Common.Common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import parkingproj.composeapp.generated.resources.Res
import parkingproj.composeapp.generated.resources.SFProText_Bold
import parkingproj.composeapp.generated.resources.SFProText_Medium
import parkingproj.composeapp.generated.resources.SFProText_Regular

@Composable
fun CustomText(
    text: String,
    type: TextType = TextType.PRIMARY,
    isActive: Boolean = true,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = when (type) {
        TextType.SECONDARY -> 14.sp
        else -> 16.sp
    },
    fontStyle: FontStyleType = FontStyleType.Regular,
    textAlign: TextAlign = TextAlign.Start,
    maxLines: Int = 1000,
    isUppercase: Boolean = false,
) {
    val color = when {
        !isActive -> MaterialTheme.colorScheme.primary
        type == TextType.PRIMARY -> Color.Black
        type == TextType.SECONDARY -> Color(0xFF8A8A8F)
        type == TextType.BLUE -> Color(0xFF007AFF)
        type == TextType.RED -> Color(0xFFFF3B30)
        type == TextType.WHITE -> Color.White
        type == TextType.SECONDARY_DARK -> Color(0xFF5C5C5C)
        else -> Color.Unspecified
    }

    val fontFamily = FontFamily(
        Font(
            when (fontStyle) {
                FontStyleType.Regular -> Res.font.SFProText_Regular
                FontStyleType.Medium -> Res.font.SFProText_Medium
                FontStyleType.Bold -> Res.font.SFProText_Bold
            }
        )
    )

    Text(
        text = if (isUppercase) text.uppercase() else text,
        fontSize = fontSize,
        lineHeight = 16.sp,
        fontFamily = fontFamily,
        letterSpacing = (-0.08).sp,
        color = color,
        modifier = modifier,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
    )
}

enum class TextType {
    PRIMARY, SECONDARY, BLUE, RED, WHITE, SECONDARY_DARK
}

enum class FontStyleType {
    Regular, Medium, Bold
}
