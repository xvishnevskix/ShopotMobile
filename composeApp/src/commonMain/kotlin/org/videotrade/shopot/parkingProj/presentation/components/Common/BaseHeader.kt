package org.videotrade.shopot.parkingProj.presentation.components.Common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BaseHeader(text: String, background: Color = Color(0xFFf9f9f9)) {

    Column {
        Row(
            modifier = Modifier
                .background(background)
                .fillMaxWidth()
                .padding(top = 35.dp, bottom = 5.dp, start = 4.dp, end = 4.dp)
            ,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            BackIcon()
            CustomText(text, fontStyle = FontStyleType.Bold)
            Spacer(modifier = Modifier.width(24.dp))
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(color = Color(0xFFD9D9D9))
        ) {

        }
    }
}

