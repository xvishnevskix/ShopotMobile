package org.videotrade.shopot.presentation.components.Auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import shopot.composeapp.generated.resources.Montserrat_Regular
import shopot.composeapp.generated.resources.Res

@Composable
fun AuthHeader(text: String, f: Float = 0.55F) {
    val navigator = LocalNavigator.currentOrThrow

    Row(
        modifier = Modifier.fillMaxWidth(f).padding(top = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,

        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.padding(end = 8.dp).clickable {
                    navigator.pop()
                },
                tint = Color.Black
            )

            Text(
                text = text,
                fontFamily = FontFamily(Font(Res.font.Montserrat_Regular)),
                fontSize = 20.sp,
                fontWeight = FontWeight.W600,
                textAlign = TextAlign.Center,
                letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                lineHeight = 24.sp,
                color = Color.Black
                
            )

    }

}


