package org.videotrade.shopot.domain.repository

import com.shepeliev.webrtckmp.PeerConnection
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import kotlinx.coroutines.flow.StateFlow

interface CallRepository {
    
    
    val wsSession: StateFlow<DefaultClientWebSocketSession?>
    
    
    val inCommingCall: StateFlow<Boolean>
    
    val peerConnection: StateFlow<PeerConnection>
    
    
    suspend fun connectionWs(userId: String)
    suspend fun reconnectPeerConnection()
    suspend fun getWsSession(): DefaultClientWebSocketSession?
    suspend fun getPeerConnection(): PeerConnection
    
    
    fun getCallerId(): String
    
     fun getOtherUserId(): String
    
    
     fun updateOtherUserId(userId: String)
     
    
    
}
