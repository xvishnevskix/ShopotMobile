package org.videotrade.shopot.presentation.screens.main

import cafe.adriel.voyager.navigator.Navigator
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.domain.usecase.CallUseCase
import org.videotrade.shopot.domain.usecase.ChatsUseCase
import org.videotrade.shopot.domain.usecase.ProfileUseCase
import org.videotrade.shopot.domain.usecase.WsUseCase

class MainViewModel : ViewModel(), KoinComponent {
    private val userUseCase: ChatsUseCase by inject()
    private val profileUseCase: ProfileUseCase by inject()
    private val WsUseCase: WsUseCase by inject()
    private val CallUseCase: CallUseCase by inject()
    
    
    val _wsSession = MutableStateFlow<DefaultClientWebSocketSession?>(null)
    val wsSession: StateFlow<DefaultClientWebSocketSession?> get() = _wsSession.asStateFlow()
    
    private val _chats = MutableStateFlow<List<ChatItem>>(emptyList())
    
    val chats: StateFlow<List<ChatItem>> = _chats.asStateFlow()
    
    
    val profile = MutableStateFlow<ProfileDTO?>(null)
    
    
    val navigator = MutableStateFlow<Navigator?>(null)
    
    
    init {
        viewModelScope.launch {
            
            
            getProfile()
            
            loadUsers()
            
            observeUsers()
            profile.collect {
                
                getWsSession()
                
                if (it !== null){
                    
                    
                    println("userID : ${it.id}")
                    
                    navigator.value?.let { navigator -> CallUseCase.connectionWs(it.id, navigator) }
                    
                }
                
            }
        }
    }

//    private fun observeUsers() {
//        userUseCase.chats.onEach { newUsers ->
//            _chats.value = newUsers
//
//        }.launchIn(viewModelScope)
//
//    }
    
    
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
        userUseCase.chats.onEach { newUsers ->
            _chats.value = newUsers.sortedByDescending { chat ->
                chat.lastMessage?.created?.toLocalDateTimeOrNull() ?: EARLY_DATE
            }
        }.launchIn(viewModelScope)
    }
    
    
    fun getNavigator(navigatorGet: Navigator) {
        
        navigator.value = navigatorGet
    }
    
    
//    fun connectionWs(userId: String) {
//        viewModelScope.launch {
//            WsUseCase.connectionWs(userId)
//        }
//    }
//
    private fun getWsSession() {
        viewModelScope.launch {
            
            _wsSession.value = WsUseCase.getWsSession() ?: return@launch
            
        }
    }
    
    
    private fun getProfile() {
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
    
    private fun loadUsers() {
        viewModelScope.launch {
            _chats.value = userUseCase.chats.value
        }
    }
    
}