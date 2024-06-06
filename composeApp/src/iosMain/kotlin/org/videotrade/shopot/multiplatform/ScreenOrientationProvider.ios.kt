package org.videotrade.shopot.multiplatform

import androidx.compose.runtime.Composable
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCClass
import kotlinx.cinterop.objcPtr
import platform.Foundation.NSSelectorFromString
import platform.UIKit.UIDevice
import platform.UIKit.UIInterfaceOrientation
import platform.UIKit.UIInterfaceOrientationMask
import platform.UIKit.UIInterfaceOrientationMaskPortrait
import platform.UIKit.UIInterfaceOrientationPortrait
import platform.UIKit.UIViewController
import platform.objc.objc_getClass

actual class ScreenOrientationProvider {
    @Composable
    actual fun setScreenOrientation() {
    
    }
    

}
