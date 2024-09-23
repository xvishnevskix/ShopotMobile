package org.videotrade.shopot.presentation.components.Chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import org.videotrade.shopot.api.formatDateOnly
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular

@Composable
fun DateHeader(date: List<Int>) {
    Box(
        modifier = Modifier
            .padding(vertical = 2.dp)
            .fillMaxWidth()
        ,// Применение анимации видимости
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(15.dp))
                .widthIn(min = 60.dp, max = 140.dp)
                .height(30.dp)
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(top = 3.dp, bottom = 3.dp, start = 8.dp, end = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = formatDateOnly(date),
                style = TextStyle(
                    color = Color.White,
                    fontSize = 13.sp,
                    fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                ),
                color = Color.White
            )
        }
    }
}