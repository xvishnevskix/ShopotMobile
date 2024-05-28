package org.videotrade.shopot.domain.usecase

import cafe.adriel.voyager.navigator.Navigator
import com.shepeliev.webrtckmp.MediaStream
import com.shepeliev.webrtckmp.PeerConnection
import com.shepeliev.webrtckmp.VideoStreamTrack
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.videotrade.shopot.domain.repository.CallRepository

class CallUseCase : KoinComponent {
    private val repository: CallRepository by inject()
    
    val inCommingCall: StateFlow<Boolean> get() = repository.inCommingCall
    val wsSession: StateFlow<DefaultClientWebSocketSession?> get() = repository.wsSession
    
    val peerConnection: StateFlow<PeerConnection> get() = repository.peerConnection
    val isConnectedWebrtc: StateFlow<Boolean> get() = repository.isConnectedWebrtc
    val localStream: StateFlow<MediaStream?> get() = repository.localStream
    val remoteVideoTrack: StateFlow<VideoStreamTrack?> get() = repository.remoteVideoTrack
    
    
    suspend fun connectionWs(userId: String, navigator: Navigator) {
        
        return repository.connectionWs(userId, navigator)
    }
    
    suspend fun setOffer() {
        
        return repository.setOffer()
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
    
    
    suspend fun initWebrtc(): Nothing {
        repository.initWebrtc()
    }
}
