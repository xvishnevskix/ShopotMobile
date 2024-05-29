package org.videotrade.shopot.multiplatform

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.videotrade.shopot.AppActivity
import kotlin.coroutines.resume

actual class PermissionsProvider(private val activity: Activity) {
    
    companion object {
        const val REQUEST_CODE_CAMERA = 1
        const val REQUEST_CODE_CONTACTS = 2
    }
    
    actual suspend fun getPermission(permissionName: String): Boolean {
        return when (permissionName) {
            "camera" -> requestCameraPermission()
            "contacts" -> requestContactsPermission()
            else -> {
                println("Неизвестное разрешение: $permissionName")
                false
            }
        }
    }
    
    private suspend fun requestCameraPermission(): Boolean {
        return withContext(Dispatchers.Main) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
                suspendCancellableCoroutine<Boolean> { continuation ->
                    (activity as AppActivity).registerActivityResultCallback { requestCode, grantResults ->
                        if (requestCode == REQUEST_CODE_CAMERA) {
                            val granted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                            continuation.resume(granted)
                        }
                    }
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(Manifest.permission.CAMERA),
                        REQUEST_CODE_CAMERA
                    )
                }
            } else {
                println("Разрешение на использование камеры уже предоставлено")
                true
            }
        }
    }
    
    private suspend fun requestContactsPermission(): Boolean {
        return withContext(Dispatchers.Main) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
                suspendCancellableCoroutine<Boolean> { continuation ->
                    (activity as AppActivity).registerActivityResultCallback { requestCode, grantResults ->
                        if (requestCode == REQUEST_CODE_CONTACTS) {
                            val granted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                            continuation.resume(granted)
                        }
                    }
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(Manifest.permission.READ_CONTACTS),
                        REQUEST_CODE_CONTACTS
                    )
                }
            } else {
                println("Разрешение на доступ к контактам уже предоставлено")
                true
            }
        }
    }
}

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
