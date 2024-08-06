package org.videotrade.shopot.domain.repository

import cafe.adriel.voyager.navigator.Navigator
import com.shepeliev.webrtckmp.IceConnectionState
import com.shepeliev.webrtckmp.MediaStream
import com.shepeliev.webrtckmp.PeerConnection
import com.shepeliev.webrtckmp.PeerConnectionState
import com.shepeliev.webrtckmp.VideoStreamTrack
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import kotlinx.coroutines.flow.StateFlow

interface CallRepository {
    
    
    val wsSession: StateFlow<DefaultClientWebSocketSession?>
    
    val peerConnection: StateFlow<PeerConnection?>
    
    
    val isConnectedWebrtc: StateFlow<Boolean>
    
    
    val localStream: StateFlow<MediaStream?>
    
    val remoteVideoTrack: StateFlow<VideoStreamTrack?>
    
    val callState: StateFlow<PeerConnectionState>
    
    val iseState: StateFlow<IceConnectionState>
    
    
    suspend fun connectionWs(userId: String, navigator: Navigator)
    suspend fun reconnectPeerConnection()
    suspend fun setOffer()
    
    
    suspend fun getWsSession(): DefaultClientWebSocketSession?
    suspend fun getPeerConnection(): PeerConnection?
    
    
    fun getCallerId(): String
    fun setMicro()
    
    fun getOtherUserId(): String
    
    
    fun updateOtherUserId(userId: String)
    suspend fun initWebrtc(): Nothing
    
    
    suspend fun makeCall(userId: String, calleeId: String)
    suspend fun answerCall()
    
   suspend fun rejectCall(navigator: Navigator, userId: String): Boolean
//    fun rejectCallAnswer()
    
    fun clearData()
    fun setIsIncomingCall(isIncomingCallValue: Boolean)
    
    
}
