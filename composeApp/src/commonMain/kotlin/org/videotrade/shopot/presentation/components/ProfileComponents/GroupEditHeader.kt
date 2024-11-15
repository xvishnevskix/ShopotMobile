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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
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
import org.videotrade.shopot.presentation.screens.group.GroupEditScreen
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.arrow_left
import shopot.composeapp.generated.resources.check_mark
import shopot.composeapp.generated.resources.profile_accept

@Composable
fun GroupEditHeader(text: String, onClick: (() -> Unit)? = null) {
    val navigator = LocalNavigator.currentOrThrow

    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 40.dp).background(Color(0xFFf9f9f9)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,

            ) {
//            Icon(
//                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                contentDescription = "Back",
//                modifier = Modifier.padding(start = 10.dp,end = 1.dp).pointerInput(Unit) {
//                    navigator.pop()
//                },
//                tint = Color.Black
//            )

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
            Image(
                painter = painterResource(Res.drawable.profile_accept),
                contentDescription = "Checkmark",
                modifier = Modifier.padding(start = 0.dp, end = 6.dp)
                    .size(18.dp).pointerInput(Unit) {
                        if (onClick != null) {
                            onClick()
                        }
                    },
                contentScale = ContentScale.FillBounds
            )
        }
        Box(Modifier.padding(bottom = 23.dp)) {
            CallBar()
        }
    }

}


