package org.videotrade.shopot.presentation.screens.call

import androidx.compose.runtime.collectAsState
import co.touchlab.kermit.Logger
import com.shepeliev.webrtckmp.MediaStream
import com.shepeliev.webrtckmp.OfferAnswerOptions
import com.shepeliev.webrtckmp.PeerConnection
import com.shepeliev.webrtckmp.PeerConnectionState
import com.shepeliev.webrtckmp.VideoStreamTrack
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.websocket.Frame
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.videotrade.shopot.domain.model.SessionDescriptionDTO
import org.videotrade.shopot.domain.model.WebRTCMessage
import org.videotrade.shopot.domain.usecase.CallUseCase

class CallViewModel() : ViewModel(), KoinComponent {
    private val callUseCase: CallUseCase by inject()
    
    private val _wsSession = MutableStateFlow<DefaultClientWebSocketSession?>(null)
    val wsSession: StateFlow<DefaultClientWebSocketSession?> get() = _wsSession.asStateFlow()
    
    
    val peerConnection: StateFlow<PeerConnection> get() = callUseCase.peerConnection
    
    private val _isConnectedWebrtc = MutableStateFlow(false)
    val isConnectedWebrtc: StateFlow<Boolean> get() = _isConnectedWebrtc
    
    
    private val _localStream = MutableStateFlow<MediaStream?>(null)
    val localStream: StateFlow<MediaStream?> get() = _localStream
    
    
    private val _remoteVideoTrack = MutableStateFlow<VideoStreamTrack?>(null)
    val remoteVideoTrack: StateFlow<VideoStreamTrack?> get() = _remoteVideoTrack
    
    
    private val _callState = MutableStateFlow(PeerConnectionState.New)
    val callState: StateFlow<PeerConnectionState> get() = _callState
    
    
    init {
        viewModelScope.launch {
            
            observeCallState()
            observeWsConnection()
            observeIsConnectedWebrtc()
            observeStreems()
        }
    }
    
    
    private fun observeCallState() {
        
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
    
    
    fun initWebrtc() {
        viewModelScope.launch {
            callUseCase.initWebrtc()
            
        }
    }
    
    @OptIn(DelicateCoroutinesApi::class)
    fun makeCall(userId: String) {
        viewModelScope.launch {
            if (wsSession.value != null) {
                val offer = peerConnection.value.createOffer(
                    OfferAnswerOptions(
//                        offerToReceiveVideo = true,
                        offerToReceiveAudio = true
                    )
                )
                peerConnection.value.setLocalDescription(offer)
                
                
                if (wsSession.value?.outgoing?.isClosedForSend == true) {
                    return@launch
                }
                
                val newCallMessage = WebRTCMessage(
                    type = "call",
                    calleeId = userId,
                    rtcMessage = SessionDescriptionDTO(offer.type, offer.sdp)
                )
                
                val jsonMessage = Json.encodeToString(WebRTCMessage.serializer(), newCallMessage)
                
                try {
                    wsSession.value?.send(Frame.Text(jsonMessage))
                    println("Message sent successfully")
                } catch (e: Exception) {
                    println("Failed to send message: ${e.message}")
                }
            }
        }
        
    }
    
    
    @OptIn(DelicateCoroutinesApi::class)
    fun answerCall() {
        viewModelScope.launch {
            if (wsSession.value != null) {
                
                try {
                    
                    callUseCase.setOffer()
                    
                    
                    val otherUserId = getOtherUserId()
                    
                    
                    val answer = peerConnection.value.createAnswer(
                        options = OfferAnswerOptions(
//                            offerToReceiveVideo = true,
                            offerToReceiveAudio = true
                        )
                    )
                    
                    peerConnection.value.setLocalDescription(answer)
                    
                    
                    if (wsSession.value?.outgoing?.isClosedForSend == true) {
                        return@launch
                    }
                    
                    val answerCallMessage = WebRTCMessage(
                        type = "answerCall",
                        callerId = otherUserId,
                        rtcMessage = SessionDescriptionDTO(answer.type, answer.sdp)
                    )
                    
                    Logger.d {
                        "answerCallMessage $answerCallMessage"
                    }
                    val jsonMessage =
                        Json.encodeToString(WebRTCMessage.serializer(), answerCallMessage)
                    
                    
                    wsSession.value?.send(Frame.Text(jsonMessage))
                    println("Message sent successfully")
                } catch (e: Exception) {
                    println("Failed to send message: ${e.message}")
                }
            }
        }
        
    }
    
    fun rejectCall() {
        viewModelScope.launch {
            callUseCase.rejectCall()
        }
    }
    
}
