package org.videotrade.shopot.presentation.components.Common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import org.jetbrains.compose.resources.Font
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res

enum class ButtonStyle {
    Primary,
    Outline,
    Gradient
}

@Composable
fun CustomButton(
    text: String,
    onClick: (CoroutineScope) -> Unit,
    width: Dp = 262.dp,
    height: Dp = 56.dp,
    style: ButtonStyle = ButtonStyle.Primary,
    disabled: Boolean = false,
) {
    val scope = rememberCoroutineScope()

    val backgroundColor: Color
    val textColor: Color
    val buttonShape: Shape = RoundedCornerShape(16.dp)

    // Determine styles based on ButtonStyle
    when (style) {
        ButtonStyle.Primary -> {
            backgroundColor = Color(0xFF373533)
            textColor = Color.White
        }
        ButtonStyle.Outline -> {
            backgroundColor = Color.Transparent
            textColor = Color(0xFF373533)
        }
        ButtonStyle.Gradient -> {
            backgroundColor = Color.Transparent // Gradient will be applied in Modifier
            textColor = Color(0xFF373533)
        }
    }

    Button(
        onClick = { onClick(scope) },
        colors = ButtonDefaults.buttonColors(backgroundColor),
        shape = buttonShape,
        modifier = Modifier
            .width(width) // set the width
            .height(height)
            .let {
                if (style == ButtonStyle.Outline) {
                    it.border(
                        width = 1.dp,
                        color = Color(0xFF373533),
                        shape = buttonShape
                    )
                } else {
                    it
                }
            }
            .background(
                brush = when (style) {
                    ButtonStyle.Gradient -> Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFCAB7A3), // rgb(202, 183, 163)
                            Color(0xFFEDDCCC), // rgb(237, 220, 204)
                            Color(0xFFBBA796)  // rgb(187, 167, 150)
                        )
                    )
                    else -> Brush.verticalGradient(listOf(backgroundColor, backgroundColor))
                },
                shape = buttonShape
            )
    ) {
        Text(
            text = text,
            fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            lineHeight = 16.sp,
            color = if (disabled) Color(0x80373533) else textColor,
        )
    }
}


