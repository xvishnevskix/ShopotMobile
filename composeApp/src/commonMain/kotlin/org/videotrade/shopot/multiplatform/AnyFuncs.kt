package org.videotrade.shopot.multiplatform

expect fun getPlatform(): Platform

expect fun getBuildVersion(): Long

expect fun closeApp()

expect fun isScreenOn(): Boolean

expect fun setScreenLockFlags(showWhenLocked: Boolean)
expect fun openUrl(url: String)

expect suspend fun getFbToken(): String?
expect suspend fun appUpdate()





enum class Platform {
    Ios,
    Android
}

