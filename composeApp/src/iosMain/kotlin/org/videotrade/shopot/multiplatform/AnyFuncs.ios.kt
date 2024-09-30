package org.videotrade.shopot.multiplatform

actual fun getPlatform(): String {
    return "Ios"
}

actual fun getBuildVersion(): Long {
    val buildVersion = NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleVersion") as? Long
    return buildVersion ?: 0
}