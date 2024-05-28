package org.videotrade.shopot.domain.repository

import cafe.adriel.voyager.navigator.Navigator
import com.shepeliev.webrtckmp.MediaStream
import com.shepeliev.webrtckmp.PeerConnection
import com.shepeliev.webrtckmp.VideoStreamTrack
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import kotlinx.coroutines.flow.StateFlow

interface CallRepository {
    
    
    val wsSession: StateFlow<DefaultClientWebSocketSession?>
    
    
    val inCommingCall: StateFlow<Boolean>
    
    val peerConnection: StateFlow<PeerConnection>
    
    
    val isConnectedWebrtc: StateFlow<Boolean>
    
    
    val localStream: StateFlow<MediaStream?>
    
    val remoteVideoTrack: StateFlow<VideoStreamTrack?>
    
    
    
    
    suspend fun connectionWs(userId: String, navigator: Navigator)
    suspend fun reconnectPeerConnection()
    suspend fun setOffer()
    
    
    suspend fun getWsSession(): DefaultClientWebSocketSession?
    suspend fun getPeerConnection(): PeerConnection
    
    
    fun getCallerId(): String
    
    fun getOtherUserId(): String
    
    
    fun updateOtherUserId(userId: String)
    suspend fun initWebrtc(): Nothing
    
    
    
    
}
