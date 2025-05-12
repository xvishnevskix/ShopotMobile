package org.videotrade.shopot.parkingProj.presentation.components.Map.Buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.videotrade.shopot.parkingProj.presentation.components.Common.CustomImage
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.minus
import shopot.composeapp.generated.resources.plus

@Composable
fun ZoomControlButtons(
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
) {
    Column(
        modifier = Modifier
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .wrapContentHeight()
            .width(40.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(topStart =  16.dp, topEnd = 16.dp))
                .fillMaxWidth()
                .height(40.dp)
                .clickable {
                    onZoomIn()
                },
            contentAlignment = Alignment.Center
        ) {
            CustomImage(image = Res.drawable.plus, size = 12.dp)
        }

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp),
            color = Color(0xFFE0E0E0)
        )

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(bottomStart =  16.dp, bottomEnd = 16.dp))
                .fillMaxWidth()
                .height(40.dp)
                .clickable {
                    onZoomOut()
                },
            contentAlignment = Alignment.Center
        ) {
            CustomImage(image = Res.drawable.minus, size = 12.dp)
        }
    }
}