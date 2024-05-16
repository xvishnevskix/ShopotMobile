package org.videotrade.shopot.data.remote.repository

import androidx.compose.runtime.mutableStateOf
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.videotrade.shopot.api.handleWebRTCWebSocket
import org.videotrade.shopot.domain.repository.WsRepository
import org.videotrade.shopot.domain.usecase.ChatUseCase

class WsRepositoryImpl : WsRepository, KoinComponent {
    
    private val webSocketSession = mutableStateOf<DefaultClientWebSocketSession?>(null)
    private val isConnected = mutableStateOf(false)
    
    
    override suspend fun connectionWs(userId: String) {
        val chatUseCase: ChatUseCase by inject()
        
        println("chatUseCase $chatUseCase")
        
        handleWebRTCWebSocket(
            webSocketSession,
            isConnected,
            userId,
            chatUseCase
        )
        
    
    }
    
    override suspend fun getWsSession(): DefaultClientWebSocketSession? {
        
        return webSocketSession.value
    }
    
    
}