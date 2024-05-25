package org.videotrade.shopot.presentation.screens.call

import co.touchlab.kermit.Logger
import com.shepeliev.webrtckmp.*
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.websocket.Frame
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
import org.videotrade.shopot.domain.usecase.CallUseCase
import org.videotrade.shopot.multiplatform.DeviceIdProviderFactory
import org.videotrade.shopot.presentation.components.Main.WebRTCMessage
import org.videotrade.shopot.presentation.components.Main.rtcMessageDTO

class CallViewModel() : ViewModel(), KoinComponent {
    private val callUseCase: CallUseCase by inject()
    
    private val _wsSession = MutableStateFlow<DefaultClientWebSocketSession?>(null)
    val wsSession: StateFlow<DefaultClientWebSocketSession?> get() = _wsSession.asStateFlow()

    
//    val peerConnection = MutableStateFlow<PeerConnection?>(null)
    
    private val _inCommingCall = MutableStateFlow(false)
    val inCommingCall: StateFlow<Boolean> get() = _inCommingCall
    
    
    val peerConnection: StateFlow<PeerConnection> get() = callUseCase.peerConnection
    
    private fun showDeviceId(): String? {
        val deviceIdProvider = DeviceIdProviderFactory.create()
        return deviceIdProvider.getDeviceId()
    }
    
    init {
        viewModelScope.launch {
            observeInCommingCall()
            observeWsConnection()
            
            showDeviceId()?.let {
                println("Device ID: $it")
                callUseCase.connectionWs(it)
            }
            
        }
    }
    
    private fun observeInCommingCall() {
        callUseCase.inCommingCall
            .onEach { isIncoming ->
                _inCommingCall.value = isIncoming
                if (isIncoming) {
                    Logger.d { "Incoming call detected" }
                    // Handle incoming call
                }
            }
            .launchIn(viewModelScope)
    }
    
    private fun observeWsConnection() {
        callUseCase.wsSession
            .onEach { wsSessionNew ->
                
                println("wsSessionNew $wsSessionNew")
                _wsSession.value = wsSessionNew
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
    
    
    
    suspend fun Call(
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
                Logger.d { "PC2213131:${getOtherUserId()}" }
                
                val iceCandidateMessage = WebRTCMessage(
                    type = "ICEcandidate",
                    calleeId = getOtherUserId(),
                    iceMessage = rtcMessageDTO(
                        label = candidate.sdpMLineIndex,
                        id = candidate.sdpMid,
                        candidate = candidate.candidate,
                    ),
                )
                
                val jsonMessage = Json.encodeToString(WebRTCMessage.serializer(), iceCandidateMessage)
                
                try {
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
}
