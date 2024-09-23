package org.videotrade.shopot.domain.repository

import kotlinx.coroutines.flow.StateFlow
import org.videotrade.shopot.domain.model.MessageItem

interface ChatRepository {
    
    
    
    fun setMessagePage(page: Int)

    fun implementCount()
    
    
    
    
    fun initMessages(messages: List<MessageItem>)
    fun getMessages(): StateFlow<List<MessageItem>>
    
    suspend fun sendReadMessage(messageId: String, userId: String)
    
    suspend fun getMessagesBack(chatId: String)
    
    suspend fun delMessage(message: MessageItem)
    fun readMessage(messageId: String)
    fun clearMessages()
    
    
    fun addMessage(message: MessageItem)
    fun updateUploadMessage(message: MessageItem)
    
    
     suspend fun sendMessage(message: MessageItem, attachments: List<String>?, answerMessageId: String?)
    suspend fun sendUploadMessage(
        message: MessageItem,
        attachments: List<String>?,
        answerMessageId: String?
    )
    
    fun clearData()
}