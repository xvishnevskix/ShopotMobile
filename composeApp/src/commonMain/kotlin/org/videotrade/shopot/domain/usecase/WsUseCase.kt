package org.videotrade.shopot.domain.usecase

import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.videotrade.shopot.domain.repository.WsRepository

class WsUseCase : KoinComponent {
    private val repository: WsRepository by inject()

    suspend fun connectionWs(userId: String) {
        return repository.connectionWs(userId)
    }
    
    
    suspend fun getWsSession(): DefaultClientWebSocketSession? {
        return repository.getWsSession()
    }

}