package org.videotrade.shopot.domain.usecase

import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.repository.ChatRepository

class ChatUseCase : KoinComponent {
    private val repository: ChatRepository by inject()
    
    fun getMessages(): StateFlow<List<MessageItem>> {
        return repository.getMessages()
    }
    
    
    suspend  fun readMessage(messageId: String, userId: String) {
        return repository.readMessage(messageId,userId)
    }
    
    
    suspend fun getMessagesBack(chatId: String){
        return repository.getMessagesBack(chatId)
    }
    
    
    fun initMessages(messages: List<MessageItem>) {
        return repository.initMessages(messages)
    }
    
    suspend  fun delMessage(message: MessageItem) {
        return repository.delMessage(message)
    }
    
      fun clearMessages() {
        return repository.clearMessages()
    }
    
    
    suspend fun addMessage(message: MessageItem) {
        return repository.addMessage(message)
    }
    
    
    suspend fun sendMessage(message: MessageItem) {
        return repository.sendMessage(message)
    }
    
    
}