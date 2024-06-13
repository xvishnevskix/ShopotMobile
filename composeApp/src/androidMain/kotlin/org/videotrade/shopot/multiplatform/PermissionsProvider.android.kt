package org.videotrade.shopot.multiplatform

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.videotrade.shopot.AppActivity
import kotlin.coroutines.resume

actual class PermissionsProvider(private val activity: Activity) {
    
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    actual suspend fun getPermission(permissionName: String): Boolean {
        return when (permissionName) {
            "camera" -> requestPermission(
                Manifest.permission.CAMERA,
                AppActivity.REQUEST_CODE_CAMERA
            )
            
            "contacts" -> requestPermission(
                Manifest.permission.READ_CONTACTS,
                AppActivity.REQUEST_CODE_CONTACTS
            )
            
            "microphone" -> requestPermission(
                Manifest.permission.RECORD_AUDIO,
                AppActivity.REQUEST_CODE_MICROPHONE
            )
            
            "notifications" -> requestNotificationPermission()
            else -> {
                println("Неизвестное разрешение: $permissionName")
                false
            }
        }
    }
    
    private suspend fun requestPermission(permission: String, requestCode: Int): Boolean {
        return withContext(Dispatchers.Main) {
            suspendCancellableCoroutine<Boolean> { continuation ->
                (activity as AppActivity).requestPermission(
                    permission,
                    requestCode
                ) { code, isGranted ->
                    println("Запрос кода: $code, результат: $isGranted")
                    if (!continuation.isCompleted) {
                        continuation.resume(isGranted)
                    }
                }
            }
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private suspend fun requestNotificationPermission(): Boolean {
        return requestPermission(
            Manifest.permission.POST_NOTIFICATIONS,
            AppActivity.REQUEST_CODE_NOTIFICATIONS
        )
    }
    
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    actual suspend fun checkPermission(permissionName: String): Boolean {
        return withContext(Dispatchers.Main) {
            when (permissionName) {
                "camera" -> checkPermissionStatus(Manifest.permission.CAMERA)
                "contacts" -> checkPermissionStatus(Manifest.permission.READ_CONTACTS)
                "microphone" -> checkPermissionStatus(Manifest.permission.RECORD_AUDIO)
                "notifications" -> checkPermissionStatus(Manifest.permission.POST_NOTIFICATIONS)
                else -> {
                    println("Неизвестное разрешение: $permissionName")
                    false
                }
            }
        }
    }
    
    private fun checkPermissionStatus(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            activity,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
}

@SuppressLint("StaticFieldLeak")
actual object PermissionsProviderFactory {
    private lateinit var activity: Activity
    
    @SuppressLint("StaticFieldLeak")
    fun initialize(activity: Activity) {
        this.activity = activity
    }
    
    @SuppressLint("StaticFieldLeak")
    actual fun create(): PermissionsProvider {
        return PermissionsProvider(activity)
    }
}
