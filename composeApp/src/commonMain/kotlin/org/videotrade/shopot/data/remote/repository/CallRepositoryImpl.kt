package org.videotrade.shopot.data.remote.repository

import androidx.compose.runtime.mutableStateOf
import co.touchlab.kermit.Logger
import com.shepeliev.webrtckmp.AudioStreamTrack
import com.shepeliev.webrtckmp.IceCandidate
import com.shepeliev.webrtckmp.IceConnectionState
import com.shepeliev.webrtckmp.IceServer
import com.shepeliev.webrtckmp.IceTransportPolicy
import com.shepeliev.webrtckmp.MediaDevices
import com.shepeliev.webrtckmp.MediaStream
import com.shepeliev.webrtckmp.MediaStreamTrackKind
import com.shepeliev.webrtckmp.OfferAnswerOptions
import com.shepeliev.webrtckmp.PeerConnection
import com.shepeliev.webrtckmp.PeerConnectionState
import com.shepeliev.webrtckmp.RtcConfiguration
import com.shepeliev.webrtckmp.SessionDescription
import com.shepeliev.webrtckmp.SessionDescriptionType
import com.shepeliev.webrtckmp.SignalingState
import com.shepeliev.webrtckmp.VideoStreamTrack
import com.shepeliev.webrtckmp.audioTracks
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
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.mp.KoinPlatform
import org.videotrade.shopot.api.EnvironmentConfig.WEB_SOCKETS_URL
import org.videotrade.shopot.api.findContactByPhone
import org.videotrade.shopot.api.getValueInStorage
import org.videotrade.shopot.api.handleConnectWebSocket
import org.videotrade.shopot.data.origin
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.domain.model.SessionDescriptionDTO
import org.videotrade.shopot.domain.model.WebRTCMessage
import org.videotrade.shopot.domain.model.rtcMessageDTO
import org.videotrade.shopot.domain.repository.CallRepository
import org.videotrade.shopot.domain.usecase.ContactsUseCase
import org.videotrade.shopot.domain.usecase.WsUseCase
import org.videotrade.shopot.multiplatform.PermissionsProviderFactory
import org.videotrade.shopot.multiplatform.Platform
import org.videotrade.shopot.multiplatform.SwiftFuncsClass
import org.videotrade.shopot.multiplatform.clearNotificationsForChannel
import org.videotrade.shopot.multiplatform.closeApp
import org.videotrade.shopot.multiplatform.configureAudioSession
import org.videotrade.shopot.multiplatform.getPlatform
import org.videotrade.shopot.multiplatform.isScreenOn
import org.videotrade.shopot.presentation.screens.call.CallScreen
import org.videotrade.shopot.presentation.screens.call.CallViewModel
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.main.MainScreen
import org.videotrade.shopot.presentation.screens.profile.ProfileViewModel
import kotlin.random.Random

class CallRepositoryImpl : CallRepository, KoinComponent {
    
    private val commonViewModel: CommonViewModel = KoinPlatform.getKoin().get()
    
    private val stunServers = listOf(
        "stun:89.221.60.157:3478",
        
        )
    
    private val iceServers = listOf(
        IceServer(stunServers),
        IceServer(
            urls = listOf("turn:89.221.60.157:3478"),
            username = "andrew",
            password = "kapustin",
        )
    )
    
    // Создание конфигурации для PeerConnection
    private val rtcConfiguration = RtcConfiguration(
        iceServers = iceServers,
        iceTransportPolicy = IceTransportPolicy.NoHost,
    )

//    private val rtcConfiguration = RtcConfiguration(
//        bundlePolicy = BundlePolicy.Balanced,
//        certificates = null,  // если не требуется специальная конфигурация
//        iceCandidatePoolSize = 100,  // или другое значение для предзагрузки
//        iceServers = listOf(
//            IceServer(
//                urls = listOf("turn:89.221.60.157:3478"),
//                username = "andrew",
//                password = "kapustin"
//            )
//        ),
//        iceTransportPolicy = IceTransportPolicy.Relay,  // использовать Relay для TURN
//        rtcpMuxPolicy = RtcpMuxPolicy.Require
//    )
    
    private val _peerConnection =
        MutableStateFlow<PeerConnection?>(PeerConnection(rtcConfiguration))
    
