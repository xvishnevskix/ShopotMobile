package org.videotrade.shopot.multiplatform

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

actual class ScreenOrientationProvider {
    @Composable
    actual fun setScreenOrientation() {
        val activity = LocalContext.current as Activity
        DisposableEffect(Unit) {
            val originalOrientation = activity.requestedOrientation
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            onDispose {
                activity.requestedOrientation = originalOrientation
            }
        }
    }
}
