package org.videotrade.shopot.presentation.components.Common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dokar.sonner.Toaster
import org.koin.compose.koinInject
import org.videotrade.shopot.multiplatform.getPlatform
import org.videotrade.shopot.presentation.screens.common.CommonViewModel

@Composable
fun SafeArea(
    isBlurred: Boolean = false,
    padding: Dp? = null,
    content: @Composable () -> Unit
) {
    val blurRadius = if (isBlurred) 20.dp else 0.dp
    val toasterViewModel: CommonViewModel = koinInject()
    
    Box(
        modifier = Modifier
            .fillMaxSize().background(Color.White)
            .blur(blurRadius)
            .then(
                if (getPlatform() == "Ios") {
                    Modifier.safeContentPadding()
                } else {
                    if (padding != null) Modifier.padding(padding) else Modifier.padding(
                        start = 20.dp,
                        end = 20.dp
                    ).statusBarsPadding()
                }
            )
    ) {
        Toaster(state = toasterViewModel.toaster)
        content()
    }
}