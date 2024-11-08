package org.videotrade.shopot.presentation.components.Auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.Montserrat_Regular
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.arrow_left

@Composable
fun BaseHeader(text: String, background: Color = Color(0xFFf9f9f9)) {
    val navigator = LocalNavigator.currentOrThrow


    Row(
        modifier = Modifier.background(background).fillMaxWidth().padding(top = 30.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {

        Box(modifier = Modifier.clickable {
            navigator.pop()
        }.padding(start = 8.dp, end = 8.dp)) {
            Image(
                modifier = Modifier
                    .size(width = 7.dp, height = 14.dp),
                painter = painterResource(Res.drawable.arrow_left),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
        Text(
            text = text,
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            lineHeight = 16.sp,
            fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
            fontWeight = FontWeight(500),
            color = Color(0xFF373533),
            letterSpacing = TextUnit(0F, TextUnitType.Sp),
        )

        Spacer(modifier = Modifier.width(20.dp))
    }
}

