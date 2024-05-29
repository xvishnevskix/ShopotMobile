//package org.videotrade.shopot.multiplatform
//
//import platform.AVFoundation.AVAuthorizationStatusAuthorized
//import platform.AVFoundation.AVAuthorizationStatusDenied
//import platform.AVFoundation.AVCaptureDevice
//import platform.AVFoundation.AVMediaTypeVideo
//import platform.Contacts.CNAuthorizationStatusAuthorized
//import platform.Contacts.CNContactStore
//import platform.Contacts.CNEntityTypeContacts
//import platform.Contacts.CNAuthorizationStatusNotDetermined
//import platform.Foundation.NSBundle
//import kotlinx.coroutines.suspendCancellableCoroutine
//import platform.AVFoundation.authorizationStatusForMediaType
//import platform.AVFoundation.requestAccessForMediaType
//import platform.Contacts.CNEntityType
//import kotlin.coroutines.resume
//
//actual class PermissionsProvider {
//
//    actual suspend fun getPermission(permissionName: String): Boolean {
//        return when (permissionName) {
//            "camera" -> requestCameraPermission()
//            "contacts" -> requestContactsPermission()
//            else -> {
//                println("Unknown permission: $permissionName")
//                false
//            }
//        }
//    }
//
//    private suspend fun requestCameraPermission(): Boolean {
//        return suspendCancellableCoroutine { continuation ->
//            val status = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)
//            if (status == AVAuthorizationStatusAuthorized) {
//                continuation.resume(true)
//            } else if (status == AVAuthorizationStatusDenied) {
//                continuation.resume(false)
//            } else {
//                AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { granted ->
//                    continuation.resume(granted)
//                }
//            }
//        }
//    }
//
//    private suspend fun requestContactsPermission(): Boolean {
//        return suspendCancellableCoroutine { continuation ->
//            val store = CNContactStore()
//            val status = CNContactStore.authorizationStatusForEntityType(CNEntityTypeContacts)
//            if (status == CNAuthorizationStatusAuthorized) {
//                continuation.resume(true)
//            } else if (status == CNAuthorizationStatusNotDetermined) {
//                store.requestAccessForEntityType(CNEntityType.CNEntityTypeContacts) { granted, _ ->
//                    continuation.resume(granted)
//                }
//            } else {
//                continuation.resume(false)
//            }
//        }
//    }
//}
//
//actual object PermissionsProviderFactory {
//    actual fun create(): PermissionsProvider {
//        return PermissionsProvider()
//    }
//}
