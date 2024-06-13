package org.videotrade.shopot.multiplatform

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import platform.AVFAudio.AVAudioSession
import platform.AVFoundation.*
import platform.Contacts.CNContactStore
import platform.Contacts.CNEntityType
import platform.Foundation.NSLog
import platform.UserNotifications.*
import kotlin.coroutines.resume

actual class PermissionsProvider {
    
    actual suspend fun getPermission(permissionName: String): Boolean {
        return when (permissionName) {
            "camera" -> requestCameraPermission()
            "contacts" -> requestContactsPermission()
            "microphone" -> requestMicrophonePermission()
            "notifications" -> requestNotificationPermission()
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
    
    private suspend fun requestNotificationPermission(): Boolean {
        return withContext(Dispatchers.Main) {
            suspendCancellableCoroutine<Boolean> { continuation ->
                UNUserNotificationCenter.currentNotificationCenter()
                    .requestAuthorizationWithOptions(
                        UNAuthorizationOptionSound or UNAuthorizationOptionAlert or UNAuthorizationOptionBadge
                    ) { granted, error ->
                        continuation.resume(granted)
                    }
            }
        }
    }
    
    actual suspend fun checkPermission(permissionName: String): Boolean {
        return withContext(Dispatchers.Main) {
            when (permissionName) {
                "camera" -> checkCameraPermissionStatus()
                "contacts" -> checkContactsPermissionStatus()
                "microphone" -> checkMicrophonePermissionStatus()
                "notifications" -> checkNotificationPermissionStatus()
                else -> {
                    NSLog("Неизвестное разрешение: $permissionName")
                    false
                }
            }
        }
    }
    
    private fun checkCameraPermissionStatus(): Boolean {
        val status = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)
        return status == AVAuthorizationStatusAuthorized
    }
    
    private fun checkContactsPermissionStatus(): Boolean {
        val status =
            CNContactStore.authorizationStatusForEntityType(CNEntityType.CNEntityTypeContacts)
        return status == CNAuthorizationStatusAuthorized
    }
    
    private fun checkMicrophonePermissionStatus(): Boolean {
        val status = AVAudioSession.sharedInstance().recordPermission
        return status.toLong() == AVAudioSessionRecordPermissionGranted
    }
    
    private fun checkNotificationPermissionStatus(): Boolean {
        var isGranted = false
        UNUserNotificationCenter.currentNotificationCenter()
            .getNotificationSettingsWithCompletionHandler { settings ->
                isGranted = settings?.authorizationStatus == UNAuthorizationStatusAuthorized
            }
        return isGranted
    }
    
    companion object {
        private const val AVAuthorizationStatusAuthorized = 3L
        private const val CNAuthorizationStatusAuthorized = 3L
        private const val AVAudioSessionRecordPermissionGranted = 1735552628L
        private const val UNAuthorizationStatusAuthorized = 2L
    }
}

actual object PermissionsProviderFactory {
    actual fun create(): PermissionsProvider {
        return PermissionsProvider()
    }
}
