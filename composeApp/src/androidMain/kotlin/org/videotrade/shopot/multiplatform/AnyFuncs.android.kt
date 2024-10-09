package org.videotrade.shopot.multiplatform

import android.os.Build
import android.os.Process
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import org.videotrade.shopot.androidSpecificApi.getContextObj
import kotlin.system.exitProcess

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


actual fun startOutgoingCall() {
    TODO()
}


actual fun simulateIncomingCall() {
    TODO()
}

actual fun closeApp() {
    
    val context = getContextObj.getActivity()
    
    if (context !== null) {
        val activity = context as ComponentActivity
        activity.finishAffinity() // Закрывает все активности
        Process.killProcess(Process.myPid()) // Убивает процесс
        exitProcess(0) // Выход из приложения
    }
    
}