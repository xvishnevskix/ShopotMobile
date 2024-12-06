package org.videotrade.shopot.multiplatform

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform
import org.videotrade.shopot.api.getValueInStorage
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.common.NetworkErrorScreen

// NetworkListener.kt
class NetworkListener(private val helper: NetworkHelper) {
    private val commonViewModel: CommonViewModel = KoinPlatform.getKoin().get()
    
    val networkStatus: Flow<NetworkStatus> = callbackFlow {
        helper.registerListener(
            onNetworkAvailable = {
                CoroutineScope(Dispatchers.Main).launch {
                    println("asdadsdadaads onNetworkAvailable")
                    
                    if (commonViewModel.isReconnectionWs.value) {
                        commonViewModel.connectionWs()
                    }
                    commonViewModel.setIsReconnectionWs(false)
                    
                }
                trySend(NetworkStatus.Connected)
            },
            onNetworkLost = {
                CoroutineScope(Dispatchers.Main).launch {
                    commonViewModel.setIsReconnectionWs(true)
                }
                trySend(NetworkStatus.Disconnected)
            }
        )
        
        
        
        awaitClose {
            helper.unregisterListener()
        }
    }.distinctUntilChanged().flowOn(Dispatchers.IO)
}

sealed class NetworkStatus {
    data object Connected : NetworkStatus()
    data object Disconnected : NetworkStatus()
}
