package org.videotrade.shopot.domain.repository

import com.shepeliev.webrtckmp.AudioStreamTrack
import com.shepeliev.webrtckmp.IceConnectionState
import com.shepeliev.webrtckmp.MediaStream
import com.shepeliev.webrtckmp.PeerConnection
import com.shepeliev.webrtckmp.PeerConnectionState
import com.shepeliev.webrtckmp.SessionDescription
import com.shepeliev.webrtckmp.VideoStreamTrack
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import kotlinx.coroutines.flow.StateFlow

interface CallRepository {
    
    
    val wsSession: StateFlow<DefaultClientWebSocketSession?>
    
    val peerConnection: StateFlow<PeerConnection?>
    
    val isConnectedWebrtc: StateFlow<Boolean>
    
    val isConnectedWs: StateFlow<Boolean>
    
    val isIncomingCall: StateFlow<Boolean>
    
    val isCallActive: StateFlow<Boolean>
    
    val isCallBackground: StateFlow<Boolean>
    
    val localStream: StateFlow<MediaStream?>
    
    val remoteVideoTrack: StateFlow<VideoStreamTrack?>
    
    val remoteAudioTrack: StateFlow<AudioStreamTrack?>
    
    val callState: StateFlow<PeerConnectionState>
    
    val iseState: StateFlow<IceConnectionState>
    
    
    suspend fun connectionWs(userId: String)
    
    suspend fun disconnectWs()
    
    suspend fun reconnectPeerConnection()
    
    
    suspend fun getWsSession(): DefaultClientWebSocketSession?
    suspend fun getPeerConnection(): PeerConnection?
    
    
    fun getCallerId(): String
    fun setMicro()
    
    fun getOtherUserId(): String
    
    
    fun updateOtherUserId(userId: String)
    suspend fun initWebrtc(): Nothing
    
    
    suspend fun makeCall(userId: String, calleeId: String)
    
    suspend fun makeCallBackground(notificToken: String, calleeId: String)
    
    suspend fun answerCall()
    
    fun answerCallBackground()
    
    suspend fun rejectCall(calleeId: String, duration: String): Boolean
    
    fun clearData()
    
    fun setIsIncomingCall(isIncomingCallValue: Boolean)
    
    fun setIsCallBackground(isCallBackground: Boolean)
    
    fun setIsCallActive(isCallActive: Boolean)
    
    fun setOtherUserId(newOtherUserId: String)
    
    fun setChatId(chatId: String)
    
    fun setCalleeId(calleeId: String)
    
    fun setOffer(sessionDescription: SessionDescription)
    
    fun resetWebRTC()
    
    
}
