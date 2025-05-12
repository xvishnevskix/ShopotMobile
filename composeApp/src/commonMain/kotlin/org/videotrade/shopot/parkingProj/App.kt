package org.videotrade.shopot.parkingProj

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import org.videotrade.shopot.parkingProj.presentation.screens.MapScreen


class ParkingScreen : Screen {
    @Composable
    override fun Content() {
        MapScreen()
    }
}
