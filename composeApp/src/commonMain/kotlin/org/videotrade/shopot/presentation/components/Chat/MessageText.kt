package org.videotrade.shopot.presentation.components.Chat

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.model.ProfileDTO
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular


@Composable
fun MessageText(message: MessageItem, profile: ProfileDTO, chat: ChatItem? = null,) {
    val colors = MaterialTheme.colorScheme
    message.content?.let {
      Text(
        text = it,
          overflow = TextOverflow.Ellipsis,
        style = TextStyle(
            color = if (message.fromUser == profile.id) Color.White else colors.primary,
            fontSize = 16.sp,
            lineHeight = 16.sp,
            fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
            fontWeight = FontWeight(400),
            letterSpacing = TextUnit(0F, TextUnitType.Sp),

        ),
          modifier = Modifier.padding(
              start = 16.dp,
              end = 16.dp,
              top = 16.dp,
//            top = if (message.fromUser == profile.id) 13.dp else 7.dp,
//              top = if (chat?.personal == true && message.forwardMessage == false) 12.dp else 6.dp,
              bottom = 18.dp
          ),
    )
    }
}