package org.videotrade.shopot.multiplatform

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import org.koin.mp.KoinPlatform
import org.videotrade.shopot.androidSpecificApi.getContextObj
//import org.videotrade.shopot.domain.usecase.NetworkStatusMonitorUseCase


//actual class NetworkStatusMonitorProvider {
//    private val connectivityManager = getContextObj.getContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//
//    private val networkStatusMonitorUseCase: NetworkStatusMonitorUseCase = KoinPlatform.getKoin().get()
//
//
//    init {
//        val networkCallback = object : ConnectivityManager.NetworkCallback() {
//            override fun onAvailable(network: Network) {
//                networkStatusMonitorUseCase.setIsConnected(true)
//            }
//
//            override fun onLost(network: Network) {
//                networkStatusMonitorUseCase.setIsConnected(false)
//            }
//        }
//
//        val request = NetworkRequest.Builder()
//            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
//            .build()
//
//        connectivityManager.registerNetworkCallback(request, networkCallback)
//    }
//}
//
//
//actual object NetworkStatusMonitorFactory {
//    actual fun create(): NetworkStatusMonitorProvider {
//        return NetworkStatusMonitorProvider()
//    }
//}
