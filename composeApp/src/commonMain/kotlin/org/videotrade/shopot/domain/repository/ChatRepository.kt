package org.videotrade.shopot.domain.repository

import kotlinx.coroutines.flow.StateFlow
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.model.MessageItem

interface ChatRepository {

    val currentChat: StateFlow<ChatItem?>
    val userStatuses: StateFlow<Map<String, Pair<String, Long>>>

    fun setMessagePage(page: Int)

    fun implementCount()

    fun initMessages(messages: List<MessageItem>)
    fun getMessages(): StateFlow<List<MessageItem>>

    suspend fun sendReadMessage(messageId: String, userId: String)

    suspend fun getMessagesBack(chatId: String)

    suspend fun delMessage(message: MessageItem)
    fun delMessageById(messageId: String, chatId: String)

    fun readMessage(messageId: String)
    fun clearMessages()


    fun addMessage(message: MessageItem)
    fun updateUploadMessage(message: MessageItem)


    suspend fun sendMessage(
        message: MessageItem,
        attachments: List<String>?,
        answerMessageId: String?
    )

    suspend fun sendUploadMessage(
        message: MessageItem,
        attachments: List<String>?,
        answerMessageId: String?,
        fileType: String
    )

    fun setCurrentChat(chat: ChatItem)

    fun updateUserStatus(userId: String, status: String)

    suspend fun sendUserStatus(action: String)

    fun clearData()
}