package org.videotrade.shopot.domain.usecase

import com.shepeliev.webrtckmp.AudioStreamTrack
import com.shepeliev.webrtckmp.IceConnectionState
import com.shepeliev.webrtckmp.MediaStream
import com.shepeliev.webrtckmp.PeerConnection
import com.shepeliev.webrtckmp.PeerConnectionState
import com.shepeliev.webrtckmp.SessionDescription
import com.shepeliev.webrtckmp.VideoStreamTrack
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.domain.repository.CallRepository

class CallUseCase : KoinComponent {
    private val repository: CallRepository by inject()
    val wsSession: StateFlow<DefaultClientWebSocketSession?> get() = repository.wsSession
    
    val peerConnection: StateFlow<PeerConnection?> get() = repository.peerConnection
    val isConnectedWebrtc: StateFlow<Boolean> get() = repository.isConnectedWebrtc
    val isConnectedWs: StateFlow<Boolean> get() = repository.isConnectedWs
    val isCallBackground: StateFlow<Boolean> get() = repository.isCallBackground
    val isIncomingCall: StateFlow<Boolean> get() = repository.isIncomingCall
    val isCallActive: StateFlow<Boolean> get() = repository.isCallActive
    val localStream: StateFlow<MediaStream?> get() = repository.localStream
    val remoteVideoTrack: StateFlow<VideoStreamTrack?> get() = repository.remoteVideoTrack
    val remoteAudioTrack: StateFlow<AudioStreamTrack?> get() = repository.remoteAudioTrack
    val callState: StateFlow<PeerConnectionState> get() = repository.callState
    val iseState: StateFlow<IceConnectionState> get() = repository.iseState
    
    
    suspend fun reconnectPeerConnection() {
        return repository.reconnectPeerConnection()
    }
    
    suspend fun connectionWs(userId: String) {
        
        return repository.connectionWs(userId)
    }
    
    
     fun setOffer(sessionDescription: SessionDescription) {
        return repository.setOffer(sessionDescription)
    }
    fun resetWebRTC() {
        return repository.resetWebRTC()
    }
    
    suspend fun getWsSession(): DefaultClientWebSocketSession? {
        return repository.getWsSession()
    }
    
    suspend fun getPeerConnection(): PeerConnection? {
        return repository.getPeerConnection()
    }
    
    fun getOtherUserId(): String {
        return repository.getOtherUserId()
    }
    
    fun getCallerId(): String {
        return repository.getCallerId()
    }
    
    fun setMicro() {
        return repository.setMicro()
    }
    
    
    fun updateOtherUserId(userId: String) {
        return repository.updateOtherUserId(userId)
    }
    
    
    suspend fun initWebrtc(): Nothing {
        repository.initWebrtc()
    }
    
    
    suspend fun makeCall(userId: String, calleeId: String) {
        repository.makeCall(userId, calleeId)
    }
    
    suspend fun makeCallBackground(notificToken: String, calleeId: String) {
        repository.makeCallBackground(notificToken, calleeId)
    }
    
    
    suspend fun answerCall() {
        repository.answerCall()
    }
    
    
    fun answerCallBackground() {
        repository.answerCallBackground()
    }
    
    
    suspend fun rejectCall(calleeId: String, duration: String): Boolean {
        return repository.rejectCall(calleeId, duration )
    }

    
    fun rejectCallAnswer() {
//        repository.rejectCallAnswer()
    }
    
    fun clearData() {
        repository.clearData()
    }
    
    fun setIsIncomingCall(isIncomingCallValue: Boolean) {
        repository.setIsIncomingCall(isIncomingCallValue)
    }
    
    fun setIsCallBackground(isCallBackground: Boolean) {
        return repository.setIsCallBackground(isCallBackground)
    }
    
    fun setIsCallActive(isCallActive: Boolean) {
        return repository.setIsCallActive(isCallActive)
    }
    
    fun setOtherUserId(newOtherUserId: String) {
        return repository.setOtherUserId(newOtherUserId)
    }

    fun setChatId(chatId: String) {
        return repository.setChatId(chatId)
    }

    fun setCalleeId(calleeId: String) {
        return repository.setCalleeId(calleeId)
    }
    
    fun setCalleeUserInfo(calleeUserInfo: ProfileDTO) {
        return repository.setCalleeUserInfo(calleeUserInfo)
    }
    

    
}
