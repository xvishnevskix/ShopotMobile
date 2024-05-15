package org.videotrade.shopot.presentation.screens.chat

import dev.icerock.moko.mvvm.viewmodel.ViewModel
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

class ChatViewModel : ViewModel(), KoinComponent {
    private val chatUseCase: ChatUseCase by inject()
    private val profileUseCase: ProfileUseCase by inject()
    
    private val _messages = MutableStateFlow<List<MessageItem>>(listOf())
    
    val messages: StateFlow<List<MessageItem>> = _messages.asStateFlow()
    
    
    val profile = MutableStateFlow<ProfileDTO?>(null)
    
    
    init {
        loadMessages()
        
        
        profile.value = profileUseCase.getProfile()
    }
    
    private fun loadMessages() {
        viewModelScope.launch {
            _messages.value = chatUseCase.getMessages()
            
        }
    }
    
    fun deleteMessage(message: MessageItem) {
        viewModelScope.launch {
        
        }
    }
    
    
    fun addMessage(message: MessageItem) {
        viewModelScope.launch {
            chatUseCase.addMessage(message)
            println("dasdadada")
            val updatedUsers =
                _messages.value.toMutableList()  // Создаем изменяемую копию текущего списка
            updatedUsers.add(0, message)
            
            println("chat $updatedUsers")// Добавляем новый чат
            _messages.value = updatedUsers                 // Обновляем значение StateFlow
        }
    }
}