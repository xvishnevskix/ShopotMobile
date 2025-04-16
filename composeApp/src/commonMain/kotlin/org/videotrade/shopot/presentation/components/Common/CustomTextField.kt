package org.videotrade.shopot.presentation.components.Common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Res

@Composable
fun CustomTextField(
    title: String = "",
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    error: String? = null,
    border: String = "gray",
    subTitle: String = ""
) {

    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier.padding(),
    ) {
        Row(
            modifier = Modifier.padding(top = 5.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom) {
            if (title != "") {
                Text(
                    title,
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                    fontWeight = FontWeight(500),
                    textAlign = TextAlign.Center,
                    color = colors.primary,
                    letterSpacing = TextUnit(0F, TextUnitType.Sp),
                    )
            }
            Spacer(modifier = Modifier.width(4.dp))
            if (subTitle != "") {
                Text(
                    "(${subTitle})",
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                    fontWeight = FontWeight(500),
                    textAlign = TextAlign.Center,
                    color = colors.secondary,
                    letterSpacing = TextUnit(0F, TextUnitType.Sp),
                    modifier = Modifier.padding(bottom = 1.dp)
                    )
            }
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .background(colors.background, shape = RoundedCornerShape(size = 16.dp))
                        .border(width = 1.dp, color = if (border == "gray") colors.secondaryContainer else colors.primary, shape = RoundedCornerShape(size = 16.dp))
                        .padding(
                            horizontal = 16.dp,
                            vertical = 16.dp
                        ),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 16.sp,
                                fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                fontWeight = FontWeight(400),
                                textAlign = TextAlign.Start,
                                letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                color = colors.secondary
                            )
                        )
                    }
                    innerTextField()
                }
            },
            textStyle = TextStyle(
                fontSize = 16.sp,
                lineHeight = 16.sp,
                fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                fontWeight = FontWeight(400),
                textAlign = TextAlign.Start,
                letterSpacing = TextUnit(0F, TextUnitType.Sp),
                color = colors.primary
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier
                .fillMaxWidth(1f),
            cursorBrush = SolidColor(colors.primary)
        )


        error?.let {
            Text(
                text = it,
                color = colors.error,
                fontSize = 12.sp,
                lineHeight = 12.sp,
                fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                fontWeight = FontWeight(400),
                textAlign = TextAlign.Center,
                letterSpacing = TextUnit(0F, TextUnitType.Sp),
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }

    }
}
