package org.videotrade.shopot.presentation.components.ProfileComponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.check_mark

@Composable
fun GroupEditHeader(text: String, onClick: (() -> Unit)? = null) {
    val navigator = LocalNavigator.currentOrThrow

    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 40.dp, bottom = 23.dp).background(Color(0xFFF3F4F6)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,

        ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            modifier = Modifier.padding(start = 10.dp,end = 1.dp).clickable {
                navigator.pop()
            },
            tint = Color.Black
            
        )
        Text(
            text = text,
            fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
            fontSize = 17.sp,
            textAlign = TextAlign.Center,
            letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
            lineHeight = 20.sp,
            modifier = Modifier.padding(end = 10.dp),
            color = Color.Black
            
        )
        Image(
            painter = painterResource(Res.drawable.check_mark),
            contentDescription = "Avatar",
            modifier = Modifier.padding(start = 0.dp, end = 6.dp)
                .size(width = 16.dp, height = 12.dp).clickable {
                if (onClick != null) {
                    onClick()
                }
            },
            contentScale = ContentScale.FillBounds
        )
    }

}


