package org.videotrade.shopot.multiplatform

expect fun getPlatform(): String

expect fun getBuildVersion(): Long

expect fun closeApp()

expect fun isScreenOn(): Boolean

expect fun setScreenLockFlags(showWhenLocked: Boolean)

