package org.videotrade.shopot.presentation.components.Common

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular

@Composable
fun TextSection(title: String, content: List<String>) {
    val colors = MaterialTheme.colorScheme

    Text(
        text = title,
        style = TextStyle(
            fontSize = 18.sp,
            color = colors.primary,
            fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular))
        ),
        modifier = Modifier.padding(vertical = 8.dp)
    )

    content.forEach {
        Text(
            text = it,
            style = TextStyle(
                fontSize = 15.sp,
                color = colors.primary,
                fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
            ),
            modifier = Modifier
                .padding(vertical = 4.dp, horizontal = 16.dp)
        )
    }
}