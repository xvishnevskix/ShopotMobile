package org.videotrade.shopot.multiplatform

import android.os.Build
import androidx.annotation.RequiresApi
import org.videotrade.shopot.androidSpecificApi.getContextObj

actual fun getPlatform(): String {
    return "Android"
}

@RequiresApi(Build.VERSION_CODES.P)
actual fun getBuildVersion(): Long {
    val context = getContextObj.getContext()
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    val versionName = packageInfo.versionName  // версия приложения
    return packageInfo.longVersionCode
}