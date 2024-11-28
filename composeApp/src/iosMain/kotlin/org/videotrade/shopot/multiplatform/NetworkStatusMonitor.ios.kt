package org.videotrade.shopot.multiplatform

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.mp.KoinPlatform
//import org.videotrade.shopot.domain.usecase.NetworkStatusMonitorUseCase
import platform.NetworkExtension.NWPathStatusSatisfied
import platform.darwin.DISPATCH_QUEUE_PRIORITY_DEFAULT
import platform.darwin.dispatch_get_global_queue
import platform.Network.*
import platform.darwin.*

//interface NetworkHelper {
//    fun registerListener(onNetworkAvailable: () -> Unit, onNetworkLost: () -> Unit)
//    fun unregisterListener()
//}
//
//actual class NetworkStatusMonitorProvider {
//    private val monitor = NWPathMonitor()
//    private val _isConnected = MutableStateFlow(true)
//    val isConnected: StateFlow<Boolean> get() = _isConnected
//
//    init {
//        monitor.pathUpdateHandler = { path ->
//            _isConnected.value = path.status == NWPathStatusSatisfied
//        }
//        monitor.start(queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT.toLong(),
//            0u
//        ))
//    }
//}
//
//
//
//actual object NetworkStatusMonitorFactory {
//    actual fun create(): NetworkStatusMonitorProvider {
//        return NetworkStatusMonitorProvider()
//    }
//}
