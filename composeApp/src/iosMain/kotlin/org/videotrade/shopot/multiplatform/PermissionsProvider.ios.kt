package org.videotrade.shopot.multiplatform

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import platform.AVFAudio.AVAudioSession
import platform.AVFoundation.AVAuthorizationStatusAuthorized
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.requestAccessForMediaType
import platform.Contacts.CNAuthorizationStatusAuthorized
import platform.Contacts.CNContactStore
import platform.Contacts.CNEntityType
import platform.Foundation.NSLog
import kotlin.coroutines.resume
actual class PermissionsProvider {
    
    actual suspend fun getPermission(permissionName: String): Boolean {
        return when (permissionName) {
            "camera" -> requestCameraPermission()
            "contacts" -> requestContactsPermission()
            "microphone" -> requestMicrophonePermission()
            else -> {
                NSLog("Неизвестное разрешение: $permissionName")
                false
            }
        }
    }
    
    private suspend fun requestCameraPermission(): Boolean {
        return withContext(Dispatchers.Main) {
            suspendCancellableCoroutine<Boolean> { continuation ->
                AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { granted ->
                    continuation.resume(granted)
                }
            }
        }
    }
    
    private suspend fun requestContactsPermission(): Boolean {
        return withContext(Dispatchers.Main) {
            suspendCancellableCoroutine<Boolean> { continuation ->
                val store = CNContactStore()
                store.requestAccessForEntityType(CNEntityType.CNEntityTypeContacts) { granted, error ->
                    continuation.resume(granted)
                }
            }
        }
    }
    
    private suspend fun requestMicrophonePermission(): Boolean {
        return withContext(Dispatchers.Main) {
            suspendCancellableCoroutine<Boolean> { continuation ->
                AVAudioSession.sharedInstance().requestRecordPermission { granted ->
                    continuation.resume(granted)
                }
            }
        }
    }
}

actual object PermissionsProviderFactory {
    actual fun create(): PermissionsProvider {
        return PermissionsProvider()
    }
}