package org.videotrade.shopot.presentation.components.Chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.presentation.screens.settings.getThemeMode
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Res

@Composable
fun SelectedCallMessage(
    selectedMessage: MessageItem,
    selectedMessageSenderName: String,
    colorTitle: Color = Color.Black,
    isFromUser: Boolean,

    ) {
    val colors = MaterialTheme.colorScheme
    val theme = getThemeMode()

    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = selectedMessageSenderName,
            style = TextStyle(
                fontSize = 16.sp,
                lineHeight = 16.sp,
                fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                fontWeight = FontWeight(500),
                color = colorTitle,
                letterSpacing = TextUnit(0F, TextUnitType.Sp),
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        selectedMessage.callInfo?.get(0)?.let {
            Text(
                text = it.status,
                maxLines = 1, // Ограничение до одной строки
                overflow = TextOverflow.Ellipsis, // Добавление многоточия в конце
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                    fontWeight = FontWeight(400),
                    color = if (isFromUser)
                        colors.onTertiary
                    else
                        colors.secondary,
                    letterSpacing = TextUnit(0F, TextUnitType.Sp),
                )
            )
        }
    }
}