    override val peerConnection: StateFlow<PeerConnection?> get() = _peerConnection
    
    
    private fun generateRandomNumber(): String {
        return Random.nextInt(1, 41).toString() // верхняя граница исключена, поэтому указываем 11
    }
    
    
    private val otherUserId = mutableStateOf("")
    
    
    private val isCall = mutableStateOf(false)
    
    private var isCallRejected = false
    
    private val _isCallActive = MutableStateFlow(false)
    override val isCallActive: StateFlow<Boolean> get() = _isCallActive
    
    
    private val _isIncomingCall = MutableStateFlow(false)
    override val isIncomingCall: StateFlow<Boolean> get() = _isIncomingCall
    
    private val _isCallBackground = MutableStateFlow(false)
    override val isCallBackground: StateFlow<Boolean> get() = _isCallBackground
    
    private val callerId = mutableStateOf(generateRandomNumber())
    
    private val _wsSession = MutableStateFlow<DefaultClientWebSocketSession?>(null)
    override val wsSession: StateFlow<DefaultClientWebSocketSession?> get() = _wsSession
    
    private val _calleeId = MutableStateFlow("")
    
    private val calleeId: StateFlow<String> get() = _calleeId
    
    
    private val calleeUser = MutableStateFlow(ProfileDTO())
    
    
    private val _chatId = MutableStateFlow("")
    val chatId: StateFlow<String> get() = _chatId
    
    
    private val offer = MutableStateFlow<SessionDescription?>(null)
    
    override val localStream = MutableStateFlow<MediaStream?>(null)
    
    override val remoteVideoTrack = MutableStateFlow<VideoStreamTrack?>(null)
    
    override val remoteAudioTrack = MutableStateFlow<AudioStreamTrack?>(null)
    
    private val _isConnectedWebrtc = MutableStateFlow(false)
    
    override val isConnectedWebrtc: StateFlow<Boolean> get() = _isConnectedWebrtc
    
    private val _isConnectedWs = MutableStateFlow(false)
    
    override val isConnectedWs: StateFlow<Boolean> get() = _isConnectedWs
    
    private val _callState = MutableStateFlow(PeerConnectionState.New)
    
    override val callState: StateFlow<PeerConnectionState> get() = _callState
    
    
    private val _iceState = MutableStateFlow(IceConnectionState.New)
    
    override val iseState: StateFlow<IceConnectionState> get() = _iceState
    
    private val isMuted = MutableStateFlow(false)
    
    
    override suspend fun reconnectPeerConnection() {
        // Переподключение PeerConnection
        _peerConnection.value = PeerConnection(rtcConfiguration)
    }
    
    override fun setOffer(sessionDescription: SessionDescription) {
        offer.value = sessionDescription
        
    }
    
    suspend fun setRemoteDisc() {
        offer.value?.let { _peerConnection.value?.setRemoteDescription(it) }
        
    }
    
