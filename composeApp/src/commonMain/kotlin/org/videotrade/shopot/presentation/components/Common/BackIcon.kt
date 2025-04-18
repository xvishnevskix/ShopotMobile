package org.videotrade.shopot.presentation.components.Common

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import org.jetbrains.compose.resources.painterResource
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.arrow_left

@Composable
fun BackIcon(modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Image(
        modifier = modifier
            .size(width = 7.dp, height = 14.dp),
        painter = painterResource(Res.drawable.arrow_left),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        colorFilter =  ColorFilter.tint(colors.primary)
    )

//    Icon(
//        imageVector = Icons.Default.ArrowBack,
//        contentDescription = "Back",
//        modifier = modifier,
//        tint = Color(0xFF000000)
//    )
}