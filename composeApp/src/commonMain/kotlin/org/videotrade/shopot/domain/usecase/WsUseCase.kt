package org.videotrade.shopot.domain.usecase

import cafe.adriel.voyager.navigator.Navigator
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.videotrade.shopot.domain.repository.WsRepository

class WsUseCase : KoinComponent {
    private val repository: WsRepository by inject()
    val wsSession: StateFlow<DefaultClientWebSocketSession?> get() = repository.wsSession
    
    suspend fun connectionWs(userId: String, navigator: Navigator) {
        return repository.connectionWs(userId, navigator)
    }
    
    
    suspend fun getWsSession(): DefaultClientWebSocketSession? {
        return repository.getWsSession()
    }
    
    
    suspend fun clearData() {
        repository.clearData()
    }
}