package org.videotrade.shopot.data.remote.repository

import androidx.compose.runtime.mutableStateOf
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import org.videotrade.shopot.api.handleWebRTCWebSocket
import org.videotrade.shopot.domain.repository.WsRepository

class WsRepositoryImpl : WsRepository {
    
    private val webSocketSession = mutableStateOf<DefaultClientWebSocketSession?>(null)
    private val isConnected = mutableStateOf(false)
    
    
    override suspend fun connectionWs(userId: String) {
        handleWebRTCWebSocket(
            webSocketSession,
            isConnected,
            userId
        )
    }
    
    override suspend fun getWsSession(): DefaultClientWebSocketSession? {
        
        return webSocketSession.value
    }
    
    
}