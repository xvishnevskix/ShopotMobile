package org.videotrade.shopot.presentation.screens.test


import androidx.compose.runtime.Composable

import cafe.adriel.voyager.core.screen.Screen

import org.videotrade.shopot.presentation.components.Common.ZoomableImage

class TestScreen : Screen {
    @Composable
    override fun Content() {
        ZoomableImage()
    }
}

