package org.videotrade.shopot.multiplatform
import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings


actual class DeviceIdProvider(private val context: Context) {
    actual fun getDeviceId(): String? {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }
}

actual object DeviceIdProviderFactory {
    private lateinit var applicationContext: Context
    
    fun initialize(context: Context) {
        applicationContext = context
    }
    
    actual fun create(): DeviceIdProvider {
        return DeviceIdProvider(applicationContext)
    }
}
