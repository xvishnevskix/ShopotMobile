package org.videotrade.shopot.presentation.screens.chat


import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.domain.usecase.ChatUseCase
import org.videotrade.shopot.domain.usecase.ProfileUseCase
import org.videotrade.shopot.domain.usecase.WsUseCase

class ChatViewModel : ViewModel(), KoinComponent {
    private val chatUseCase: ChatUseCase by inject()
    private val profileUseCase: ProfileUseCase by inject()
    private val wsUseCase: WsUseCase by inject()
    
    private val _messages = MutableStateFlow<List<MessageItem>>(listOf())
    
    val messages: StateFlow<List<MessageItem>> = _messages.asStateFlow()
    
    val profile = MutableStateFlow(ProfileDTO())
    val ws = MutableStateFlow<DefaultClientWebSocketSession?>(null)
    
    
    init {
        
        
        profile.value = profileUseCase.getProfile()!!
        
        
        viewModelScope.launch {
            
            
            chatUseCase.getMessages().collect {
                _messages.value = it
            }
            
            
        }
    }
    
    
    fun readMessage(messageId: String) {
        viewModelScope.launch {
            chatUseCase.readMessage(messageId, profile.value.id)
        }
    }
    
    
    fun getMessagesBack(chatId: String) {
        viewModelScope.launch {
            chatUseCase.getMessagesBack(chatId)
        }
    }
    
    fun sendMessage(message: MessageItem) {
        viewModelScope.launch {
            chatUseCase.sendMessage(message)
        }
    }
    
    
    fun deleteMessage(message: MessageItem) {
        viewModelScope.launch {
            chatUseCase.delMessage(message)
        }
    }
    
    fun clearMessages() {
        viewModelScope.launch {
            chatUseCase.clearMessages()
        }
    }
}


