package org.videotrade.shopot.data.remote.repository

import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.navigator.Navigator
import co.touchlab.kermit.Logger
import com.shepeliev.webrtckmp.IceCandidate
import com.shepeliev.webrtckmp.IceServer
import com.shepeliev.webrtckmp.MediaDevices
import com.shepeliev.webrtckmp.MediaStream
import com.shepeliev.webrtckmp.MediaStreamTrackKind
import com.shepeliev.webrtckmp.PeerConnection
import com.shepeliev.webrtckmp.PeerConnectionState
import com.shepeliev.webrtckmp.RtcConfiguration
import com.shepeliev.webrtckmp.SessionDescription
import com.shepeliev.webrtckmp.SessionDescriptionType
import com.shepeliev.webrtckmp.SignalingState
import com.shepeliev.webrtckmp.VideoStreamTrack
import com.shepeliev.webrtckmp.onConnectionStateChange
import com.shepeliev.webrtckmp.onIceCandidate
import com.shepeliev.webrtckmp.onIceConnectionStateChange
import com.shepeliev.webrtckmp.onSignalingStateChange
import com.shepeliev.webrtckmp.onTrack
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.http.HttpMethod
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.koin.core.component.KoinComponent
import org.videotrade.shopot.api.EnvironmentConfig.webSocketsUrl
import org.videotrade.shopot.domain.model.WebRTCMessage
import org.videotrade.shopot.domain.model.rtcMessageDTO
import org.videotrade.shopot.domain.repository.CallRepository
import org.videotrade.shopot.presentation.screens.call.IncomingCallScreen
import kotlin.random.Random

class CallRepositoryImpl : CallRepository, KoinComponent {
    
    val iceServers = listOf(
        "stun:stun.l.google.com:19302",
        "stun:stun1.l.google.com:19302",
        "stun:stun2.l.google.com:19302",
    )
    
    val turnServers = listOf(
        "turn:89.221.60.156:3478",
    )
    
    // Создание конфигурации для PeerConnection
    val rtcConfiguration = RtcConfiguration(
        iceServers = listOf(
            IceServer(iceServers),
            IceServer(
                urls = turnServers, // URL TURN сервера
                username = "andrew", // Имя пользователя
                password = "kapustin" // Пароль
            )
        )
    )
    
    private val isConnected = mutableStateOf(false)
    
    
    private fun generateRandomNumber(): String {
        return Random.nextInt(1, 41).toString() // верхняя граница исключена, поэтому указываем 11
    }
    
    
    private val otherUserId = mutableStateOf("")
    private val callerId = mutableStateOf(generateRandomNumber())
    
    private val _wsSession = MutableStateFlow<DefaultClientWebSocketSession?>(null)
    override val wsSession: StateFlow<DefaultClientWebSocketSession?> get() = _wsSession
    
    private val _peerConnection = MutableStateFlow(PeerConnection(rtcConfiguration))
    override val peerConnection: StateFlow<PeerConnection> get() = _peerConnection
    
    private val offer = MutableStateFlow<SessionDescription?>(null)
    
    override val localStream = MutableStateFlow<MediaStream?>(null)
    
    override val remoteVideoTrack = MutableStateFlow<VideoStreamTrack?>(null)
    
    
    private val _isConnectedWebrtc = MutableStateFlow(false)
    
    override val isConnectedWebrtc: StateFlow<Boolean> get() = _isConnectedWebrtc
    
    
    private val _callState = MutableStateFlow(PeerConnectionState.New)
    
    override val callState: StateFlow<PeerConnectionState> get() = _callState
    
    
    override suspend fun reconnectPeerConnection() {
        // Переподключение PeerConnection
        _peerConnection.value = PeerConnection(rtcConfiguration)
    }
    
    override suspend fun setOffer() {
        
        Logger.d { "onTrack:1111" }
        
        offer.value?.let { _peerConnection.value.setRemoteDescription(it) }
        
    }
    
