package org.videotrade.shopot.presentation.components.Chat

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.model.ProfileDTO


@Composable
fun MessageText(message: MessageItem, profile: ProfileDTO) {
    message.content?.let {
      Text(
        text = it,
        style = TextStyle(
            color = if (message.fromUser == profile.id) Color.White else Color(0xFF2A293C),
            fontSize = 16.sp
        ),
        modifier = Modifier.padding(
            start = 25.dp,
            end = 25.dp,
            top = 13.dp,
            bottom = 12.dp
        ),
    )
    }
}