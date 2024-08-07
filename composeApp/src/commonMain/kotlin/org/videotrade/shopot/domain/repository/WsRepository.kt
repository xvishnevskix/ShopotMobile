package org.videotrade.shopot.domain.repository

import cafe.adriel.voyager.navigator.Navigator
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.network.sockets.Connection
import kotlinx.coroutines.flow.StateFlow


interface WsRepository {
    
    val wsSession: StateFlow<DefaultClientWebSocketSession?>
    
    
    suspend fun connectionWs(userId: String, navigator: Navigator)
    
    suspend fun disconnectWs()
    fun setConnection(isConnection: Boolean)
    
    
    suspend fun getWsSession(): DefaultClientWebSocketSession?
    fun setWsSession(wsSession: DefaultClientWebSocketSession)
    
    
    suspend fun clearData()
}