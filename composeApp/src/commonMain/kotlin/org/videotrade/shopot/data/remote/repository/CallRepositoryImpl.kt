package org.videotrade.shopot.data.remote.repository

import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.navigator.Navigator
import co.touchlab.kermit.Logger
import com.shepeliev.webrtckmp.IceCandidate
import com.shepeliev.webrtckmp.IceConnectionState
import com.shepeliev.webrtckmp.IceServer
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
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
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
import org.videotrade.shopot.api.EnvironmentConfig.webSocketsUrl
import org.videotrade.shopot.api.findContactByPhone
import org.videotrade.shopot.api.getValueInStorage
import org.videotrade.shopot.data.origin
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.domain.model.SessionDescriptionDTO
import org.videotrade.shopot.domain.model.WebRTCMessage
import org.videotrade.shopot.domain.model.rtcMessageDTO
import org.videotrade.shopot.domain.repository.CallRepository
import org.videotrade.shopot.domain.usecase.ContactsUseCase
import org.videotrade.shopot.multiplatform.PermissionsProviderFactory
import org.videotrade.shopot.presentation.screens.call.CallViewModel
import org.videotrade.shopot.presentation.screens.call.IncomingCallScreen
import org.videotrade.shopot.presentation.screens.main.MainScreen
import kotlin.random.Random

class CallRepositoryImpl : CallRepository, KoinComponent {
    private val iceServers = listOf(
        "stun:stun.l.google.com:19302",
        "stun:stun1.l.google.com:19302",
        "stun:stun2.l.google.com:19302",
    )

    private val turnServers = listOf(
//        "turn:89.221.60.156:3478",
        "turn:89.221.60.161:3478?transport=udp",
    )

