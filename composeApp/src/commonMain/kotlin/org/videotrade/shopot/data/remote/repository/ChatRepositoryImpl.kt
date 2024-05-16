package org.videotrade.shopot.data.remote.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.repository.ChatRepository

class ChatRepositoryImpl : ChatRepository {
    private val _messages = MutableStateFlow<List<MessageItem>>(
        listOf(
        )
    )
    
    
    override fun initMessages(messages: List<MessageItem>) {
        _messages.value = messages
    }
    
    override fun addMessage(message: MessageItem) {
        _messages.value = listOf(message) + _messages.value
    }
    
    
    // Теперь это Flow
    override fun getMessages(): StateFlow<List<MessageItem>> = _messages.asStateFlow()
    
    
    override fun delMessage(message: MessageItem) {
        _messages.value = _messages.value.filter { it.id != message.id }
    }
}
