package org.videotrade.shopot.multiplatform

actual class PermissionsProvider {
    actual suspend fun getPermission(permissionName: String): Boolean {
        TODO("Not yet implemented")
    }
}

actual object PermissionsProviderFactory {
    actual fun create(): PermissionsProvider {
        TODO("Not yet implemented")
    }
}