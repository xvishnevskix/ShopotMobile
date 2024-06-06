package org.videotrade.shopot.multiplatform

import androidx.compose.runtime.Composable
import org.videotrade.shopot.domain.model.ContactDTO

expect class ScreenOrientationProvider {
     
     @Composable
     fun  setScreenOrientation()
}


