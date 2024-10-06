package org.videotrade.shopot.domain.usecase

import cafe.adriel.voyager.navigator.Navigator
import com.shepeliev.webrtckmp.IceConnectionState
import com.shepeliev.webrtckmp.MediaStream
import com.shepeliev.webrtckmp.PeerConnection
import com.shepeliev.webrtckmp.PeerConnectionState
import com.shepeliev.webrtckmp.VideoStreamTrack
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.videotrade.shopot.domain.repository.CallRepository

class CallUseCase : KoinComponent {
    private val repository: CallRepository by inject()
    val wsSession: StateFlow<DefaultClientWebSocketSession?> get() = repository.wsSession
    
    val peerConnection: StateFlow<PeerConnection?> get() = repository.peerConnection
    val isConnectedWebrtc: StateFlow<Boolean> get() = repository.isConnectedWebrtc
    val localStream: StateFlow<MediaStream?> get() = repository.localStream
    val remoteVideoTrack: StateFlow<VideoStreamTrack?> get() = repository.remoteVideoTrack
    val callState: StateFlow<PeerConnectionState> get() = repository.callState
    val iseState: StateFlow<IceConnectionState> get() = repository.iseState
    
    
    suspend fun reconnectPeerConnection() {
        return repository.reconnectPeerConnection()
    }
    
    suspend fun connectionWs(userId: String, navigator: Navigator) {
        
        return repository.connectionWs(userId, navigator)
    }

    suspend fun connectionBackgroundWs(userId: String) {

        return repository.connectionBackgroundWs(userId)
    }
    
    suspend fun setOffer() {
        
        return repository.setOffer()
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
    
    
    suspend fun makeCall(userId: String, calleeId: String){
        repository.makeCall(userId, calleeId)
    }

    suspend fun makeCallBackground(notificToken: String, calleeId: String){
        repository.makeCallBackground(notificToken, calleeId)
    }


    
    suspend fun answerCall() {
        repository.answerCall()
    }


     fun answerCallBackground() {
        repository.answerCallBackground()
    }
    
    
    suspend fun rejectCall(navigator: Navigator, userId: String): Boolean {
        return repository.rejectCall(navigator, userId)
    }
    
    suspend  fun rejectCallBackground(userId: String): Boolean {
        return   repository.rejectCallBackground(userId)
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
    
    
}
