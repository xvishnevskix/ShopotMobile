package org.videotrade.shopot.presentation.components.Common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.dp
import com.dokar.sonner.Toaster
import org.koin.compose.koinInject
import org.videotrade.shopot.presentation.screens.common.ToasterViewModel

@Composable
fun SafeArea(isBlurred: Boolean = false, content: @Composable () -> Unit) {
    val blurRadius = if (isBlurred) 10.dp else 0.dp
    val toasterViewModel: ToasterViewModel = koinInject()
    
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .blur(blurRadius)
            .safeContentPadding()
    ) {
        
        Toaster(state = toasterViewModel.toaster)
        
        content()
    }
}