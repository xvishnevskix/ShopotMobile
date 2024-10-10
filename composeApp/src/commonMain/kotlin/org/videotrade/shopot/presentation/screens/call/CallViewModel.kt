package org.videotrade.shopot.presentation.screens.call

import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import com.shepeliev.webrtckmp.IceConnectionState
import com.shepeliev.webrtckmp.MediaStream
import com.shepeliev.webrtckmp.PeerConnectionState
import com.shepeliev.webrtckmp.VideoStreamTrack
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.videotrade.shopot.domain.usecase.CallUseCase
import org.videotrade.shopot.domain.usecase.ProfileUseCase
import org.videotrade.shopot.multiplatform.closeApp
import org.videotrade.shopot.presentation.screens.main.MainScreen

class CallViewModel() : ViewModel(), KoinComponent {
    private val callUseCase: CallUseCase by inject()
    private val profileUseCase: ProfileUseCase by inject()
    
    val isConnectedWs = callUseCase.isConnectedWs
    val isCallBackground = callUseCase.isCallBackground
    
    val isIncomingCall = callUseCase.isIncomingCall
    
    private val _isConnectedWebrtc = MutableStateFlow(false)
    val isConnectedWebrtc: StateFlow<Boolean> get() = _isConnectedWebrtc
    
    private val _localStream = MutableStateFlow<MediaStream?>(null)
    val localStream: StateFlow<MediaStream?> get() = _localStream
    
    private val _remoteVideoTrack = MutableStateFlow<VideoStreamTrack?>(null)
    val remoteVideoTrack: StateFlow<VideoStreamTrack?> get() = _remoteVideoTrack
    
    private val _callState = MutableStateFlow(PeerConnectionState.New)
    val callState: StateFlow<PeerConnectionState> get() = _callState
    
    val _iceState = MutableStateFlow<IceConnectionState>(IceConnectionState.New)
    val iceState: StateFlow<IceConnectionState> get() = _iceState
    
    // Флаг для управления наблюдением
    private var isObserving = MutableStateFlow(true)
    
    var answerData = MutableStateFlow<JsonObject?>(null)
    
    val isScreenOn = MutableStateFlow(false)
    
    
    val localStreamm = callUseCase.localStream
    
    val isCallActive = callUseCase.isCallActive
    
    
    // Таймер
    private val _timer = MutableStateFlow("00:00:00")
    val timer: StateFlow<String> get() = _timer
    
    private var timerJob: Job? = null
    private var elapsedSeconds = 0L
    
    val isTimerRunning = MutableStateFlow(false)
    
    private val _userIcon = MutableStateFlow<String?>(null)
    val userIcon: StateFlow<String?> get() = _userIcon
    
    val callScreenInfo = MutableStateFlow<Screen?>(null)
    
    fun updateUserIcon(icon: String?) {
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
                    println("callStateNew $callStateNew")
                }
            }
            .launchIn(viewModelScope)
    }
    
    private fun observeStreams() {
        callUseCase.localStream
            .onEach { localStreamNew ->
                if (isObserving.value) {
                    _localStream.value = localStreamNew
                    println("_localStream $_localStream")
                }
            }
            .launchIn(viewModelScope)
        
        callUseCase.remoteVideoTrack
            .onEach { remoteVideoTrackNew ->
                if (isObserving.value) {
                    _remoteVideoTrack.value = remoteVideoTrackNew
                    println("remoteVideoTrackNew $remoteVideoTrackNew")
                }
            }
            .launchIn(viewModelScope)
    }
    
    private fun observeIsConnectedWebrtc() {
        callUseCase.isConnectedWebrtc
            .onEach { isConnectedWebrtcNew ->
                if (isObserving.value) {
                    _isConnectedWebrtc.value = isConnectedWebrtcNew
                    println("isConnectedWebrtcNew $isConnectedWebrtcNew")
                }
            }
            .launchIn(viewModelScope)
    }
    
    
    fun getWsSession() {
        viewModelScope.launch {
            println("dsadada ${callUseCase.getWsSession()}")
        }
    }
    
    fun connectionBackgroundWs(userId: String) {
        viewModelScope.launch {
            println("dsadada")
            callUseCase.connectionBackgroundWs(userId)
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
    
    fun answerCallBackground() {
        callUseCase.answerCallBackground()
    }
    
    fun rejectCall(navigator: Navigator, userId: String) {
        viewModelScope.launch {
            val isRejectCall = callUseCase.rejectCall(navigator, userId)
            
            if (isRejectCall) {
                
                navigator.push(MainScreen())
                
            }
        }
        
    }
    
    
    fun rejectCallBackground(userId: String) {
        viewModelScope.launch {
            callUseCase.rejectCallBackground(userId)
            
            closeApp()
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
    
    fun initCall(callCase: String, userId: String) {
        println("dsadadadadad ${callUseCase.wsSession.value}")
        
        viewModelScope.launch {
            if (callUseCase.wsSession.value != null) {
                when (callCase) {
                    "Call" -> {
                        updateOtherUserId(userId)
                        makeCall(userId)
                    }
                    
                    "IncomingCall" -> answerCall()
                    "IncomingBackgroundCall" -> answerCallBackground()
                }
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
    
}
