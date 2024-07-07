package org.videotrade.shopot.presentation.screens.main

import cafe.adriel.voyager.navigator.Navigator
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.videotrade.shopot.api.delValueInStorage
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.domain.usecase.CallUseCase
import org.videotrade.shopot.domain.usecase.ChatsUseCase
import org.videotrade.shopot.domain.usecase.ProfileUseCase
import org.videotrade.shopot.domain.usecase.WsUseCase
import org.videotrade.shopot.presentation.screens.login.SignInScreen

class MainViewModel : ViewModel(), KoinComponent {
    private val profileUseCase: ProfileUseCase by inject()
    private val wsUseCase: WsUseCase by inject()
    private val callUseCase: CallUseCase by inject()
    private val chatsUseCase: ChatsUseCase by inject()
    
    val _wsSession = MutableStateFlow<DefaultClientWebSocketSession?>(null)
    val wsSession: StateFlow<DefaultClientWebSocketSession?> get() = _wsSession.asStateFlow()

//    val callWsSession: Flow<DefaultClientWebSocketSession?> get() = callUseCase.wsSession
    
    
    private val _chats = MutableStateFlow<List<ChatItem>>(emptyList())
    
    val chats: StateFlow<List<ChatItem>> = _chats.asStateFlow()
    
    
    val profile = MutableStateFlow(ProfileDTO())
    
    
    val navigator = MutableStateFlow<Navigator?>(null)
    
    
    init {
        viewModelScope.launch {
            
            
            observeUsers()
            profile.collect {
                
                getWsSession()
                
                
                println("userID : ${it.id}")
                
                navigator.value?.let { navigator -> callUseCase.connectionWs(it.id, navigator) }
                
            }
            
            
        }
    }
    
    fun getChatsInBack() {
        viewModelScope.launch {
            if (wsUseCase.wsSession.value !== null && wsUseCase.wsSession.value!!.isActive) {
                chatsUseCase.getChatsInBack(wsUseCase.wsSession.value!!, profile.value.id)
                observeUsers()
            }
        }
        
    }
    
    
    private fun List<Int>.toLocalDateTimeOrNull(): LocalDateTime? {
        return if (this.size >= 6) {
            LocalDateTime(
                this[0],
                this[1],
                this[2],
                this[3],
                this[4],
                this[5],
                this.getOrNull(6) ?: 0
            )
        } else {
            null
        }
    }
    
    private val EARLY_DATE = LocalDateTime(1970, 1, 1, 0, 0, 0)
    
    private fun observeUsers() {
        chatsUseCase.chats.onEach { newUsers ->
            _chats.value = newUsers.sortedByDescending { chat ->
                chat.lastMessage?.created?.toLocalDateTimeOrNull() ?: EARLY_DATE
            }
        }.launchIn(viewModelScope)
    }
    
    
    fun getNavigator(navigatorGet: Navigator) {
        
        navigator.value = navigatorGet
    }
    
    
    private fun getWsSession() {
        viewModelScope.launch {
            
            _wsSession.value = wsUseCase.getWsSession() ?: return@launch
            
        }
    }
    
    
    fun getProfile() {
        viewModelScope.launch {
            profile.value = profileUseCase.getProfile() ?: return@launch
        }
    }
    
    
    fun downloadProfile() {
        viewModelScope.launch {
            val profileCase = profileUseCase.downloadProfile() ?: return@launch
            
            
            profile.value = profileCase
            
            
        }
    }
    
    
    fun setZeroUnread(chat: ChatItem) {
        viewModelScope.launch {
            chatsUseCase.setZeroUnread(chat)
        }
    }
    
    
    fun setCurrentChat(chatValue: String) {
        viewModelScope.launch {
            chatsUseCase.setCurrentChat(chatValue)
        }
    }
    
    
    fun loadUsers() {
        viewModelScope.launch {
            
            println("prrrr ${chatsUseCase.chats.value}")
            _chats.value = chatsUseCase.chats.value
        }
    }
    
    
    fun leaveApp(navigator: Navigator) {
        viewModelScope.launch {
            
            _chats.value = emptyList()
            _wsSession.value = null
            
            profileUseCase.clearData()
            wsUseCase.clearData()
            callUseCase.clearData()
            chatsUseCase.clearData()
            
            delValueInStorage("accessToken")
            delValueInStorage("refreshToken")
            
            navigator.replace(SignInScreen())
            
        }
    }
}