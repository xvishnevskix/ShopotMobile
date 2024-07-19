
package org.videotrade.shopot.multiplatform


expect class PermissionsProvider {
    suspend fun getPermission(permissionName: String): Boolean
    suspend fun checkPermission(permissionName: String): Boolean
    
}


expect object PermissionsProviderFactory {
    fun create(): PermissionsProvider
}



