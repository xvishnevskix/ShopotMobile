package org.videotrade.shopot.presentation.screens.main

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.websocket.Frame
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.domain.model.UserItem
import org.videotrade.shopot.domain.usecase.ProfileUseCase
import org.videotrade.shopot.domain.usecase.UsersUseCase
import org.videotrade.shopot.domain.usecase.WsUseCase
import org.videotrade.shopot.presentation.components.Main.WebRTCMessage

class MainViewModel : ViewModel(), KoinComponent {
    private val userUseCase: UsersUseCase by inject()
    private val profileUseCase: ProfileUseCase by inject()
    private val WsUseCase: WsUseCase by inject()
    
    
     val _wsSession = MutableStateFlow<DefaultClientWebSocketSession?>(null)
    val wsSession: StateFlow<DefaultClientWebSocketSession?> get() = _wsSession.asStateFlow()
    
    private val _chats = MutableStateFlow<List<UserItem>>(listOf())
    
    val chats: StateFlow<List<UserItem>> = _chats.asStateFlow()
    
    
    val profile = MutableStateFlow<ProfileDTO?>(null)
    
    
    init {
        viewModelScope.launch {
            downloadProfile()
            
            profile.collect { updatedProfile ->
                
                profile.value?.id?.let {
                    println("dadsadada")
                    observeWsConnection(it)
                    
                    
                    connectionWs(it)
                }
                
                updatedProfile?.let {
                    
                    loadUsers()
                    
                }
            }
        }
    }
    
    
    private fun observeWsConnection(userId: String) {
        WsUseCase.wsSession
            .onEach { wsSessionNew ->
                
                if (wsSessionNew != null) {
                    println("wsSessionNew $wsSessionNew")
                    _wsSession.value = wsSessionNew
                    
                    
                    val jsonContent = Json.encodeToString(
                        buildJsonObject {
                            put("action", "getUserChats")
                            put("userId", userId)
                        }
                    )
                    
                    try {
                        
                        println("jsonContent $jsonContent")
                        
                        wsSessionNew.send(Frame.Text(jsonContent))
                        
                        println("Message sent successfully")
                    } catch (e: Exception) {
                        println("Failed to send message: ${e.message}")
                    }
                    
                    
                    
                }
                
            }
            .launchIn(viewModelScope)
    }
    
    fun connectionWs(userId: String) {
        viewModelScope.launch {
            WsUseCase.connectionWs(userId)
        }
    }
    
    fun getWsSession() {
        viewModelScope.launch {
            val ws = WsUseCase.getWsSession() ?: return@launch
            
            
        }
    }
    
    
    fun getProfile() {
        viewModelScope.launch {
            val profileCase = profileUseCase.downloadProfile() ?: return@launch
            
            
            profile.value = profileCase
            
            
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
            _chats.value = userUseCase.getUsers()
        }
    }
    
    fun deleteUserItem(user: UserItem) {
        viewModelScope.launch {
            userUseCase.delUser(user)
            // После удаления обновить список
            _chats.update { currentUsers ->
                currentUsers.filter { it.id != user.id }
            }
        }
    }
    
    
    fun addUserItem(user: UserItem) {
        viewModelScope.launch {
            userUseCase.addUser(user)
            // Обновляем список чатов, добавляя новый элемент, если его ещё нет в списке
            val updatedUsers =
                _chats.value.toMutableList()  // Создаем изменяемую копию текущего списка
            if (!updatedUsers.any { it.id == user.id }) {    // Проверяем, нет ли уже такого чата в списке
                updatedUsers.add(user)                      // Добавляем новый чат
                _chats.value = updatedUsers                 // Обновляем значение StateFlow
            }
        }
    }
}