    override suspend fun connectionWs(userId: String) {
        
        println("aaaaaaa11111")
        val httpClient = HttpClient {
            install(WebSockets)
        }
        
        if (!_isConnectedWs.value) {
            try {
                httpClient.webSocket(
                    method = HttpMethod.Get,
                    host = WEB_SOCKETS_URL,
                    port = 3006,
                    path = "/ws?callerId=${userId}",
                ) {
                    _wsSession.value = this
                    _isConnectedWs.value = true
                    println("Connection Call")
                    
                    val callOutputRoutine = launch {
                        for (frame in incoming) {
                            if (frame is Frame.Text) {
                                
                                val text = frame.readText()
                                
                                val jsonElement = Json.parseToJsonElement(text)
                                
                                println("jsonElement1112 $jsonElement")
                                
                                val type = jsonElement.jsonObject["type"]?.jsonPrimitive?.content
                                
                                println("jsonElement1112 $type")
                                
                                val rtcMessage = jsonElement.jsonObject["rtcMessage"]?.jsonObject
                                
                                when (type) {
                                    "newCall" -> {
                                        try {
                                            println("newCall")
                                            
                                            val contactsUseCase: ContactsUseCase by inject()
                                            val callViewModel: CallViewModel by inject()
                                            val navigator = commonViewModel.mainNavigator.value
                                            
                                            val cameraPer = PermissionsProviderFactory.create()
                                                .getPermission("microphone")
                                            
                                            if (cameraPer) {
                                                println("rtcMessage $rtcMessage")
                                                
                                                rtcMessage?.let {
                                                    val userJson =
                                                        jsonElement.jsonObject["user"]?.jsonObject
                                                    
                                                    
                                                    var user =
                                                        Json.decodeFromString<ProfileDTO>(userJson.toString())
                                                    
                                                    
                                                    println("aadauser $user")
                                                    
                                                    val sdp =
                                                        it["sdp"]?.jsonPrimitive?.content
                                                            ?: return@launch
                                                    
                                                    
                                                    val callerId =
                                                        jsonElement.jsonObject["callerId"]?.jsonPrimitive?.content
                                                    
                                                    offer.value = SessionDescription(
                                                        SessionDescriptionType.Offer,
                                                        sdp
                                                    )
                                                    
                                                    
                                                    
                                                    callerId?.let { userId ->
                                                        
                                                        
                                                        otherUserId.value = userId
                                                        
                                                        _isIncomingCall.value = true
                                                        val contact = findContactByPhone(
                                                            user.phone,
                                                            contactsUseCase.contacts.value
                                                        )
                                                        
                                                        if (
                                                            contact !== null && contact.firstName !== null && contact.lastName !== null
                                                        ) {
                                                            user = user.copy(
                                                                firstName = contact.firstName,
                                                                lastName = contact.lastName
                                                            )
                                                        }
                                                        
                                                        callViewModel.callScreenInfo.value =
                                                            CallScreen(
                                                                userId,
                                                                null,
                                                                user.firstName,
                                                                user.lastName,
                                                                user.phone,
                                                            )
                                                        
                                                        setIsCallBackground(false)
                                                        navigator?.push(
                                                            CallScreen(
                                                                userId,
                                                                null,
                                                                user.firstName,
                                                                user.lastName,
                                                                user.phone,
                                                            )
                                                        )
                                                        
                                                    }
                                                    
                                                    
                                                }
                                            }
                                            
                                            
                                        } catch (e: Exception) {
                                            
                                            println("Error newCall: $e")
                                        }
                                    }
                                    
                                    "callAnswered" -> {
                                        rtcMessage?.let {
                                            
                                            println("return@launch callAnswered ${it["sdp"]?.jsonPrimitive?.content}")
                                            
                                            val sdp =
                                                it["sdp"]?.jsonPrimitive?.content ?: return@launch
                                            
                                            val answer = SessionDescription(
                                                SessionDescriptionType.Answer,
                                                sdp
                                            )
                                            _peerConnection.value?.setRemoteDescription(answer)
                                            
                                            if (getPlatform() == Platform.Ios) {
                                                val swiftFuncsClass: SwiftFuncsClass = getKoin().get()
                                                
                                                swiftFuncsClass.initCallKit(phone = calleeUser.value.phone, callId = "1")
                                            }
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
                                                _peerConnection.value?.addIceCandidate(iceCandidate)
                                            }
                                        }
                                    }
                                    
                                    "rejectCall" -> {
                                        val navigator = commonViewModel.mainNavigator.value
                                        
                                        clearNotificationsForChannel("OngoingCallChannel")
                                        
                                        println("fafafasfa515151151 ${isScreenOn()}")
                                        
                                        if(getPlatform() == Platform.Ios) {
                                            val swiftFuncsClass: SwiftFuncsClass= getKoin().get()
                                            
                                            swiftFuncsClass.stopAVAudioSession()
                                            
                                        }
                                        
                                        val callViewModel: CallViewModel =
                                            KoinPlatform.getKoin().get()
                                        
                                        val profileViewModel: ProfileViewModel =
                                            KoinPlatform.getKoin().get()
                                        
                                        val currentScreen = navigator?.lastItem
                                        
                                        val userID = profileViewModel.profile.value.id
                                        
                                        val duration = callViewModel.timer.value
                                        
                                        callViewModel.stopTimer()
                                        
                                        setIsCallActive(false)
                                        
                                        
                                        
                                        if (isScreenOn()) {
                                            if (_isIncomingCall.value) {
                                                println("rejectCallAnswer() ${_isIncomingCall.value}")
                                                if (currentScreen is CallScreen) {
                                                    println("Мы на экране CallScreen 1")
                                                    
                                                    // Вы на экране CallScreen
                                                    navigator?.push(MainScreen())
                                                    
                                                } else if (currentScreen is MainScreen) {
                                                    // Вы на экране MainScreen
                                                    println("Мы на экране MainScreen 1")
                                                }
                                            }
                                            
                                            if (isCall.value) {
                                                rejectCallAnswer(
                                                    userId = userID,
                                                    chatId = chatId.value,
                                                    duration = duration,
                                                    calleeId = calleeId.value
                                                )
                                                println("rejectCallAnswer() 1")
                                            }


//                                            if (isConnectedWebrtc.value) {
                                            println("rejectCallAnswer() ${currentScreen}")
                                            if (currentScreen is CallScreen) {
                                                // Вы на экране CallScreen
                                                navigator?.push(MainScreen())
                                                
                                                println("Мы на экране CallScreen 2")
                                            } else if (currentScreen is MainScreen) {
                                                // Вы на экране MainScreen
                                                println("Мы на экране MainScreen 2")
                                            }

//                                            }
                                        } else {
                                            rejectCallAnswer()
                                            println("rejectCallAnswer() 2")
                                        }
                                        
                                        
                                        
                                        println("rejectCall3")
                                        
                                    }
                                }
                            }
                        }
                    }
                    
                    callOutputRoutine.join()
                }
            } catch (e: Exception) {
                _isConnectedWs.value = false
                println("Ошибка соединения: $e")
            }
        }
    }
    
    
    
    suspend fun reconnectCallWebSocket(
        userId: String
    ) {
        
        while (wsSession.value == null || wsSession.value?.isActive == false) {
            try {
                println("Attempting to reconnect WebSocket...")
                connectionWs(
                    userId = userId,
                )
                if (wsSession.value != null) {
                    println("WebSocket reconnected successfully!")
                    break
                }
            } catch (e: Exception) {
                println("Reconnect failed: ${e.message}. Retrying in 3 seconds...")
                delay(3000) // Задержка перед следующей попыткой
            }
        }
    }
    
    
    override suspend fun disconnectWs() {
        wsSession.value?.close()
    }
    
    
    override suspend fun initWebrtc(): Nothing = coroutineScope {
        
        isCall.value = true
        println("signalingStateClose")
        
        
        _peerConnection.value = PeerConnection(rtcConfiguration)
        
        
        println("peerConnection.value ${peerConnection.value}")
        
        if(getPlatform() == Platform.Ios) {
            val swiftFuncsClass: SwiftFuncsClass= getKoin().get()
            
//            swiftFuncsClass.setAVAudioSession()
            
        }
        
        val stream = MediaDevices.getUserMedia(audio = true)
        
        
        if (peerConnection.value !== null) {
            
            
            val cameraPer = PermissionsProviderFactory.create()
                .getPermission("microphone")
            
            
            
            println("Faileddadasdasda $stream")
            
            if (stream == null) {
                println("Failed to obtain audio stream")
            }
            
            localStream.value = stream
            
            stream.tracks.forEach { track ->
                println("addtrack ${track}")
                peerConnection.value!!.addTrack(track, localStream.value!!)
            }

//            }
            
            
            _isConnectedWebrtc.value = true
            // Обработка кандидатов ICE
            peerConnection.value!!.onIceCandidate
                .onEach { candidate ->
                    println("candidate ${candidate}")
                    
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
                        Logger.d { "PC2213131: $jsonMessage" }
                        Logger.d { "wsSession: ${wsSession.value}" }
                        
                        // Проверяем, активна ли корутина и открыт ли WebSocket
                        if (wsSession.value?.isActive == true) {
                            wsSession.value?.send(Frame.Text(jsonMessage))
                            println("Message sent successfully")
                        } else {
                            println("WebSocket session is not active")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        println("Failed to send message: onIceCandidate ${e.message}")
                    }
                    
                    peerConnection.value?.addIceCandidate(candidate)
                }
                .launchIn(this) // или другой подходящий Scope
            
            
            // Следим за изменениями состояния сигнализации
            peerConnection.value!!.onSignalingStateChange
                .onEach { signalingState ->
                    Logger.d { "peerState111 onSignalingStateChange: $signalingState" }
                    
                    if (signalingState == SignalingState.HaveRemoteOffer) {
                        Logger.d { " peer2 signalingState: $signalingState" }
                    }
                }
                .launchIn(this)
            
            // Следим за изменениями состояния соединения ICE
            peerConnection.value!!.onIceConnectionStateChange
                .onEach { state ->
                    
                    _iceState.value = state
                    Logger.d { "peerState111 onIceConnectionStateChange: $state" }
                }
                .launchIn(this)
            
            // Следим за изменениями общего состояния соединения
            peerConnection.value!!.onConnectionStateChange
                .onEach { state ->
                    Logger.d { "peerState111 onConnectionStateChange: $state" }

//                    AudioFactory.createAudioPlayer().stopAllAudioStreams()
                    
                    _callState.value = state
                    
                }
                .launchIn(this)
            
            
            // Обработка треков, получаемых от удалённого пира
            peerConnection.value!!.onTrack
                .onEach { println( "PC2 onTrack: ${it.track?.kind}" ) }
                .map { it.track }
                .filterNotNull()
                .onEach {
                    if (it.kind == MediaStreamTrackKind.Audio) {
                        remoteAudioTrack.value = it as AudioStreamTrack
                    } else if (it.kind == MediaStreamTrackKind.Video) {
                        remoteVideoTrack.value = it as VideoStreamTrack
                    }
                }
                .launchIn(this)
        }
        awaitCancellation()  // Поддерживаем корутину активной
        
    }
    
    
    override suspend fun getWsSession(): DefaultClientWebSocketSession? {
        return wsSession.value
    }
    
    override suspend fun getPeerConnection(): PeerConnection? {
        return peerConnection.value
    }
    
    override fun getOtherUserId(): String {
        return otherUserId.value
    }
    
    
    override fun getCallerId(): String {
        return callerId.value
    }
    
    
    override fun setMicro() {
        
        localStream.value?.audioTracks?.forEach { it.enabled = isMuted.value }
        
        isMuted.value = !isMuted.value
    }
    
    
    
     suspend fun setMedia() {
        val stream = MediaDevices.getUserMedia(audio = true, video = true)

         localStream.value = stream
    }
    
    
    override fun updateOtherUserId(userId: String) {
        
        otherUserId.value = userId
    }
    
    
    @OptIn(DelicateCoroutinesApi::class)
    override suspend fun makeCall(userId: String, calleeId: String) {
        println("makeCall31313131 ${wsSession.value}")
        
        coroutineScope {
//            resetWebRTC()
            
            if (wsSession.value != null) {
                try {
                    println("makeCall")
                    
                    if(getPlatform() == Platform.Ios) {
                        val swiftFuncsClass: SwiftFuncsClass= getKoin().get()
                        
//                        swiftFuncsClass.setAVAudioSession()
                        
                    }

                    val offer = peerConnection.value?.createOffer(
                        OfferAnswerOptions(offerToReceiveAudio = true)
                    )
                    if (offer != null) {
                        peerConnection.value?.setLocalDescription(offer)
                    }
                    
                    
                    if (wsSession.value?.outgoing?.isClosedForSend == true) {
                        return@coroutineScope
                    }
                    
                    println("offer $offer")
                    
                    val newCallMessage = WebRTCMessage(
                        type = "call",
                        calleeId = calleeId,
                        userId = userId,
                        rtcMessage = offer?.let { SessionDescriptionDTO(it.type, offer.sdp) }
                    )
                    
                    
                    val jsonMessage =
                        Json.encodeToString(WebRTCMessage.serializer(), newCallMessage)
                    
                    wsSession.value?.send(Frame.Text(jsonMessage))
                    println("Message sent successfully Call $jsonMessage")
                    
                    
                } catch (e: Exception) {
                    println("Failed to send message: ${e.message}")
                }
            }
        }
        
    }
    
    override suspend fun makeCallBackground(notificToken: String, calleeId: String) {
        coroutineScope {
            try {
                println("makeCall")
                
                val offer = peerConnection.value?.createOffer(
                    OfferAnswerOptions(
                        offerToReceiveAudio = true
                    )
                )
                if (offer != null) {
                    peerConnection.value?.setLocalDescription(offer)
                }
                
                
                val profileId = getValueInStorage("profileId")
                
                val newCallMessage = WebRTCMessage(
                    type = "call",
                    calleeId = calleeId,
                    userId = profileId,
                    rtcMessage = offer?.let { SessionDescriptionDTO(it.type, offer.sdp) }
                )
                
                val jsonMessage =
                    Json.encodeToString(WebRTCMessage.serializer(), newCallMessage)
                
                
                val jsonContent = Json.encodeToString(
                    buildJsonObject {
                        put("callData", jsonMessage)
                        put(
                            "notificationToken",
                            notificToken
                        )
                        
                    }
                )
                
                origin().post("notification/notifyCallBackground", jsonContent)
                
                
                println("Message sent successfully Call")
                
                
            } catch (e: Exception) {
                println("Failed to send message: ${e.message}")
            }
        }
        
    }
    
    @OptIn(DelicateCoroutinesApi::class)
    override suspend fun answerCall() {
        CoroutineScope(Dispatchers.IO).launch { // Запускаем в фоновом потоке
            if (wsSession.value != null) {
                try {
                    if(getPlatform() == Platform.Ios) {
                        val swiftFuncsClass: SwiftFuncsClass= getKoin().get()
                        
                        swiftFuncsClass.setAVAudioSession()
                        
                    }
                    println("answerCall1")
                    setRemoteDisc()
                    println("answerCall12")
                    
                    val otherUserId = otherUserId.value
                    
                    println("answerCall123")
                    
                    val answer = peerConnection.value?.createAnswer(
                        options = OfferAnswerOptions()
                    )
                    println("answerCall1234")
                    
                    if (answer != null) {
                        if (peerConnection.value?.remoteDescription == null) {
                            println("❌ RemoteDescription не установлен перед setLocalDescription")
                        } else {
                            peerConnection.value?.setLocalDescription(answer)
                        }
                    }
                    println("answerCall12345")
                    
                    if (wsSession.value?.outgoing?.isClosedForSend == true) {
                        println("wsSession.value?.outgoing?.isClosedForSend aaaa!!!!!!!")
                        return@launch
                    }
                    
                    val answerCallMessage = WebRTCMessage(
                        type = "answerCall",
                        callerId = otherUserId,
                        rtcMessage = answer?.let { SessionDescriptionDTO(it.type, answer.sdp) }
                    )
                    
                    println("answerCallMessage $answerCallMessage")
                    val jsonMessage = Json.encodeToString(WebRTCMessage.serializer(), answerCallMessage)
                    
                    wsSession.value?.send(Frame.Text(jsonMessage))
                    
                    setIsIncomingCall(false)
                    
                    println("Message sent successfully")
                } catch (e: Exception) {
                    println("❌ Failed to send message: ${e.message}")
                }
            }
        }
    }

    
    override fun answerCallBackground() {
        
        println("Start answerCallBackground1")
        
        CoroutineScope(Dispatchers.IO).launch {
            println("Start answerCallBackground2 ${wsSession.value}")
            
            
            try {
//                val contactsUseCase: ContactsUseCase by inject()
                val callViewModel: CallViewModel = KoinPlatform.getKoin().get()
                
                val cameraPer = PermissionsProviderFactory.create()
                    .getPermission("microphone")
                
                if (cameraPer) {
                    callViewModel.answerData.value?.let { answerData ->
                        
                        val rtcMessage = answerData.jsonObject["rtcMessage"]?.jsonObject
                        
                        rtcMessage.let {
                            val sdp =
                                it?.get("sdp")?.jsonPrimitive?.content
                                    ?: return@launch
                            
                            
                            val callerId =
                                answerData.jsonObject["userId"]?.jsonPrimitive?.content
                            
                            _peerConnection.value?.setRemoteDescription(
                                SessionDescription(
                                    SessionDescriptionType.Offer,
                                    sdp
                                )
                            )
                            
                            if (callerId != null) {
                                otherUserId.value = callerId
                                
                                println("111111")
                                
                                val answer = peerConnection.value?.createAnswer(
                                    options = OfferAnswerOptions()
                                )
                                println("22222")
                                
                                
                                if (answer != null) {
                                    peerConnection.value?.setLocalDescription(answer)
                                }
                                println("3333")
                                
                                if (wsSession.value?.outgoing?.isClosedForSend == true) {
                                    
                                    return@launch
                                }
                                println("44444")
                                
                                val answerCallMessage = WebRTCMessage(
                                    type = "answerCall",
                                    callerId = callerId,
                                    rtcMessage = answer?.let {
                                        SessionDescriptionDTO(
                                            it.type,
                                            answer.sdp
                                        )
                                    }
                                )
                                
                                
                                val jsonMessage =
                                    Json.encodeToString(
                                        WebRTCMessage.serializer(),
                                        answerCallMessage
                                    )
                                
                                setIsIncomingCall(false)
                                
                                
                                println("answerCallMessage $jsonMessage")
                                
                                wsSession.value?.send(Frame.Text(jsonMessage))
                                
                            }
                            
                        }
                    }
                }
                
                
            } catch (e: Exception) {
                
                println("Error newCall: $e")
            }
        }
        
    }
    
    
    override suspend fun rejectCall(calleeId: String, duration: String): Boolean {
        
        if (isCallRejected) {
            println("Call already rejected, skipping duplicate request")
            return false // Пропускаем повторный вызов
        }
        
        isCallRejected = true // Помечаем вызов как выполненный
        
        val callViewModel: CallViewModel = KoinPlatform.getKoin().get()
        val profileViewModel: ProfileViewModel = KoinPlatform.getKoin().get()
        val userID = profileViewModel.profile.value.id
        
        
        
        try {
            
            val jsonContent = Json.encodeToString(
                buildJsonObject {
                    put("type", "rejectCall")
                    put("callerId", userID) //кто звонит
                    put("calleeId", calleeId) // кому звонят
                    put("chatId", chatId.value)
                    put("duration", duration)
                }
            )
            
            
            
            
            println("rejectCalaaa $jsonContent")
            
            setIsIncomingCall(false)
            
            
            println("rejectCalWsSession ${wsSession.value}")
            
            wsSession.value?.send(Frame.Text(jsonContent))
            println("${jsonContent} rejectCall13")
            
            rejectCallAnswer(
                userId = userID,
                calleeId = _calleeId.value,
                chatId = _chatId.value,
                duration = duration
            )
            
            println("rejectCall134")
            
            return true
            
        } catch (e: Exception) {
            println("errorRejectCall: $e")
            val navigator = commonViewModel.mainNavigator.value
            
            navigator?.push(MainScreen())
            return false
            
        } finally {
            isCallRejected = false
        }
    }
    
    
    private suspend fun rejectCallAnswer(
        userId: String? = null,
        calleeId: String? = null,
        chatId: String? = null,
        duration: String? = null
    ) {
        try {
            
            if (getPlatform() == Platform.Ios) {
                val swiftFuncsClass: SwiftFuncsClass = getKoin().get()
                
                swiftFuncsClass.stopAVAudioSession()
            }
            
            
            if (!_isIncomingCall.value) {
                // Убедиться, что сообщение отправляется только один раз
                if (isCallRejected) {
                    println("Call answer already rejected, skipping duplicate request")
                    return
                }
                
                val jsonContent = Json.encodeToString(
                    buildJsonObject {
                        put("type", "rejectCall")
                        put("callerId", userId)
                        put("calleeId", calleeId)
                        put("chatId", chatId)
                        put("duration", duration)
                    }
                )
                wsSession.value?.send(Frame.Text(jsonContent))
                println("${jsonContent} callEnded")
            }
            
            // Очистка ресурсов
            
        } catch (e: Exception) {
            println("Error in rejectCallAnswer: $e")
        } finally {
            isCallRejected = false // Сбрасываем флаг после выполнения
        }
        
        try {
            // Проверяем, инициализирован ли peerConnection
            val currentPeerConnection = _peerConnection.value
            println("rejectCallAnswer1")
            
            if (currentPeerConnection !== null && currentPeerConnection.signalingState != SignalingState.Closed) {
                // Останавливаем все треки локального потока
                println("rejectCallAnswer2signalingState")
                
//                localStream.value?.let { stream ->
//                    stream.tracks.forEach { track ->
//                        track.stop()
//                    }
//                }
                println("rejectCallAnswer3")
                
                // Удаляем все треки
                currentPeerConnection.getTransceivers().forEach {
                    currentPeerConnection.removeTrack(it.sender)
                }
                
                localStream.value?.release()
                
                // Закрываем PeerConnection
                currentPeerConnection.close()
                
                
            } else {
                Logger.w { "PeerConnection already closed" }
            }
            
            println("rejectCallAnswer4")
// Очищаем локальный и удаленный потоки
            isCall.value = false
            _isCallActive.value = false
            _isIncomingCall.value = false
            _isCallBackground.value = false
//            localStream.value = null
            remoteVideoTrack.value = null
            _isConnectedWebrtc.value = false
            offer.value = null
            otherUserId.value = ""
            callerId.value = ""
            _iceState.value = IceConnectionState.New
            _callState.value = PeerConnectionState.New
            isMuted.value = false
            println("rejectCallAnswer5")
            _chatId.value = ""
            _calleeId.value = ""
            _peerConnection.value = null
            

            
            if (isScreenOn()) {
                
                val navigator = commonViewModel.mainNavigator.value
                val currentScreen = navigator?.lastItem
                
                if (currentScreen is CallScreen) {
                    // Вы на экране CallScreen
                    
                    println("MainScreen $navigator")
                    navigator.push(MainScreen())
                    
                    println("Мы на экране CallScreen")
                } else if (currentScreen is MainScreen) {
                    // Вы на экране MainScreen
                    println("Мы на экране MainScreen")
                }
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    wsSession.value?.close()
                }
                closeApp()
            }
            
            
            Logger.d { "Call answer rejected and resources cleaned up successfully." }
        } catch (e: Exception) {
            Logger.e(e) { "Failed to reject call answer and clean up resources. $e" }
        }
    }
    
    override fun clearData() {
        _peerConnection.value?.close()
        _peerConnection.value = null
        isCall.value = false
        _isCallActive.value = false
        _isIncomingCall.value = false
        _isCallBackground.value = false
        _peerConnection.value = null
        localStream.value = null
        remoteVideoTrack.value = null
        _isConnectedWebrtc.value = false
        _isConnectedWs.value = false
        offer.value = null
        otherUserId.value = ""
        callerId.value = generateRandomNumber() // Генерация нового callerId для следующего вызова
        _wsSession.value = null
        _iceState.value = IceConnectionState.New
        _callState.value = PeerConnectionState.New
        _chatId.value = ""
        _calleeId.value = ""
        isMuted.value = false
    }
    
    
    override fun setIsIncomingCall(isIncomingCallValue: Boolean) {
        _isIncomingCall.value = isIncomingCallValue
    }
    
    override fun setIsCallActive(isCallActive: Boolean) {
        _isCallActive.value = isCallActive
    }
    
    override fun setIsCallBackground(isCallBackground: Boolean) {
        _isCallBackground.value = isCallBackground
    }
    
    override fun setOtherUserId(newOtherUserId: String) {
        otherUserId.value = newOtherUserId
    }
    
    override fun setChatId(chatId: String) {
        _chatId.value = chatId
    }
    
    override fun setCalleeId(calleeId: String) {
        _calleeId.value = calleeId
    }
    
    override fun setCalleeUserInfo(calleeUserInfo: ProfileDTO) {
        calleeUser.value = calleeUserInfo
    }
    override  fun resetWebRTC() {
        println("🛑 Resetting WebRTC session...")
//
//        // 1. Остановка всех треков локального потока
//        localStream.value?.tracks?.forEach { it.stop() }
//        localStream.value = null
//
//        remoteVideoTrack.value = null
//
//        // 2. Закрываем текущий PeerConnection
//        _peerConnection.value?.close()
//        _peerConnection.value = null

//        // 3. Закрываем WebSocket
//        wsSession.value?.close()
//        _wsSession.value = null
        
        // 4. Деактивируем AVAudioSession (iOS)
//        if (getPlatform() == Platform.Ios) {
//            val swiftFuncsClass: SwiftFuncsClass = getKoin().get()
//            swiftFuncsClass.stopAVAudioSession() // Добавь этот метод в Swift
//        }
//
        // 5. Сбрасываем состояние
//        _isConnectedWebrtc.value = false
//        _isConnectedWs.value = false
//        _isCallActive.value = false
//        _isIncomingCall.value = false
//        _isCallBackground.value = false
//        offer.value = null
//        otherUserId.value = ""
//        callerId.value = generateRandomNumber()
//        _chatId.value = ""
//        _calleeId.value = ""
        
        println("✅ WebRTC session reset complete.")
    }
    
}


suspend fun initializeAudioSessionAndStream(): MediaStream? {
    return withContext(Dispatchers.IO) {
        try {
            configureAudioSession() // Настраиваем аудиосессию
            println("Audio session configured, obtaining MediaStream...")
            val stream = MediaDevices.getUserMedia(audio = true) // Получаем аудиопоток
            if (stream == null) {
                println("Failed to obtain audio stream")
            } else {
                println("Audio stream obtained: $stream")
            }
            stream
        } catch (e: Exception) {
            println("Error initializing audio session or stream: ${e.message}")
            null
        }
    }
}




