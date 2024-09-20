package org.videotrade.shopot.data.remote.repository

import io.ktor.websocket.Frame
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.put
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.repository.ChatRepository
import org.videotrade.shopot.domain.usecase.WsUseCase

class ChatRepositoryImpl : ChatRepository, KoinComponent {
    private val _messages = MutableStateFlow<List<MessageItem>>(
        emptyList()
    )
    val wsUseCase: WsUseCase by inject()
    
    private val messageCount = MutableStateFlow(0)
    
    
    override fun setCount(count: Int) {
        messageCount.value = count
        
    }
    
    
    override fun implementCount() {
        messageCount.value += 1
        
    }
    
    override fun initMessages(messages: List<MessageItem>) {
        _messages.value += messages
    }
    
    
    override suspend fun sendMessage(
        message: MessageItem, attachments: List<String>?,
        answerMessageId: String?,
    ) {
        try {
            val jsonContent = Json.encodeToString(
                buildJsonObject {
                    put("action", "sendMessage")
                    put("content", message.content)
                    put("fromUser", message.fromUser)
                    put("chatId", message.chatId)
                    put("answerMessage", answerMessageId)
                    put("forwardMessage", message.forwardMessage)
                    put(
                        "attachments",
                        Json.encodeToJsonElement(attachments)
                    )
                }
            )
            println("jsonContent $jsonContent")
            wsUseCase.wsSession.value?.send(Frame.Text(jsonContent))
            
        } catch (e: Exception) {
            println("Failed to send message: ${e.message}")
        }
    }
    
    override suspend fun sendUploadMessage(
        message: MessageItem,
        attachments: List<String>?,
        answerMessageId: String?
    ) {
        try {
            val jsonContent = Json.encodeToString(
                buildJsonObject {
                    put("action", "sendUploadMessage")
                    put("content", message.content)
                    put("fromUser", message.fromUser)
                    put("uploadId", message.uploadId)
                    put("chatId", message.chatId)
                    put("answerMessage", answerMessageId)
//                    put("action", "sendMessage")
//                    put("content", message.content)
//                    put("fromUser", message.fromUser)
//                    put("chatId", message.chatId)
                    put(
                        "attachments",
                        Json.encodeToJsonElement(attachments)
                    )
                }
            )
            println("jsonContent $jsonContent")
            wsUseCase.wsSession.value?.send(Frame.Text(jsonContent))
            
        } catch (e: Exception) {
            println("Failed to send message: ${e.message}")
        }
    }
    
    
    override fun addMessage(message: MessageItem) {
        _messages.value = listOf(message) + _messages.value
        
        
        println(
            "_messages_messages ${
                _messages.value.size
                
            }"
        )
    }
    
    override fun updateUploadMessage(message: MessageItem) {
        _messages.update { currentChat ->
            currentChat.map { messageItem ->
                println("messageItem.id == message.uploadId ${messageItem.uploadId} ${message.uploadId}")
                
                if (messageItem.uploadId == message.uploadId) {
                    messageItem.copy(
                        id = message.id,
                        fromUser = message.fromUser,
                        content = message.content,
                        forwardMessage = message.forwardMessage,
                        answerMessage = message.answerMessage,
                        replaces = message.replaces,
                        created = message.created,
                        isDeleted = message.isDeleted,
                        chatId = message.chatId,
                        anotherRead = message.anotherRead,
                        iread = message.iread,
                        attachments = message.attachments,
                        upload = null,
                        uploadId = message.uploadId
                    )
                } else {
                    messageItem
                }
            }
        }
        
        
    }
    
    
    override fun getMessages(): StateFlow<List<MessageItem>> = _messages.asStateFlow()
    
    
    override suspend fun sendReadMessage(messageId: String, userId: String) {
        
        try {
            
            val jsonContent = Json.encodeToString(
                buildJsonObject {
                    put("action", "readMessage")
                    put("messageId", messageId)
                    put("readerId", userId)
                }
            )
            
            wsUseCase.wsSession.value?.send(Frame.Text(jsonContent))
            
        } catch (e: Exception) {
            println("Failed to send message: ${e.message}")
        }
    }
    
    
    override fun readMessage(messageId: String) {
        
        println("messageId!!!!!!  $messageId")
        
        _messages.update { currentChat ->
            currentChat.map { messageItem ->
                
                if (messageItem.id == messageId) {
                    println("messageId!!!!!! ${messageItem.id} $messageId")
                    
                    messageItem.copy(anotherRead = true)
                } else {
                    messageItem
                }
            }
        }
        
    }
    
    
    override suspend fun getMessagesBack(chatId: String) {
        try {
            val jsonContent = Json.encodeToString(
                buildJsonObject {
                    put("action", "getMessages")
                    put("chatId", chatId)
                    put("page", messageCount.value)
                }
            )
            println("jsonContent4144141 ${jsonContent}")
            wsUseCase.wsSession.value?.send(Frame.Text(jsonContent))
            
            
        } catch (e: Exception) {
            println("Failed to send message: ${e.message}")
        }
        
    }
    
    
    override suspend fun delMessage(message: MessageItem) {
        _messages.value = _messages.value.filter { it.id != message.id }
        
        try {
            
            val jsonContent = Json.encodeToString(
                buildJsonObject {
                    put("action", "removeMessage")
                    put("messageId", message.id)
                }
            )
            
            
            println("jsonContent $jsonContent")
            
            wsUseCase.wsSession.value?.send(Frame.Text(jsonContent))
            
        } catch (e: Exception) {
            println("Failed to send message: ${e.message}")
        }
    }
    
    override fun clearMessages() {
        _messages.value = emptyList()
        
    }
    
    override fun clearData() {
        _messages.value = emptyList()
    }
}
