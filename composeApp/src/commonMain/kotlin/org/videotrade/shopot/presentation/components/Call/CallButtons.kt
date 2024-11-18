package org.videotrade.shopot.presentation.components.Call

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.MokoRes
import shopot.composeapp.generated.resources.Montserrat_Regular
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.call_micro
import shopot.composeapp.generated.resources.call_microphone_disabled
import shopot.composeapp.generated.resources.call_microphone_on
import shopot.composeapp.generated.resources.call_speaker
import shopot.composeapp.generated.resources.call_speaker_on
import shopot.composeapp.generated.resources.call_video
import shopot.composeapp.generated.resources.cancel
import shopot.composeapp.generated.resources.chat_call

@Composable
fun rejectBtn(onClick: () -> Unit, size: Dp = 56.dp) {

        Button(
            onClick = onClick,
            enabled = true,
            modifier = Modifier
                .size(size)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFFF3B30)),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3B30))
        ) {
            Image(
                painter = painterResource(Res.drawable.cancel),
                alignment = Alignment.Center,
                contentDescription = "Reject",
                modifier = Modifier.size( if (size == 72.dp) 14.dp else 28.dp),

            )
        }
}

@Composable
fun aceptBtn(onClick: () -> Unit, size: Dp = 56.dp,) {

        Button(
            onClick = onClick,
            enabled = true,
            modifier = Modifier
                .size(size)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFCAB7A3)),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCAB7A3))
        ) {
            Image(
                painter = painterResource(Res.drawable.chat_call),
                alignment = Alignment.Center,
                contentDescription = "Accept Call",
                modifier = Modifier.size(18.dp),
                colorFilter = ColorFilter.tint(Color.White)
            )
        }

}

@Composable
fun speakerBtn(isActive: Boolean ,onClick: () -> Unit) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(if (!isActive) 0xFFCAB7A3 else 0xFFF7F7F7))
                .clickable{
                    onClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(Res.drawable.call_speaker),
                contentDescription = "Speaker",
                modifier = Modifier
                    .size(width = 20.dp, height = 14.5.dp)
                    ,
                colorFilter =
                if (!isActive) ColorFilter.tint(Color.White)
                else ColorFilter.tint(Color(0xFF373533))
            )
        }
}

@Composable
fun microfonBtn(isActive: Boolean ,onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(if (!isActive) 0xFFCAB7A3 else 0xFFF7F7F7))
            .clickable{
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
            Image(
                painter = painterResource(Res.drawable.call_micro),
                contentDescription = "Micro",
                modifier = Modifier
                    .size(width = 14.dp, height = 19.dp)
                    ,
                colorFilter =
                if (!isActive) ColorFilter.tint(Color.White)
                else ColorFilter.tint(Color(0xFF373533))
            )
        }
}

@Composable
fun videoBtn(onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
//        Button(
//            onClick = onClick,
//            enabled = true,
//            modifier = Modifier.size(180.dp),
//            colors = ButtonDefaults.buttonColors(containerColor = Color(41, 48, 60))
//        ) {
            Image(
                painter = painterResource(Res.drawable.call_video),
                contentDescription = "Video",
                modifier = Modifier.size(80.dp).clip(CircleShape).clickable{
                    onClick()
                }
            )
//        }
        Text(
            modifier = Modifier.padding(top = 2.dp).align(Alignment.CenterHorizontally),
            text = stringResource(MokoRes.strings.video_call),
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily(Font(Res.font.Montserrat_Regular)),
            letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
            lineHeight = 20.sp,
            color = Color(255, 255, 255)
        )
    }
}

