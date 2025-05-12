package org.videotrade.shopot.parkingProj.presentation.components.Common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.videotrade.shopot.parkingProj.presentation.components.Common.CustomText
import org.videotrade.shopot.parkingProj.presentation.components.Common.FontStyleType
import org.videotrade.shopot.parkingProj.presentation.components.Common.TextType


@Composable
fun Title(date: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFEFEFF4))
            .padding(horizontal = 16.dp)
    ) {
        CustomText(
            text = date,
            fontSize = 14.sp,
            type = TextType.SECONDARY,
            fontStyle = FontStyleType.Medium,
            isUppercase = true
        )
    }
}