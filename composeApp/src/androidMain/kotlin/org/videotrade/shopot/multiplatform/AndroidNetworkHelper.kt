package org.videotrade.shopot.multiplatform

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network

// AndroidNetworkHelper.kt
class AndroidNetworkHelper(context: Context) : NetworkHelper {
    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    
    override fun registerListener(onNetworkAvailable: () -> Unit, onNetworkLost: () -> Unit) {
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                println("asdadsdadaads onAvailable")
                
                onNetworkAvailable()
            }
            override fun onUnavailable() {
                println("asdadsdadaads onUnavailable")
                
                onNetworkLost()
            }
            override fun onLost(network: Network) {
                println("asdadsdadaads onLost")
                
                onNetworkLost()
            }
        }
        networkCallback?.let { connectivityManager.registerDefaultNetworkCallback(it) }
    }
    
    override fun unregisterListener() {
        
        networkCallback?.let { connectivityManager.unregisterNetworkCallback(it) }
    }
}
