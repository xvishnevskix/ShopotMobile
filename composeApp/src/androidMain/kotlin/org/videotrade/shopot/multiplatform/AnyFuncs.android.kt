package org.videotrade.shopot.multiplatform

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Process
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import com.mmk.kmpnotifier.notification.NotifierManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.videotrade.shopot.androidSpecificApi.getContextObj
import kotlin.system.exitProcess

actual fun getPlatform(): Platform {
    return Platform.Android
}

@RequiresApi(Build.VERSION_CODES.P)
actual fun getBuildVersion(): Long {
    val context = getContextObj.getContext()
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        // Для Android 9 и выше
        packageInfo.longVersionCode
    } else {
        // Для Android 8.1 и ниже
        @Suppress("DEPRECATION")
        packageInfo.versionCode.toLong()
    }
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

actual fun isScreenOn(): Boolean {
    val context = getContextObj.getContext()
    
    val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
    return !keyguardManager.isDeviceLocked
    
}

actual fun setScreenLockFlags(showWhenLocked: Boolean) {
    
    println("setScreenLockFlags")
    
    
    val activity = getContextObj.getActivity() as ComponentActivity
    
    activity.setShowWhenLocked(showWhenLocked)
    activity.setTurnScreenOn(showWhenLocked)
}

actual suspend fun getFbToken(): String? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        try {
            // Попытка получить токен на SDK >= 28
            NotifierManager.getPushNotifier().getToken()
        } catch (e: Exception) {
            // Обработка исключений, если не удалось получить токен
            Log.e("FB_TOKEN", "Error retrieving FCM token", e)
            null
        }
    } else {
        // Возвращаем null для SDK < 28
        Log.w("FB_TOKEN", "FCM token is not supported on SDK < 28")
        null
    }
}

actual suspend fun appUpdate() {
    // Получаем контекст из локального окруженя
    val context = getContextObj.getActivity() as ComponentActivity

    // Ссылка на страницу приложения в RuStore
    val url = "https://www.rustore.ru/catalog/app/org.videotrade.shopot.androidApp"

    // Переход в главный поток для выполнения действия UI
    withContext(Dispatchers.Main) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }
}