    // Создание конфигурации для PeerConnection
    private val rtcConfiguration = RtcConfiguration(
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


    private val isCall = mutableStateOf(false)
    private val isIncomingCall = mutableStateOf(false)

    private val callerId = mutableStateOf(generateRandomNumber())

    private val _wsSession = MutableStateFlow<DefaultClientWebSocketSession?>(null)
    override val wsSession: StateFlow<DefaultClientWebSocketSession?> get() = _wsSession

    private val _peerConnection =
        MutableStateFlow<PeerConnection?>(PeerConnection(rtcConfiguration))
    override val peerConnection: StateFlow<PeerConnection?> get() = _peerConnection

    private val offer = MutableStateFlow<SessionDescription?>(null)

    override val localStream = MutableStateFlow<MediaStream?>(null)

    override val remoteVideoTrack = MutableStateFlow<VideoStreamTrack?>(null)


    private val _isConnectedWebrtc = MutableStateFlow(false)

    override val isConnectedWebrtc: StateFlow<Boolean> get() = _isConnectedWebrtc


    private val _callState = MutableStateFlow(PeerConnectionState.New)

    override val callState: StateFlow<PeerConnectionState> get() = _callState


    private val _iceState = MutableStateFlow(IceConnectionState.New)

    override val iseState: StateFlow<IceConnectionState> get() = _iceState

    private val isMuted = MutableStateFlow(false)


    override suspend fun reconnectPeerConnection() {
        // Переподключение PeerConnection
        _peerConnection.value = PeerConnection(rtcConfiguration)
    }

    override suspend fun setOffer() {
        offer.value?.let { _peerConnection.value?.setRemoteDescription(it) }

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
                                            val contactsUseCase: ContactsUseCase by inject()

                                            val cameraPer = PermissionsProviderFactory.create()
                                                .getPermission("microphone")

                                            if (cameraPer) {
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

                                                        isIncomingCall.value = true
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

                                                        navigator.push(
                                                            IncomingCallScreen(
                                                                userId,
                                                                user
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
                                        if (isIncomingCall.value) {
                                            navigator.push(MainScreen())
                                        }

                                        println("rejectCall1 ${isCall.value} ${isConnectedWebrtc.value}")
                                        if (isCall.value)
                                            rejectCallAnswer(navigator)

                                        println("rejectCall2 ${isConnectedWebrtc.value}")


                                        if (isConnectedWebrtc.value) {

                                            navigator.push(MainScreen())

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
                isConnected.value = false
                println("Ошибка соединения: $e")
            }
        }
    }

    override suspend fun connectionBackgroundWs(userId: String) {
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
                                            val contactsUseCase: ContactsUseCase by inject()

                                            val cameraPer = PermissionsProviderFactory.create()
                                                .getPermission("microphone")

                                            if (cameraPer) {
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

                                                        isIncomingCall.value = true
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

                                        println("rejectCall1 ${isCall.value} ${isConnectedWebrtc.value}")

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

        isCall.value = true
        println("signalingStateClose")

//            val peerConnection.value = PeerConnection(rtcConfiguration)


//            _peerConnection.value = PeerConnection(rtcConfiguration)


        println("peerConnection.value ${peerConnection.value}")

        if (peerConnection.value !== null) {

            val stream = MediaDevices.getUserMedia(audio = true)

            localStream.value = stream


            stream.tracks.forEach { track ->
                println("addtrack ${track}")
                peerConnection.value!!.addTrack(track, localStream.value!!)
            }



            _isConnectedWebrtc.value = true
            // Обработка кандидатов ICE
            peerConnection.value!!.onIceCandidate
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


                    _callState.value = state

                }
                .launchIn(this)


            // Обработка треков, получаемых от удалённого пира
            peerConnection.value!!.onTrack
                .onEach { event ->
                    Logger.d { "onTrack: $  ${event.track} ${event.streams} ${event.receiver} ${event.transceiver}" }
                    if (event.track?.kind == MediaStreamTrackKind.Video) {
                        remoteVideoTrack.value = event.track as VideoStreamTrack
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


    override fun updateOtherUserId(userId: String) {

        otherUserId.value = userId
    }


    @OptIn(DelicateCoroutinesApi::class)
    override suspend fun makeCall(userId: String, calleeId: String) {
        println("makeCall31313131 ${wsSession.value}")

        coroutineScope {
            if (wsSession.value != null) {
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


                    if (wsSession.value?.outgoing?.isClosedForSend == true) {
                        return@coroutineScope
                    }

                    val newCallMessage = WebRTCMessage(
                        type = "call",
                        calleeId = calleeId,
                        userId = userId,
                        rtcMessage = offer?.let { SessionDescriptionDTO(it.type, offer.sdp) }
                    )


                    val jsonMessage =
                        Json.encodeToString(WebRTCMessage.serializer(), newCallMessage)

                    wsSession.value?.send(Frame.Text(jsonMessage))
                    println("Message sent successfully Call")


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
        coroutineScope {
            if (wsSession.value != null) {

                try {
                    println("answerCall")
                    setOffer()


                    val otherUserId = otherUserId.value


                    val answer = peerConnection.value?.createAnswer(
                        options = OfferAnswerOptions(
                            offerToReceiveAudio = true
                        )
                    )

                    if (answer != null) {
                        peerConnection.value?.setLocalDescription(answer)
                    }

                    if (wsSession.value?.outgoing?.isClosedForSend == true) {
                        println("wsSession.value?.outgoing?.isClosedForSend aaaa!!!!!!!")

                        return@coroutineScope
                    }

                    val answerCallMessage = WebRTCMessage(
                        type = "answerCall",
                        callerId = otherUserId,
                        rtcMessage = answer?.let { SessionDescriptionDTO(it.type, answer.sdp) }
                    )

                    Logger.d {
                        "answerCallMessage $answerCallMessage"
                    }
                    val jsonMessage =
                        Json.encodeToString(WebRTCMessage.serializer(), answerCallMessage)

                    setIsIncomingCall(false)

                    wsSession.value?.send(Frame.Text(jsonMessage))
                    println("Message sent successfully")
                } catch (e: Exception) {
                    println("Failed to send message: ${e.message}")
                }
            }
        }

    }

    override fun answerCallBackground() {

        println("Start answerCallBackground1")

        CoroutineScope(Dispatchers.IO).launch {
            println("Start answerCallBackground2")


            try {
//                val contactsUseCase: ContactsUseCase by inject()
                val callViewModel: CallViewModel = KoinPlatform.getKoin().get()

//                val cameraPer = PermissionsProviderFactory.create()
//                    .getPermission("microphone")
//
//                if (cameraPer) {
                    callViewModel.answerData.value?.let { answerData ->

                        val rtcMessage = answerData.jsonObject["rtcMessage"]?.jsonObject

                        rtcMessage.let {
                            val sdp =
                                it?.get("sdp")?.jsonPrimitive?.content
                                    ?: return@launch


                            val callerId =
                                answerData.jsonObject["userId"]?.jsonPrimitive?.content

                            _peerConnection.value?.setRemoteDescription(SessionDescription(
                                SessionDescriptionType.Offer,
                                sdp
                            ))

                            if (callerId != null) {
                                otherUserId.value = callerId

                                println("111111")

                                val answer = peerConnection.value?.createAnswer(
                                    options = OfferAnswerOptions(
                                        offerToReceiveAudio = true
                                    )
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


                                Logger.d {
                                    "answerCallMessage $jsonMessage"
                                }

                                wsSession.value?.send(Frame.Text(jsonMessage))

                            }

                        }
                    }
//                }


            } catch (e: Exception) {

                println("Error newCall: $e")
            }
        }

    }


    override suspend fun rejectCall(navigator: Navigator, userId: String): Boolean {
        try {


            val jsonContent = Json.encodeToString(
                buildJsonObject {
                    put("type", "rejectCall")
                    put("userId", userId)
                }
            )


            rejectCallAnswer(navigator)

            setIsIncomingCall(false)

            wsSession.value?.send(Frame.Text(jsonContent))
            println("rejectCall13")


            println("rejectCall134")

            return true

        } catch (e: Exception) {
            println(e)

            navigator.push(MainScreen())
            return false

        }
    }

    fun rejectCallAnswer(navigator: Navigator) {
        try {
            // Проверяем, инициализирован ли peerConnection
            val currentPeerConnection = _peerConnection.value
            println("rejectCallAnswer1")

            if (currentPeerConnection !== null && currentPeerConnection.signalingState != SignalingState.Closed) {
                // Останавливаем все треки локального потока
                println("rejectCallAnswer2")

                localStream.value?.let { stream ->
                    stream.tracks.forEach { track ->
                        track.stop()
                    }
                }

                println("rejectCallAnswer3")

                // Удаляем все треки
                currentPeerConnection.getTransceivers().forEach {
                    currentPeerConnection.removeTrack(it.sender)
                }

                // Закрываем PeerConnection
                currentPeerConnection.close()
            } else {
                Logger.w { "PeerConnection already closed" }
            }

            println("rejectCallAnswer4")

            // Очищаем локальный и удаленный потоки
            isCall.value = false
            _peerConnection.value = null
            localStream.value = null
            remoteVideoTrack.value = null
            _isConnectedWebrtc.value = false
            offer.value = null
            otherUserId.value = ""
            callerId.value = ""
            _iceState.value = IceConnectionState.New
            _callState.value = PeerConnectionState.New

            println("rejectCallAnswer5")

            _peerConnection.value = PeerConnection(rtcConfiguration)


            navigator.push(MainScreen())

            Logger.d { "Call answer rejected and resources cleaned up successfully." }
        } catch (e: Exception) {
            Logger.e(e) { "Failed to reject call answer and clean up resources. $e" }
        }
    }

    override fun clearData() {
        _peerConnection.value?.close()
        _peerConnection.value = null
        localStream.value = null
        remoteVideoTrack.value = null
        _isConnectedWebrtc.value = false
        offer.value = null
        otherUserId.value = ""
        callerId.value = ""
        _iceState.value = IceConnectionState.New
        _callState.value = PeerConnectionState.New
        _wsSession.value = null
    }


    override fun setIsIncomingCall(isIncomingCallValue: Boolean) {
        isIncomingCall.value = isIncomingCallValue
    }


}
