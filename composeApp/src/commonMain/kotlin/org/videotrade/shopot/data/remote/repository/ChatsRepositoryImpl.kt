package org.videotrade.shopot.data.remote.repository

import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.koin.mp.KoinPlatform
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.repository.ChatsRepository
import org.videotrade.shopot.domain.usecase.ProfileUseCase
import org.videotrade.shopot.presentation.screens.common.CommonViewModel

class ChatsRepositoryImpl : ChatsRepository {
    
    
    private val _chats = MutableStateFlow<List<ChatItem>>(
        listOf(
        
        )
    )
    override val chats: StateFlow<List<ChatItem>> get() = _chats
    
    private val _currentChat = MutableStateFlow(
        ""
    )
    
    override val currentChat: StateFlow<String> get() = _currentChat
    
    
    override fun getChats(): List<ChatItem> {
        
        return chats.value
        
    }
    
    
    override suspend fun getChatsInBack(wsSession: WebSocketSession, userId: String) {
        try {
            val jsonContent = Json.encodeToString(
                buildJsonObject {
                    put("action", "getUserChats")
                    put("userId", userId)
                }
            )
            println("jsonContent $jsonContent")
            
            wsSession.send(Frame.Text(jsonContent))
            
            println("Message sent successfully")
        } catch (e: Exception) {
            println("Failed to send message: ${e.message}")
        }
        
        
    }
    
    
    override fun updateLastMessageChat(messageItem: MessageItem) {
        val profileUseCase: ProfileUseCase =
            KoinPlatform.getKoin().get()
        
        _chats.update { currentChats ->
            currentChats.map { chatItem ->
                if (chatItem.chatId == messageItem.chatId) {
                    println("11111")
                    if (currentChat.value == chatItem.chatId) {
                        println("22222")
                        
                        chatItem.copy(lastMessage = messageItem)
                    } else {
                        if (profileUseCase.getProfile().id !== messageItem.fromUser) {
                            println("33333 ${chatItem.userId} ${messageItem.fromUser}")
                            
                            chatItem.copy(lastMessage = messageItem, unread = chatItem.unread + 1)
                            
                        } else {
                            chatItem.copy(lastMessage = messageItem)
                        }
                    }
                } else {
                    chatItem
                }
            }
        }
    }
    
    override fun updateReadLastMessageChat(messageItem: MessageItem) {
        _chats.update { currentChats ->
            currentChats.map { chatItem ->
                if (chatItem.chatId == messageItem.chatId) {
                    if (chatItem.lastMessage?.id == messageItem.id) {
                        chatItem.copy(lastMessage = messageItem)
                    } else {
                        chatItem
                    }
                } else {
                    chatItem
                }
            }
        }
    }
    
    
    override fun setZeroUnread(chat: ChatItem) {
        _chats.update { currentChats ->
            currentChats.map { chatItem ->
                if (chatItem.chatId == chat.chatId) {
                    chatItem.copy(unread = 0)
                } else {
                    chatItem
                }
            }
        }
    }
    
    override fun setCurrentChat(chatValue: String) {
        _currentChat.value = chatValue
    }
    
    
    override fun delChat(chat: ChatItem) {
//        _chats= _users.value.filter { it.id != user.id }
    }
    
    
    override fun addChat(chat: ChatItem) {
        _chats.update { currentChats ->
            currentChats + chat
        }
    }
    
    override fun addChats(chatsInit: MutableList<ChatItem>) {
        _chats.value = chatsInit
    }
    
    
    override fun clearData() {
        _chats.value = emptyList()
    }
}