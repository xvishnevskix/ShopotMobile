package org.videotrade.shopot.presentation.screens.call

import androidx.compose.runtime.remember
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import com.shepeliev.webrtckmp.IceConnectionState
import com.shepeliev.webrtckmp.MediaStream
import com.shepeliev.webrtckmp.PeerConnectionState
import com.shepeliev.webrtckmp.VideoStreamTrack
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.http.HttpMethod
import io.ktor.util.encodeBase64
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import okio.ByteString.Companion.decodeBase64
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.mp.KoinPlatform
import org.videotrade.shopot.api.EnvironmentConfig.WEB_SOCKETS_URL
import org.videotrade.shopot.api.addValueInStorage
import org.videotrade.shopot.api.decupsMessage
import org.videotrade.shopot.api.delValueInStorage
import org.videotrade.shopot.api.getValueInStorage
import org.videotrade.shopot.domain.usecase.CallUseCase
import org.videotrade.shopot.domain.usecase.ChatsUseCase
import org.videotrade.shopot.domain.usecase.ContactsUseCase
import org.videotrade.shopot.domain.usecase.ProfileUseCase
import org.videotrade.shopot.domain.usecase.WsUseCase
import org.videotrade.shopot.multiplatform.AudioFactory
import org.videotrade.shopot.multiplatform.CipherWrapper
import org.videotrade.shopot.multiplatform.EncapsulationFileResult
import org.videotrade.shopot.multiplatform.MusicPlayer
import org.videotrade.shopot.multiplatform.Platform
import org.videotrade.shopot.multiplatform.clearNotificationsForChannel
import org.videotrade.shopot.multiplatform.getPlatform
import org.videotrade.shopot.multiplatform.iosCall.GetCallInfoDto
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.intro.IntroViewModel
import org.videotrade.shopot.presentation.screens.intro.WelcomeScreen
import org.videotrade.shopot.presentation.screens.main.MainScreen

class CallViewModel() : ViewModel(), KoinComponent {
    private val callUseCase: CallUseCase by inject()
    private val profileUseCase: ProfileUseCase by inject()
    private val contactsUseCase: ContactsUseCase by inject()
    private val commonViewModel: CommonViewModel by inject()
    private val wsUseCase: WsUseCase by inject()
    private val chatsUseCase: ChatsUseCase by inject()
    
    val isConnectedWs = callUseCase.isConnectedWs
    val isCallBackground = callUseCase.isCallBackground

    val isIncomingCall = callUseCase.isIncomingCall
    
//    private val _isConnectedWebrtc = MutableStateFlow(false)
    val isConnectedWebrtc =  callUseCase.isConnectedWebrtc
    
    val localStream =  callUseCase.localStream
    
    val remoteVideoTrack = callUseCase.remoteVideoTrack
    
    val remoteAudioTrack = callUseCase.remoteAudioTrack
    
    
    private val _callState = MutableStateFlow(PeerConnectionState.New)
    val callState: StateFlow<PeerConnectionState> get() = _callState
    
    val _iceState = MutableStateFlow<IceConnectionState>(IceConnectionState.New)
    val iceState: StateFlow<IceConnectionState> get() = _iceState
    
    // Флаг для управления наблюдением
    private var isObserving = MutableStateFlow(true)
    
    var answerData = MutableStateFlow<JsonObject?>(null)
    
    var iosCallData = MutableStateFlow<GetCallInfoDto?>(null)
    
    val isScreenOn = MutableStateFlow(false)
    
    val localStreamm = callUseCase.localStream
    
    val isCallActive = callUseCase.isCallActive
    
    val replaceInCall = MutableStateFlow(false)
    
    // Таймер
    private val _timer = MutableStateFlow("00:00:00")
    val timer: StateFlow<String> get() = _timer
    
    private var timerJob: Job? = null
    private var elapsedSeconds = 0L
    
    val isTimerRunning = MutableStateFlow(false)
    
    private val _userIcon = MutableStateFlow<String?>(null)
    val userIcon: StateFlow<String?> get() = _userIcon
    
    val callScreenInfo = MutableStateFlow<Screen?>(null)
    
