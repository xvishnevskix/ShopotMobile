package org.videotrade.shopot.presentation.components.ProfileComponents

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.videotrade.shopot.presentation.components.Common.BackIcon
import shopot.composeapp.generated.resources.Montserrat_Bold
import shopot.composeapp.generated.resources.Montserrat_Regular
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFProText_Regular

@Composable
fun ProfileHeader(text: String) {
    val navigator = LocalNavigator.currentOrThrow

    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 40.dp, bottom = 23.dp).background(Color(0xFFF3F4F6)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,

        ) {
        BackIcon(Modifier.padding(start = 10.dp, end = 0.dp).width(25.dp).pointerInput(Unit) {


            navigator.pop()


        })
        Text(
            text = text,
            fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
            fontSize = 16.sp,
            fontWeight = FontWeight.W600,
            textAlign = TextAlign.Center,
            letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
            lineHeight = 24.sp,
            color = androidx.compose.ui.graphics.Color.Black
        )
        Spacer(modifier = Modifier.width(35.dp))
    }

}


