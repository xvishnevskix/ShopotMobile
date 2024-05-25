package org.videotrade.shopot.domain.usecase

import com.shepeliev.webrtckmp.PeerConnection
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.videotrade.shopot.domain.repository.CallRepository

class CallUseCase : KoinComponent {
    private val repository: CallRepository by inject()
    
    val inCommingCall: StateFlow<Boolean> get() = repository.inCommingCall
    val wsSession : StateFlow<DefaultClientWebSocketSession?> get() = repository.wsSession
    
    val peerConnection: StateFlow<PeerConnection> get() = repository.peerConnection
    
    
    
    suspend fun connectionWs(userId: String) {
        return repository.connectionWs(userId)
    }
    
    suspend fun getWsSession(): DefaultClientWebSocketSession? {
        return repository.getWsSession()
    }
    
    suspend fun getPeerConnection(): PeerConnection {
        return repository.getPeerConnection()
    }
    
     fun getOtherUserId(): String {
        return repository.getOtherUserId()
    }
    fun getCallerId(): String {
        return repository.getCallerId()
    }
    
    
    
     fun updateOtherUserId(userId: String) {
        return repository.updateOtherUserId(userId)
    }
}
