package org.videotrade.shopot.domain.repository

import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import kotlinx.coroutines.flow.StateFlow


interface WsRepository {
    
    val wsSession: StateFlow<DefaultClientWebSocketSession?>
    
    
    suspend fun connectionWs(userId: String)
    
    suspend fun getWsSession(): DefaultClientWebSocketSession?
    
    

}