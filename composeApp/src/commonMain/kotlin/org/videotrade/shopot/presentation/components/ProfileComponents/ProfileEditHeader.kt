package org.videotrade.shopot.presentation.components.ProfileComponents


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.presentation.components.Call.CallBar
import org.videotrade.shopot.presentation.components.Common.BackIcon
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.arrow_left
import shopot.composeapp.generated.resources.profile_accept

@Composable
fun ProfileEditHeader(text: String, onClick: (() -> Unit)? = null) {
    val navigator = LocalNavigator.currentOrThrow
    val colors = MaterialTheme.colorScheme

    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp).background(colors.surface),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,

            ) {

            Box(modifier = Modifier.clip(CircleShape).clickable {
                navigator.pop()
            }.padding(12.dp)) {
                BackIcon()
            }

            Text(
                text = text,
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                lineHeight = 16.sp,
                fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                fontWeight = FontWeight(500),
                color = colors.primary,
                letterSpacing = TextUnit(0F, TextUnitType.Sp),

                )
            Image(
                painter = painterResource(Res.drawable.profile_accept),
                contentDescription = "Checkmark",
                modifier = Modifier.padding(start = 0.dp, end = 6.dp)
                    .size(width = 15.56.dp, height = 10.61.dp).pointerInput(Unit) {
                        if (onClick != null) {
                            onClick()
                        }
                    },
                contentScale = ContentScale.FillBounds,
                colorFilter =  ColorFilter.tint(colors.primary)
            )
        }
        Box(Modifier.padding(bottom = 23.dp)) {
            CallBar()
        }
    }

}


