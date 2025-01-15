package org.videotrade.shopot.presentation.components.Chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.domain.model.CallInfo
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.chat_call

@Composable
fun CallMessage(callInfo: CallInfo, isFromUser: Boolean,) {
    val colors = MaterialTheme.colorScheme

        println("${callInfo} callInfocallInfo")

        Column(
            modifier = Modifier.padding(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 16.dp
            ),
        ) {
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = getCallStatusString(callInfo.status),
                        style = TextStyle(
                            color = if (isFromUser) Color(0xFFFFFFFF) else colors.primary,
                            fontSize = 16.sp,
                            lineHeight = 16.sp,
                            fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                            fontWeight = FontWeight(500),
                            letterSpacing = TextUnit(0F, TextUnitType.Sp),
                        ),
                        modifier = Modifier.padding(),
                    )


                    if (callInfo.callDuration != "00:00:00") {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formatCallDuration(callInfo.callDuration),
                            style = TextStyle(
                                color =  if (isFromUser) Color(0xFFF7F7F7) else colors.secondary,
                                fontSize = 16.sp,
                                lineHeight = 16.sp,
                                fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                fontWeight = FontWeight(400),
                                letterSpacing = TextUnit(0F, TextUnitType.Sp),
                            ),
                        )
                    }
                }

                Spacer(Modifier.width(43.dp))

                Image(
                    painter = painterResource(Res.drawable.chat_call),
                    contentDescription = null,
                    modifier = Modifier
                        .size(18.dp),
                    colorFilter = if (isFromUser) ColorFilter.tint(Color(0xFFFFFFFF))  else ColorFilter.tint(colors.primary)
                )
            }


        }

}


@Composable
fun formatCallDuration(duration: String): String {
    val parts = duration.split(":").map { it.toInt() }
    val hours = parts[0]
    val minutes = parts[1]
    val seconds = parts[2]

    @Composable
    fun getHoursText(hours: Int): String {
        return when {
            hours % 10 == 1 && hours % 100 != 11 -> stringResource(MokoRes.strings.hour_singular)
            hours % 10 in 2..4 && hours % 100 !in 12..14 -> stringResource(MokoRes.strings.hour_few)
            else -> stringResource(MokoRes.strings.hour_plural)
        }
    }

    @Composable
    fun getMinutesText(minutes: Int): String {
        return when {
            minutes % 10 == 1 && minutes % 100 != 11 -> stringResource(MokoRes.strings.minute_singular)
            minutes % 10 in 2..4 && minutes % 100 !in 12..14 -> stringResource(MokoRes.strings.minute_few)
            else -> stringResource(MokoRes.strings.minute_plural)
        }
    }

    @Composable
    fun getSecondsText(seconds: Int): String {
        return when {
            seconds % 10 == 1 && seconds % 100 != 11 -> stringResource(MokoRes.strings.second_singular)
            seconds % 10 in 2..4 && seconds % 100 !in 12..14 -> stringResource(MokoRes.strings.second_few)
            else -> stringResource(MokoRes.strings.second_plural)
        }
    }

    return when {
        hours > 0 -> "$hours ${getHoursText(hours)}"
        minutes > 0 -> "$minutes ${getMinutesText(minutes)}"
        else -> "$seconds ${getSecondsText(seconds)}"
    }
}

@Composable
fun getCallStatusString(callStatus: String): String {
    return when (callStatus) {
        "Отменённый" -> stringResource(MokoRes.strings.call_status_canceled)
        "Исходящий" -> stringResource(MokoRes.strings.call_status_outgoing)
        "Пропущенный" -> stringResource(MokoRes.strings.call_status_missed)
        "Входящий" -> stringResource(MokoRes.strings.call_status_incoming)
        else -> callStatus // Для непредвиденных значений
    }
}