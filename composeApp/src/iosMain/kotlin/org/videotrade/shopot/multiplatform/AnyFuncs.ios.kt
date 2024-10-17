package org.videotrade.shopot.multiplatform

actual fun getPlatform(): String {
    return "Ios"
}

actual fun getBuildVersion(): Long {
//    val buildVersion = NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleVersion") as? Long
//    return buildVersion ?: 0
    return  0
}

actual fun closeApp() {
}

actual fun isScreenOn(): Boolean {
    return false
}

actual fun setScreenLockFlags(showWhenLocked: Boolean) {
}