package videotrade.parkingProj.presentation.components.Common
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import parkingproj.composeapp.generated.resources.Res
import parkingproj.composeapp.generated.resources.SFProText_Regular
import videotrade.parkingProj.presentation.components.Common.Common.CustomText
import videotrade.parkingProj.presentation.components.Common.Common.FontStyleType
import videotrade.parkingProj.presentation.components.Common.Common.TextType

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
    fontSize: TextUnit = 16.sp,
    singleLine: Boolean = true,
    placeholderFontSize: TextUnit = 13.sp,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = singleLine,
            cursorBrush = SolidColor(Color(0xFF2E8BB7)),
            textStyle = LocalTextStyle.current.copy(
                fontSize = fontSize,
                fontFamily = FontFamily(Font(Res.font.SFProText_Regular)),
                color = Color.Black,
                textAlign = textAlign
            ),
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    CustomText(
                        text = placeholder,
                        type = TextType.SECONDARY,
                        fontStyle = FontStyleType.Regular,
                        modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                        textAlign = textAlign,
                        fontSize = placeholderFontSize
                    )
                }
                innerTextField()
            }
        )
    }
}

