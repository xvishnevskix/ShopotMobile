package org.videotrade.shopot.presentation.screens.main

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.domain.usecase.ChatsUseCase
import org.videotrade.shopot.domain.usecase.ProfileUseCase
import org.videotrade.shopot.domain.usecase.WsUseCase

class MainViewModel : ViewModel(), KoinComponent {
    private val userUseCase: ChatsUseCase by inject()
    private val profileUseCase: ProfileUseCase by inject()
    private val WsUseCase: WsUseCase by inject()
    
    
    val _wsSession = MutableStateFlow<DefaultClientWebSocketSession?>(null)
    val wsSession: StateFlow<DefaultClientWebSocketSession?> get() = _wsSession.asStateFlow()
    
    private val _chats = MutableStateFlow<List<ChatItem>>(emptyList())
    
    val chats: StateFlow<List<ChatItem>> = _chats.asStateFlow()
    
    
    val profile = MutableStateFlow<ProfileDTO?>(null)

    
    
    init {
        viewModelScope.launch {
            
            getProfile()
            
            loadUsers()
            
            observeUsers()
            profile.collect {
                if (it !== null)
                    getWsSession()
            }
        }
    }
    
    private fun observeUsers() {
        userUseCase.chats.onEach { newUsers ->
            _chats.value = newUsers
            
        }.launchIn(viewModelScope)
        
    }

    
    fun connectionWs(userId: String) {
        viewModelScope.launch {
            WsUseCase.connectionWs(userId)
        }
    }
    
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