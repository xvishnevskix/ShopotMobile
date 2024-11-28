package org.videotrade.shopot.multiplatform

import com.mmk.kmpnotifier.notification.NotifierManager
import platform.Foundation.NSBundle

actual fun getPlatform(): Platform {
    return Platform.Ios
}

actual fun getBuildVersion(): Long {
    val buildVersion = NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleVersion") as? Long
    return buildVersion ?: 0
}

actual fun closeApp() {
}

actual fun isScreenOn(): Boolean {
    return true
}

actual fun setScreenLockFlags(showWhenLocked: Boolean) {
}

actual suspend fun getFbToken(): String? {
    return  NotifierManager.getPushNotifier().getToken()
}