    override suspend fun connectionWs(userId: String, navigator: Navigator) {
        val httpClient = HttpClient {
            install(WebSockets)
        }
        
        if (!isConnected.value) {
            try {
                httpClient.webSocket(
                    method = HttpMethod.Get,
                    host = webSocketsUrl,
                    port = 3006,
                    path = "/ws?callerId=${userId}",
                ) {
                    _wsSession.value = this
                    isConnected.value = true
                    
                    
                    val callOutputRoutine = launch {
                        for (frame in incoming) {
                            if (frame is Frame.Text) {
                                val text = frame.readText()
                                val jsonElement = Json.parseToJsonElement(text)
                                val type = jsonElement.jsonObject["type"]?.jsonPrimitive?.content
                                val rtcMessage = jsonElement.jsonObject["rtcMessage"]?.jsonObject
                                
                                when (type) {
                                    "newCall" -> {
                                        rtcMessage?.let {
                                            val sdp =
                                                it["sdp"]?.jsonPrimitive?.content ?: return@launch
                                            val callerId =
                                                jsonElement.jsonObject["callerId"]?.jsonPrimitive?.content
                                            
                                            offer.value = SessionDescription(
                                                SessionDescriptionType.Offer,
                                                sdp
                                            )
                                            
                                            
                                            
                                            callerId?.let { userId ->
                                                
                                                
                                                otherUserId.value = userId
                                                
                                                
                                                
                                                navigator.push(IncomingCallScreen(userId))
                                                
                                            }
                                            
                                            
                                        }
                                    }
                                    
                                    "callAnswered" -> {
                                        rtcMessage?.let {
                                            val sdp =
                                                it["sdp"]?.jsonPrimitive?.content ?: return@launch
                                            val answer = SessionDescription(
                                                SessionDescriptionType.Answer,
                                                sdp
                                            )
                                            _peerConnection.value.setRemoteDescription(answer)
                                        }
                                    }
                                    
                                    "ICEcandidate" -> {
                                        rtcMessage?.let {
                                            Logger.d("ICEcandidate111111 $rtcMessage")
                                            val jsonElement =
                                                Json.parseToJsonElement(rtcMessage.toString())
                                            val label =
                                                jsonElement.jsonObject["label"]?.jsonPrimitive?.int
                                            val id =
                                                jsonElement.jsonObject["id"]?.jsonPrimitive?.content
                                            val candidate =
                                                jsonElement.jsonObject["candidate"]?.jsonPrimitive?.content
                                            
                                            if (candidate != null && id != null && label != null) {
                                                val iceCandidate = IceCandidate(
                                                    candidate = candidate,
                                                    sdpMid = id,
                                                    sdpMLineIndex = label
                                                )
                                                _peerConnection.value.addIceCandidate(iceCandidate)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    callOutputRoutine.join()
                }
            } catch (e: Exception) {
                isConnected.value = false
                println("Ошибка соединения: $e")
            }
        }
    }
    
    
    override suspend fun initWebrtc(): Nothing = coroutineScope {
        val stream = MediaDevices.getUserMedia(audio = true, video = true)
        
        localStream.value = stream
        
        
        
        stream.tracks.forEach { track ->
            peerConnection.value.addTrack(track, localStream.value!!)
        }
        
        
        
        _isConnectedWebrtc.value = true
        // Обработка кандидатов ICE
        peerConnection.value.onIceCandidate
            .onEach { candidate ->
                Logger.d { "PC2213131" }
                
                val iceCandidateMessage = WebRTCMessage(
                    type = "ICEcandidate",
                    calleeId = otherUserId.value,
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
                    
                    wsSession.value?.send(Frame.Text(jsonMessage))
                    println("Message sent successfully")
                } catch (e: Exception) {
                    println("Failed to send message: ${e.message}")
                }
                
                peerConnection.value.addIceCandidate(candidate)
            }
            .launchIn(this)
        
        // Следим за изменениями состояния сигнализации
        peerConnection.value.onSignalingStateChange
            .onEach { signalingState ->
                Logger.d { "peerState111 onSignalingStateChange: $signalingState" }
                
                if (signalingState == SignalingState.HaveRemoteOffer) {
                    Logger.d { " peer2 signalingState: $signalingState" }
                }
            }
            .launchIn(this)
        
        // Следим за изменениями состояния соединения ICE
        peerConnection.value.onIceConnectionStateChange
            .onEach { state ->
                Logger.d { "peerState111 onIceConnectionStateChange: $state" }
            }
            .launchIn(this)
        
        // Следим за изменениями общего состояния соединения
        peerConnection.value.onConnectionStateChange
            .onEach { state ->
                Logger.d { "peerState111 onConnectionStateChange: $state" }
                
                
                _callState.value = state
                
            }
            .launchIn(this)
        
        
        // Обработка треков, получаемых от удалённого пира
        peerConnection.value.onTrack
            .onEach { event ->
                Logger.d { "onTrack: $  ${event.track} ${event.streams} ${event.receiver} ${event.transceiver}" }
                if (event.track?.kind == MediaStreamTrackKind.Video) {
                    remoteVideoTrack.value = event.track as VideoStreamTrack
                }
            }
            .launchIn(this)
        
        
        
        awaitCancellation()  // Поддерживаем корутину активной
    }
    
    override suspend fun getWsSession(): DefaultClientWebSocketSession? {
        return wsSession.value
    }
    
    override suspend fun getPeerConnection(): PeerConnection {
        return peerConnection.value
    }
    
    override fun getOtherUserId(): String {
        return otherUserId.value
    }
    
    
    override fun getCallerId(): String {
        return callerId.value
    }
    
    
    override fun updateOtherUserId(userId: String) {
        
        otherUserId.value = userId
    }
    
    override fun rejectCall() {
        // Закрываем PeerConnection
        _peerConnection.value.close()
        // Освобождаем все треки в локальных стримах
        localStream.value?.let { stream ->
            stream.tracks.forEach { track ->
                track.stop()
            }
        }

        localStream.value = null
        remoteVideoTrack.value = null
        _isConnectedWebrtc.value = false
    }

    
}
