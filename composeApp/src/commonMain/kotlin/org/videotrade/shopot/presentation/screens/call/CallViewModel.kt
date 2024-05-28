package org.videotrade.shopot.presentation.screens.call

import co.touchlab.kermit.Logger
import com.shepeliev.webrtckmp.MediaStream
import com.shepeliev.webrtckmp.MediaStreamTrackKind
import com.shepeliev.webrtckmp.OfferAnswerOptions
import com.shepeliev.webrtckmp.PeerConnection
import com.shepeliev.webrtckmp.SignalingState
import com.shepeliev.webrtckmp.VideoStreamTrack
import com.shepeliev.webrtckmp.onConnectionStateChange
import com.shepeliev.webrtckmp.onIceCandidate
import com.shepeliev.webrtckmp.onIceConnectionStateChange
import com.shepeliev.webrtckmp.onSignalingStateChange
import com.shepeliev.webrtckmp.onTrack
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.websocket.Frame
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.coroutineScope
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
import org.videotrade.shopot.domain.model.rtcMessageDTO
import org.videotrade.shopot.domain.usecase.CallUseCase

class CallViewModel() : ViewModel(), KoinComponent {
    private val callUseCase: CallUseCase by inject()
    
    private val _wsSession = MutableStateFlow<DefaultClientWebSocketSession?>(null)
    val wsSession: StateFlow<DefaultClientWebSocketSession?> get() = _wsSession.asStateFlow()
    
    
    private val _inCommingCall = MutableStateFlow(false)
    val inCommingCall: StateFlow<Boolean> get() = _inCommingCall
    
    
    val peerConnection: StateFlow<PeerConnection> get() = callUseCase.peerConnection
    
    
    init {
        viewModelScope.launch {
            observeWsConnection()
        }
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


//    private fun getPeerConnection() {
//        viewModelScope.launch {
//            _peerConnection.value = callUseCase.getPeerConnection()
//        }
//    }
    
    fun getOtherUserId(): String {
        return callUseCase.getOtherUserId()
    }
    
    fun getCallerId(): String {
        return callUseCase.getCallerId()
    }
    
    
    suspend fun initWebrtc(
        webSocketSession: StateFlow<DefaultClientWebSocketSession?>,
        peerConnection: PeerConnection,
        localStream: MediaStream,
        setRemoteVideoTrack: (VideoStreamTrack?) -> Unit,
    ): Nothing = coroutineScope {
        localStream.tracks.forEach { track ->
            peerConnection.addTrack(track, localStream)
        }
        
        // Обработка кандидатов ICE
        peerConnection.onIceCandidate
            .onEach { candidate ->
                
                val iceCandidateMessage = WebRTCMessage(
                    type = "ICEcandidate",
                    calleeId = getOtherUserId(),
                    iceMessage = rtcMessageDTO(
                        label = candidate.sdpMLineIndex,
                        id = candidate.sdpMid,
                        candidate = candidate.candidate,
                    ),
                )
                
                
                val jsonMessage =
                    Json.encodeToString(WebRTCMessage.serializer(), iceCandidateMessage)
                
                try {
                    
                    Logger.d { "PC2213131:${jsonMessage}" }
                    
                    webSocketSession.value?.send(Frame.Text(jsonMessage))
                    println("Message sent successfully")
                } catch (e: Exception) {
                    println("Failed to send message: ${e.message}")
                }
                
                peerConnection.addIceCandidate(candidate)
            }
            .launchIn(this)
        
        // Следим за изменениями состояния сигнализации
        peerConnection.onSignalingStateChange
            .onEach { signalingState ->
                Logger.d { "PC1 onSignalingStateChange: $signalingState" }
                
                if (signalingState == SignalingState.HaveRemoteOffer) {
                    Logger.d { " peer2 signalingState: $signalingState" }
                }
            }
            .launchIn(this)
        
        // Следим за изменениями состояния соединения ICE
        peerConnection.onIceConnectionStateChange
            .onEach { state ->
                Logger.d { "PC1 onIceConnectionStateChange: $state" }
            }
            .launchIn(this)
        
        // Следим за изменениями общего состояния соединения
        peerConnection.onConnectionStateChange
            .onEach { state ->
                Logger.d { "PC1 onConnectionStateChange: $state" }
            }
            .launchIn(this)
        
        // Обработка треков, получаемых от удалённого пира
        peerConnection.onTrack
            .onEach { event ->
                Logger.d { "onTrack: $  ${event.track} ${event.streams} ${event.receiver} ${event.transceiver}" }
                if (event.track?.kind == MediaStreamTrackKind.Video) {
                    setRemoteVideoTrack(event.track as VideoStreamTrack)
                }
            }
            .launchIn(this)
        
        awaitCancellation()  // Поддерживаем корутину активной
    }
    
    
    @OptIn(DelicateCoroutinesApi::class)
    fun makeCall(userId: String) {
        viewModelScope.launch {
            if (wsSession.value != null) {
                val offer = peerConnection.value.createOffer(
                    OfferAnswerOptions(
                        offerToReceiveVideo = true,
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
                    
                    val otherUserId = getOtherUserId()
                    
                    
                    val answer = peerConnection.value.createAnswer(
                        options = OfferAnswerOptions(
                            offerToReceiveVideo = true,
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
                    
                    println("answerCallMessage $answerCallMessage")
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
}
