package org.videotrade.shopot.presentation.components.Call

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
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
import shopot.composeapp.generated.resources.acceptCall
import shopot.composeapp.generated.resources.call_microphone
import shopot.composeapp.generated.resources.call_speaker
import shopot.composeapp.generated.resources.call_video
import shopot.composeapp.generated.resources.microfon
import shopot.composeapp.generated.resources.rejectCall
import shopot.composeapp.generated.resources.svgviewer_png_output

@Composable
fun rejectBtn(onClick: () -> Unit, text: String= "Отменить") {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onClick,
            enabled = true,
            modifier = Modifier.size(100.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(255, 255, 255))
        ) {
            Image(
                painter = painterResource(Res.drawable.rejectCall),
                alignment = Alignment.Center,
                contentDescription = "Reject Call",
                modifier = Modifier.size(44.dp)
            )
        }
        Text(
            modifier = Modifier.padding(top = 2.dp),
            text = text,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily(Font(Res.font.Montserrat_Regular)),
            letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
            lineHeight = 20.sp,
            color = Color(255, 255, 255)
        )
    }
}

@Composable
fun aceptBtn(onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onClick,
            enabled = true,
            modifier = Modifier.size(80.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(41, 48, 60))
        ) {
            Image(
                painter = painterResource(Res.drawable.acceptCall),
                alignment = Alignment.Center,
                contentDescription = "Accept Call",
                modifier = Modifier.size(44.dp)
            )
        }
        Text(
            modifier = Modifier.padding(top = 2.dp).align(Alignment.CenterHorizontally),
            text = "Принять",
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily(Font(Res.font.Montserrat_Regular)),
            letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
            lineHeight = 20.sp,
            color = Color(255, 255, 255)
        )
    }
}

@Composable
fun speakerBtn(onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(Res.drawable.call_speaker),
            contentDescription = "Video",
            modifier = Modifier.size(80.dp).clip(CircleShape).clickable{
                onClick()
            }
        )
        Text(
            modifier = Modifier.padding(top = 2.dp).align(Alignment.CenterHorizontally),
            text = stringResource(MokoRes.strings.speaker),
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily(Font(Res.font.Montserrat_Regular)),
            letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
            lineHeight = 20.sp,
            color = Color(255, 255, 255)
        )
    }
}

@Composable
fun microfonBtn(onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(Res.drawable.call_microphone),
            contentDescription = "Video",
            modifier = Modifier.size(80.dp).clip(CircleShape).clickable{
                onClick()
            }
        )
        Text(
            modifier = Modifier.padding(top = 2.dp).align(Alignment.CenterHorizontally),
            text = stringResource(MokoRes.strings.microphone),
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily(Font(Res.font.Montserrat_Regular)),
            letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
            lineHeight = 20.sp,
            color = Color(255, 255, 255)
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

