package org.videotrade.shopot.multiplatform

// NetworkHelper.kt
interface NetworkHelper {
    fun registerListener(onNetworkAvailable: () -> Unit, onNetworkLost: () -> Unit)
    fun unregisterListener()
}
