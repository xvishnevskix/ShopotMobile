package org.videotrade.shopot.parkingProj.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun SafeArea(
    isBlurred: Boolean = false,
    padding: Dp? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    content: @Composable () -> Unit,
) {
    val blurRadius = if (isBlurred) 20.dp else 0.dp
    
    Box(
        modifier = Modifier
            .fillMaxSize().background(backgroundColor)
            .blur(blurRadius)
            .then(
                Modifier.statusBarsPadding()
                    .navigationBarsPadding()
            )
    ) {
        content()
    }
}