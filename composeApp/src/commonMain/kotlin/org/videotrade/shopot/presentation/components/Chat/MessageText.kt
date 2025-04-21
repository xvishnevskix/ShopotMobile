package org.videotrade.shopot.presentation.components.Chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.api.formatTimeOnly
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.model.ProfileDTO
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.message_double_check
import shopot.composeapp.generated.resources.message_single_check


@Composable
fun MessageText(
    message: MessageItem,
    profile: ProfileDTO,
    chat: ChatItem? = null
) {
    val colors = MaterialTheme.colorScheme
    val isMainUser = (message.fromUser == profile.id)
    
    message.content?.let {
        Box(
            modifier = Modifier

        ) {
            Text(
                text = it,
                style = TextStyle(
                    color =
//                    if (isMainUser) Color.White else colors.primary
                    colors.primary
                    ,
                    fontSize = 14.sp,
                    lineHeight = 14.sp,
                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                    fontWeight = FontWeight(400),
                    letterSpacing = TextUnit(0F, TextUnitType.Sp),
                ),
                modifier = Modifier
                    .padding(
                        end = if (isMainUser && !message.anotherRead) 60.dp else if (!isMainUser) 45.dp else if (message.anotherRead && isMainUser) 70.dp else 60.dp, // отступ справа, чтобы освободить место под статус
                        start = 12.dp,
                        top = 12.dp,
                        bottom = 12.dp
                    )
            )
            //статус
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 6.dp, bottom = 8.dp, )
            ) {
                if (message.created.isNotEmpty()) {
                    Text(
                        text = formatTimeOnly(message.created),
                        style = TextStyle(
                            fontSize = 12.sp,
                            lineHeight = 12.sp,
                            fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                            fontWeight = FontWeight(400),
                            color =
//                            if (isMainUser) Color.White else colors.onSecondary
                            colors.onSecondary
                            ,
                            letterSpacing = TextUnit(0F, TextUnitType.Sp),
                        )
                    )
                }

                if (isMainUser) {
                    if (message.anotherRead) {
                        Image(
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .size(width = 17.7.dp, height = 8.dp),
                            painter = painterResource(Res.drawable.message_double_check),
                            contentDescription = null,
//                    colorFilter = ColorFilter.tint(Color.White)
                        )
                    } else {
                        Image(
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .size(width = 12.7.dp, height = 8.dp),
                            painter = painterResource(Res.drawable.message_single_check),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(colors.secondary)
                        )
                    }
                }
            }
        }
    }
}
