package org.videotrade.shopot.parkingProj.presentation.components.Map.Buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.videotrade.shopot.parkingProj.presentation.components.Common.CustomImage
import org.videotrade.shopot.parkingProj.presentation.components.Common.CustomText
import org.videotrade.shopot.parkingProj.presentation.components.Common.FontStyleType
import org.videotrade.shopot.parkingProj.presentation.components.Common.TextType
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.plus

@Composable
fun BalanceChip(balance: String, onTopUp: () -> Unit) {
    Row(
        modifier = Modifier
            .shadow(14.dp, shape = RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .clickable(onClick = onTopUp)
            .padding(horizontal = 12.dp, vertical = 8.dp)
        ,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CustomText(text = balance, type = TextType.PRIMARY, fontStyle = FontStyleType.Medium)
        Divider(
            color = Color.LightGray,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .height(16.dp)
                .width(1.dp)
        )
        CustomImage(image = Res.drawable.plus, size = 12.dp, tint = Color(0xFF65B144))
    }
}