package org.videotrade.shopot.parkingProj.presentation.components.Common

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.jetbrains.compose.resources.painterResource
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.arrow_left


@Composable
fun BackIcon(modifier: Modifier = Modifier) {
    val navigator = LocalNavigator.currentOrThrow

    Box(modifier = Modifier
        .clip(CircleShape)
        .clickable {
            navigator.pop()
        }
        .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        Image(
            modifier = modifier
                .size(width = 9.dp, height = 18.dp),
            painter = painterResource(Res.drawable.arrow_left),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.tint(Color(0xFF007AFF))
        )
    }
}