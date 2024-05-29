package org.videotrade.shopot.presentation.screens.call

import com.shepeliev.webrtckmp.IceConnectionState
import com.shepeliev.webrtckmp.MediaStream
import com.shepeliev.webrtckmp.PeerConnection
import com.shepeliev.webrtckmp.PeerConnectionState
import com.shepeliev.webrtckmp.VideoStreamTrack
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.videotrade.shopot.domain.usecase.CallUseCase

class CallViewModel() : ViewModel(), KoinComponent {
    private val callUseCase: CallUseCase by inject()
    
    private val _wsSession = MutableStateFlow<DefaultClientWebSocketSession?>(null)
    val wsSession: StateFlow<DefaultClientWebSocketSession?> get() = _wsSession.asStateFlow()
    
    private val _isConnectedWebrtc = MutableStateFlow(false)
    val isConnectedWebrtc: StateFlow<Boolean> get() = _isConnectedWebrtc
    
    
    private val _localStream = MutableStateFlow<MediaStream?>(null)
    val localStream: StateFlow<MediaStream?> get() = _localStream
    
    
    private val _remoteVideoTrack = MutableStateFlow<VideoStreamTrack?>(null)
    val remoteVideoTrack: StateFlow<VideoStreamTrack?> get() = _remoteVideoTrack
    
    
    private val _callState = MutableStateFlow(PeerConnectionState.New)
    val callState: StateFlow<PeerConnectionState> get() = _callState
    
    
    private val _iceState = MutableStateFlow(IceConnectionState.New)
    val iceState: StateFlow<IceConnectionState> get() = _iceState
    
    
    init {
        viewModelScope.launch {
            
            println("callStateNew22 ${callUseCase.peerConnection}")
            
            
            observeCallStates()
            observeWsConnection()
            observeIsConnectedWebrtc()
//            observeStreems()
        }
    }
    
    
    private fun observeCallStates() {
        
        callUseCase.iseState
            .onEach { iseStateNew ->
                
                
                _iceState.value = iseStateNew
                
                println("iseStateNew $iseStateNew")
                
            }
            .launchIn(viewModelScope)
        
        callUseCase.callState
            .onEach { callStateNew ->
                
                _callState.value = callStateNew
                
                println("callStateNew $callStateNew")
                
            }
            .launchIn(viewModelScope)
    }
    
    
    private fun observeStreems() {
        callUseCase.localStream
            .onEach { localStreamNew ->
                
                _localStream.value = localStreamNew
                
                println("_localStream $_localStream")
                
            }
            .launchIn(viewModelScope)
        
        callUseCase.remoteVideoTrack
            .onEach { remoteVideoTrackNew ->
                
                _remoteVideoTrack.value = remoteVideoTrackNew
                
                println("remoteVideoTrackNew $remoteVideoTrackNew")
                
            }
            .launchIn(viewModelScope)
    }
    
    private fun observeIsConnectedWebrtc() {
        callUseCase.isConnectedWebrtc
            .onEach { isConnectedWebrtcNew ->
                
                _isConnectedWebrtc.value = isConnectedWebrtcNew
                
                println("isConnectedWebrtcNew $isConnectedWebrtcNew")
                
            }
            .launchIn(viewModelScope)
    }
    
    
    private fun observeWsConnection() {
        callUseCase.wsSession
            .onEach { wsSessionNew ->
                
                _wsSession.value = wsSessionNew
                
                println("wsSessionNew1111 $wsSessionNew")
                
            }
            .launchIn(viewModelScope)
    }
    
    fun getWsSession() {
        viewModelScope.launch {
            println("dsadada ${callUseCase.getWsSession()}")
        }
    }
    
    
    fun updateOtherUserId(userId: String) {
        viewModelScope.launch {
            callUseCase.updateOtherUserId(userId)
        }
    }
    
    
    private fun getOtherUserId(): String {
        return callUseCase.getOtherUserId()
    }
    
    fun getCallerId(): String {
        return callUseCase.getCallerId()
    }
    
    
    fun reconnectPeerConnection() {
        viewModelScope.launch {
            callUseCase.reconnectPeerConnection()
        }
    }
    
    fun initWebrtc() {
        viewModelScope.launch {
            callUseCase.initWebrtc()
        }
    }
    
    @OptIn(DelicateCoroutinesApi::class)
    suspend fun makeCall(userId: String) {
        callUseCase.makeCall(userId)
    }
    
    @OptIn(DelicateCoroutinesApi::class)
    suspend fun answerCall() {
        callUseCase.answerCall()
    }
    
    
    fun rejectCall() {
        viewModelScope.launch {
            
            _callState.value = PeerConnectionState.New
            _iceState.value = IceConnectionState.New
            callUseCase.rejectCall()
        }
    }
    
    fun rejectCallAnswer() {
        viewModelScope.launch {
            callUseCase.rejectCallAnswer()
        }
    }
    
    
}
