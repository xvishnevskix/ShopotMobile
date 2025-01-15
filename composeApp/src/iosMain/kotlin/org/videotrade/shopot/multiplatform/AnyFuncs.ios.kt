package org.videotrade.shopot.multiplatform

import com.mmk.kmpnotifier.notification.NotifierManager
import platform.Foundation.NSBundle
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual fun getPlatform(): Platform {
    return Platform.Ios
}

actual fun getBuildVersion(): Long {
    val buildVersion = NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleVersion") as? Long
    println("buildVersion $buildVersion")
    return buildVersion ?: 2000
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

actual suspend fun appUpdate() {
    // Ссылка на страницу приложения в App Store
    val appStoreUrl = "https://apps.apple.com/us/app/id6446670966?mt=8"  // Замените <YOUR_APP_ID> на ваш реальный идентификатор приложения

    // Преобразуем строку URL в объект NSURL
    val nsUrl = NSURL.URLWithString(appStoreUrl)

    // Проверяем, можем ли открыть URL
    nsUrl?.let {
        // Открываем URL через приложение (например, Safari или App Store)
        UIApplication.sharedApplication.openURL(it)
    }
}

actual fun openUrl(url: String) {
    val nsUrl = NSURL.URLWithString(url)
    if (nsUrl != null) {
        UIApplication.sharedApplication.openURL(nsUrl)
    } else {
        println("Invalid URL: $url")
    }
}