package org.videotrade.shopot.multiplatform

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import org.videotrade.shopot.androidSpecificApi.getContextObj

actual fun checkNetwork(): Boolean {
    val context = getContextObj.getContext()
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}
