package org.videotrade.shopot.presentation.components.Chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import getImageStorage
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.api.EnvironmentConfig.SERVER_URL
import org.videotrade.shopot.api.formatTimeOnly
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.model.ProfileDTO
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.message_double_check
import shopot.composeapp.generated.resources.message_single_check


@Composable
fun StickerMessage(
    message: MessageItem,
    imageId: String,
    chat: ChatItem,
    profile: ProfileDTO
) {
    val isMainUser = message.fromUser == profile.id
    val imagePainter = rememberAsyncImagePainter("${SERVER_URL}file/plain/$imageId")
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .size(150.dp)
            .padding(7.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {

            }
    ) {
        Image(
            painter = imagePainter,
            contentDescription = "Sticker",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )

        // Статус
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    end = 4.dp,
                    bottom = 4.dp,
                )
                .background(
                    color = Color(0x80000000),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 6.dp, vertical = 3.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                if (message.created.isNotEmpty()) {
                    Text(
                        text = formatTimeOnly(message.created),
                        style = TextStyle(
                            fontSize = 12.sp,
                            lineHeight = 12.sp,
                            fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                            fontWeight = FontWeight(400),
                            color = Color.White,
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
                            colorFilter = ColorFilter.tint(Color.White)                        )
                    }
                }
            }
        }
    }
}
