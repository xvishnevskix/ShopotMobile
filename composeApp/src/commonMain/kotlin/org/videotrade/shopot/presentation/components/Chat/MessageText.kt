package org.videotrade.shopot.presentation.components.Chat

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.model.ProfileDTO
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular


@Composable
fun MessageText(message: MessageItem, profile: ProfileDTO) {
    message.content?.let {
      Text(
        text = it,
        style = TextStyle(
            color = if (message.fromUser == profile.id) Color.White else Color(0xFF2A293C),
            fontSize = 16.sp,
            fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
        ),
        modifier = Modifier.padding(
            start = 25.dp,
            end = 25.dp,
//            top = if (message.fromUser == profile.id) 13.dp else 7.dp,
            top = 12.dp,
            bottom = 12.dp
        ),
    )
    }
}