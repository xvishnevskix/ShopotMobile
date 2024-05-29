package org.videotrade.shopot.domain.repository

import kotlinx.coroutines.flow.StateFlow
import org.videotrade.shopot.domain.model.MessageItem

interface ChatRepository {
    
    fun initMessages(messages: List<MessageItem>)
    fun getMessages(): StateFlow<List<MessageItem>>
    
    
    suspend fun readMessage(messageId: String, userId: String)
    
    
    suspend fun getMessagesBack(chatId: String)
    
    suspend fun delMessage(message: MessageItem)
    suspend fun addMessage(message: MessageItem)
    suspend fun sendMessage(message: MessageItem)
    
    
}