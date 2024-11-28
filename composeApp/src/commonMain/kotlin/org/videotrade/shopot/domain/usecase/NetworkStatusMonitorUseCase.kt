package org.videotrade.shopot.domain.usecase

import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.videotrade.shopot.domain.model.ContactDTO
import org.videotrade.shopot.domain.repository.ContactsRepository
//import org.videotrade.shopot.domain.repository.NetworkStatusMonitorRepository

//class NetworkStatusMonitorUseCase : KoinComponent {
//    private val repository: NetworkStatusMonitorRepository by inject()
//
//
//    val isConnected: StateFlow<Boolean> get() = repository.isConnected
//
//     fun setIsConnected(connectValue: Boolean) {
//        return repository.setIsConnected(connectValue)
//    }
//
//}