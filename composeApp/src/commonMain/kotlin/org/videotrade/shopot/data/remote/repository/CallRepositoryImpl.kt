package org.videotrade.shopot.data.remote.repository

import androidx.compose.runtime.mutableStateOf
import co.touchlab.kermit.Logger
import com.shepeliev.webrtckmp.*
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.http.HttpMethod
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.koin.core.component.KoinComponent
import org.videotrade.shopot.domain.repository.CallRepository
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
    
    private val _inCommingCall = MutableStateFlow(false)
    override val inCommingCall: StateFlow<Boolean> get() = _inCommingCall
    
    override suspend fun reconnectPeerConnection() {
        // Переподключение PeerConnection
        _peerConnection.value = PeerConnection(rtcConfiguration)
    }
    
    override suspend fun connectionWs(userId: String) {
        val httpClient = HttpClient {
            install(WebSockets)
        }
        
        if (!isConnected.value) {
            try {
                httpClient.webSocket(
                    method = HttpMethod.Get,
                    host = "videotradedev.ru",
                    port = 3006,
                    path = "/ws?callerId=${callerId.value}",
                    
                    
                    
//                    host = "192.168.31.223",
//                    port = 3001,
//                    path = "/message",
                
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
                                        
                                        println("sadada")
                                        
                                        rtcMessage?.let {
                                            val sdp = it["sdp"]?.jsonPrimitive?.content ?: return@launch
                                            val callerId = jsonElement.jsonObject["callerId"]?.jsonPrimitive?.content
                                            callerId?.let { otherUserId.value = it }
                                            
                                            val offer = SessionDescription(
                                                SessionDescriptionType.Offer,
                                                sdp
                                            )
                                            _peerConnection.value.setRemoteDescription(offer)
                                            _inCommingCall.value = true
                                        }
                                    }
                                    
                                    "callAnswered" -> {
                                        rtcMessage?.let {
                                            val sdp = it["sdp"]?.jsonPrimitive?.content ?: return@launch
                                            val answer = SessionDescription(
                                                SessionDescriptionType.Answer,
                                                sdp
                                            )
                                            Logger.d("rtcMessage31313 $answer")
                                            _peerConnection.value.setRemoteDescription(answer)
                                        }
                                    }
                                    
                                    "ICEcandidate" -> {
                                        rtcMessage?.let {
                                            Logger.d("ICEcandidate313131 $rtcMessage")
                                            val jsonElement = Json.parseToJsonElement(rtcMessage.toString())
                                            val label = jsonElement.jsonObject["label"]?.jsonPrimitive?.int
                                            val id = jsonElement.jsonObject["id"]?.jsonPrimitive?.content
                                            val candidate = jsonElement.jsonObject["candidate"]?.jsonPrimitive?.content
                                            
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
    
    override suspend fun getWsSession(): DefaultClientWebSocketSession? {
        return wsSession.value
    }
    
    override suspend fun getPeerConnection(): PeerConnection {
        return peerConnection.value
    }
    
    override  fun getOtherUserId(): String {
        return otherUserId.value
    }
    
    
    override  fun getCallerId(): String {
        return callerId.value
    }
    
    
    override fun updateOtherUserId(userId: String){
        
         otherUserId.value = userId
    }

}