    val musicPlayer: StateFlow<MusicPlayer> = MutableStateFlow(AudioFactory.createMusicPlayer())
    
    
    private fun updateUserIcon(icon: String?) {
        _userIcon.value = icon
    }
    
    fun startTimer(icon: String?) {
        updateUserIcon(icon)
        elapsedSeconds = 0L
        isTimerRunning.value = true
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                elapsedSeconds++
                _timer.value = formatTime(elapsedSeconds)
            }
        }
    }
    
    fun stopTimer() {
        timerJob?.cancel()
        isTimerRunning.value = false
        timerJob = null
        _timer.value = "00:00:00"
    }
    
    
    private fun formatTime(seconds: Long): String {
        val hours = (seconds / 3600).toInt().toString().padStart(2, '0')
        val minutes = ((seconds % 3600) / 60).toInt().toString().padStart(2, '0')
        val secs = (seconds % 60).toInt().toString().padStart(2, '0')
        return "$hours:$minutes:$secs"
    }
    
    fun setAnswerData(JsonObject: JsonObject?) {
        answerData.value = JsonObject
    }
    
    fun setIosCallData(callInfo: GetCallInfoDto) {
        iosCallData.value = callInfo
    }
    
    fun setIsScreenOn(isScreenOnNew: Boolean) {
        isScreenOn.value = isScreenOnNew
    }
    
    
    private fun startObserving() {
        viewModelScope.launch {
            observeCallStates()
            observeIsConnectedWebrtc()
            observeStreams()
        }
    }
    
    private fun observeCallStates() {
        callUseCase.iseState
            .onEach { iseStateNew ->
                if (isObserving.value) {
                    _iceState.value = iseStateNew
                    println("iseStateNew $iseStateNew")
                }
            }
            .launchIn(viewModelScope)
        
        callUseCase.callState
            .onEach { callStateNew ->
                if (isObserving.value) {
                    _callState.value = callStateNew
                    musicPlayer.value.stop()
                    println("callStateNew $callStateNew")
                }
            }
            .launchIn(viewModelScope)
    }
    
    private fun observeStreams() {
//        callUseCase.localStream
//            .onEach { localStreamNew ->
//                if (isObserving.value) {
//                    _localStream.value = localStreamNew
//                    println("_localStream ${_localStream.value}")
//                }
//            }
//            .launchIn(viewModelScope)
//
//        callUseCase.remoteVideoTrack
//            .onEach { remoteVideoTrackNew ->
//                if (isObserving.value) {
//                    _remoteVideoTrack.value = remoteVideoTrackNew
//                    println("remoteVideoTrackNew $remoteVideoTrackNew")
//                }
//            }
//            .launchIn(viewModelScope)
    }
    
    private fun observeIsConnectedWebrtc() {
//        callUseCase.isConnectedWebrtc
//            .onEach { isConnectedWebrtcNew ->
//                if (isObserving.value) {
//                    _isConnectedWebrtc.value = isConnectedWebrtcNew
//                    println("isConnectedWebrtcNew $isConnectedWebrtcNew")
//                }
//            }
//            .launchIn(viewModelScope)
    }
    
    
    fun getWsSession() {
        viewModelScope.launch {
            println("dsadada ${callUseCase.getWsSession()}")
        }
    }
    
    fun connectionCallWs(userId: String) {
        viewModelScope.launch {
            callUseCase.connectionWs(userId)
        }
    }
    
    fun updateOtherUserId(userId: String) {
        viewModelScope.launch {
            callUseCase.updateOtherUserId(userId)
        }
    }
    
    fun getOtherUserId(): String {
        return callUseCase.getOtherUserId()
    }
    
    fun getCallerId(): String {
        return callUseCase.getCallerId()
    }
    
    fun setMicro() {
        return callUseCase.setMicro()
    }
    
    fun reconnectPeerConnection() {
        viewModelScope.launch {
            callUseCase.reconnectPeerConnection()
        }
    }
    
    fun initWebrtc() {
        viewModelScope.launch {
            startObservingAgain()
            callUseCase.initWebrtc()
        }
    }
    
    suspend fun makeCall(calleeId: String) {
        println("aaaaaaa ${profileUseCase.getProfile()}")
        profileUseCase.getProfile().let { callUseCase.makeCall(it.id, calleeId) }
    }
    
    @OptIn(DelicateCoroutinesApi::class)
    suspend fun answerCall() {
        callUseCase.answerCall()
    }
    
     fun resetWebRTC() {
        callUseCase.resetWebRTC()
    }
    
    fun answerCallBackground() {
        callUseCase.answerCallBackground()
    }
    
    fun rejectCall(calleeId: String, duration: String) {
        viewModelScope.launch {

            if(getPlatform() == Platform.Android) {
                clearNotificationsForChannel("OngoingCallChannel")
            }
            
            stopTimer()
            setIsCallActive(false)

            val isRejectCall = callUseCase.rejectCall(calleeId, duration)

            if (isRejectCall) {
                val navigator = commonViewModel.mainNavigator.value
//                navigator?.popAll()
                navigator?.push(MainScreen())
            }
        }
        
    }
    
    
    fun rejectCallAnswer(): Boolean {
        _callState.value = PeerConnectionState.New
        _iceState.value = IceConnectionState.New
        callUseCase.rejectCallAnswer()
        
        return true
    }
    
    // Новая функция для остановки наблюдения
    fun stopObserving() {
        isObserving.value = false
    }
    
    // Новая функция для запуска наблюдения
    fun startObservingAgain() {
        isObserving.value = true
        startObserving()
    }
    
    fun initCall(userId: String) {
        println("dsadadadadad ${callUseCase.wsSession.value}")
        viewModelScope.launch {
            if (callUseCase.wsSession.value != null) {
                updateOtherUserId(userId)
                makeCall(userId)
            }
        }
        
    }
    
    fun makeCallBackground(notificToken: String, calleeId: String) {
        viewModelScope.launch {
            callUseCase.makeCallBackground(notificToken, calleeId)
            
        }
    }
    
    fun setIsCallBackground(isCallBackground: Boolean) {
        callUseCase.setIsCallBackground(isCallBackground)
    }
    
    fun setIsCallActive(isCallActive: Boolean) {
        return callUseCase.setIsCallActive(isCallActive)
    }
    
    fun setIsIncomingCall(isIncomingCallValue: Boolean) {
        return callUseCase.setIsIncomingCall(isIncomingCallValue)
    }
    
    
    private fun observeWsConnection() {
        println("wsSessionIntrowsUseCase.wsSession ${wsUseCase.wsSession.value}")
        
        val profileId = getValueInStorage("profileId")
        
        
        wsUseCase.wsSession
            .onEach { wsSessionNew ->
                
                if (profileId !== null && isObserving.value) {
                    
                    if (wsSessionNew != null) {
                        println("wsSessionIntro $wsSessionNew")
                        stopObserving()
                        chatsUseCase.getChatsInBack(wsSessionNew, profileId)
                        
                    }
                }
                
            }
            .launchIn(viewModelScope)
        
        
    }
    
    
    fun checkUserShared(userId: String, navigator: Navigator) {
        
        viewModelScope.launch {
            
            val httpClient = HttpClient {
                install(WebSockets)
            }
            try {
                httpClient.webSocket(
                    method = HttpMethod.Get,
                    host = WEB_SOCKETS_URL,
                    port = 3050,
                    path = "/crypto?userId=$userId",
                    
                    ) {
                    
                    println("jsonElement$userId")
                    
                    val jsonContent = Json.encodeToString(
                        buildJsonObject {
                            put("action", "getKeys")
                        }
                    )
                    
                    send(Frame.Text(jsonContent))
                    
                    for (frame in incoming) {
                        if (frame is Frame.Text) {
                            val text = frame.readText()
                            
                            val jsonElement = Json.parseToJsonElement(text)
                            
                            println("jsonElement $jsonElement")
                            
                            val action =
                                jsonElement.jsonObject["action"]?.jsonPrimitive?.content
                            
                            
                            when (action) {
                                "answerPublicKey" -> {
                                    val cipherWrapper: CipherWrapper = KoinPlatform.getKoin().get()
                                    
                                    val publicKeyString =
                                        jsonElement.jsonObject["publicKey"]?.jsonPrimitive?.content
                                    
                                    val publicKeyBytes =
                                        publicKeyString?.decodeBase64()?.toByteArray()
                                    
                                    println("publicKeyBytes ${publicKeyBytes?.encodeBase64()}")
                                    
                                    
                                    val result = publicKeyBytes?.let {
                                        cipherWrapper.getSharedSecretCommon(
                                            it
                                        )
                                    }
                                    
                                    
                                    if (result !== null) {
                                        val answerPublicKeyJsonContent = Json.encodeToString(
                                            buildJsonObject {
                                                put("action", "sendCipherText")
                                                put("cipherText", result.ciphertext.encodeBase64())
                                                put(
                                                    "deviceId", getValueInStorage("deviceId")
                                                )
                                            }
                                        )
                                        
                                        println(
                                            "answerPublicKeyJsonContent111 $answerPublicKeyJsonContent"
                                        )
                                        
                                        
                                        
                                        
                                        addValueInStorage(
                                            "sharedSecret",
                                            result.sharedSecret.encodeBase64()
                                        )
                                        
                                        send(Frame.Text(answerPublicKeyJsonContent))
                                    }
                                    
                                    
                                }
                                
                                "successSharedSecret" -> {
                                    
                                    val introViewModel: IntroViewModel =
                                        KoinPlatform.getKoin().get()
                                    
                                    addValueInStorage("profileId", userId!!)
                                    
                                    commonViewModel.updateNotificationToken()
                                    
                                    introViewModel.fetchContacts(navigator)
                                    
                                }
                                
                                "encryptedSharedSecret" -> {
                                    println(
                                        "encryptedSharedSecret $jsonElement"
                                    )
                                    val secret =
                                        jsonElement.jsonObject["secret"]?.jsonObject
                                    
                                    println(
                                        "encryptedSharedSecret $secret"
                                    )
                                    
                                    val cipherWrapper: CipherWrapper = KoinPlatform.getKoin().get()
                                    
                                    if (secret != null) {
                                        val sharedSecretDecups =
                                            decupsMessage(secret.toString(), cipherWrapper)
                                        
                                        
                                        if (sharedSecretDecups != null) {
                                            addValueInStorage(
                                                "sharedSecret",
                                                sharedSecretDecups
                                            )
                                            
                                            val introViewModel: IntroViewModel =
                                                KoinPlatform.getKoin().get()
                                            
                                            addValueInStorage("profileId", userId!!)
                                            
                                           commonViewModel.updateNotificationToken()
                                            
                                            introViewModel.fetchContacts(navigator)
                                        }
                                    }
                                    
                                }
                                
                            }
                        }
                    }
                }
            } catch (e: Exception) {
            
            
            }
        }
        
    }
    
    
    private fun fetchContacts(navigator: Navigator) {
        viewModelScope.launch {
            val contacts = contactsUseCase.fetchContacts()
            
            if (contacts != null) {
                val profileCase = profileUseCase.downloadProfile()
                
                if (profileCase == null) {
                    
                    navigator.push(WelcomeScreen())
                    return@launch
                    
                } else {
                    addValueInStorage("profileId", profileCase.id)
                    observeWsConnection()
                    connectionMainWs(profileCase.id, navigator)
                }
                
            }
        }
    }
    
    private fun connectionMainWs(userId: String, navigator: Navigator) {
        viewModelScope.launch {
            wsUseCase.connectionWs(userId, navigator)
        }
    }
    
    
     fun disconnectWs() {
        viewModelScope.launch {
            wsUseCase.disconnectWs()
        }
    }
    
    fun replacePopCall(navigator: Navigator) {
        val navPop = navigator.pop()
        println("navPop $navPop")
        if (!navPop) {
            navigator.push(MainScreen())
        }
    }
    
    
    fun setOtherUserId(newOtherUserId: String) {
        callUseCase.setOtherUserId(newOtherUserId)
    }

    fun setChatId(chatId: String) {
        callUseCase.setChatId(chatId)
    }

    fun setCalleeId(calleeId: String) {
        callUseCase.setCalleeId(calleeId)
    }
}
