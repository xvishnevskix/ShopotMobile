package org.videotrade.shopot.domain.repository

import kotlinx.coroutines.flow.StateFlow
import org.videotrade.shopot.domain.model.MessageItem

interface ChatRepository {
    
    fun initMessages(messages: List<MessageItem>)
    fun getMessages(): StateFlow<List<MessageItem>>
    
    fun delMessage(message: MessageItem)
    fun addMessage(message: MessageItem)
    
    
}