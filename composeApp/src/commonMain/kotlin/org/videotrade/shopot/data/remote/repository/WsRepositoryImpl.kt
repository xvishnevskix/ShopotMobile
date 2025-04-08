package org.videotrade.shopot.data.remote.repository

import cafe.adriel.voyager.navigator.Navigator
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.websocket.close
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.videotrade.shopot.api.handleConnectWebSocket
import org.videotrade.shopot.domain.repository.WsRepository

class WsRepositoryImpl : WsRepository, KoinComponent {
    
    override val isConnected: MutableStateFlow<Boolean> = MutableStateFlow(false)
    
    private val _wsSession = MutableStateFlow<DefaultClientWebSocketSession?>(null)
    override val wsSession: StateFlow<DefaultClientWebSocketSession?> get() = _wsSession
    
    override val processingReconnect: MutableStateFlow<Boolean> = MutableStateFlow(false)
    
    
    
    override suspend fun connectionWs(userId: String, navigator: Navigator) {
        
        
        println("aaaaaaaa1111111 $userId")
        
        handleConnectWebSocket(
            userId
        )
        
        
    }
    
    
    override suspend fun disconnectWs() {
//        clearData()
        isConnected.value = false
        _wsSession.value?.close()
        
    }
    
    override fun setConnection(isConnection: Boolean) {
        isConnected.value = isConnection
    }
    
    override suspend fun getWsSession(): DefaultClientWebSocketSession? {
        
        return wsSession.value
    }
    
    
    override fun setWsSession(wsSession: DefaultClientWebSocketSession?) {
        
        _wsSession.value = wsSession
    }
    
    override suspend fun clearData() {
        _wsSession.value?.close()
        _wsSession.value = null
        isConnected.value = false
    }
}