package org.videotrade.shopot.domain.repository

import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.websocket.WebSocketSession
import kotlinx.coroutines.flow.StateFlow
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.model.MessageItem

interface ChatsRepository {
    
    
    val chats: StateFlow<List<ChatItem>>
    
    val currentChat: StateFlow<String>
    
    val isLoadingChats: StateFlow<Boolean>
    
    
    fun getChats(): List<ChatItem>
    
    suspend fun getChatsInBack(wsSession: DefaultClientWebSocketSession, userId: String)
    
    fun updateLastMessageChat(messageItem: MessageItem)
    fun updateReadLastMessageChat(messageItem: MessageItem)
    
    fun setZeroUnread(chat: ChatItem)
    fun setCurrentChat(chatValue: String)
    
    
    suspend fun delChat(chat: ChatItem)
    fun addChat(chat: ChatItem)
    fun addChats(chatsInit: MutableList<ChatItem>)
    
    fun clearData()
    
    fun setIsLoadingValue(loadingValue: Boolean)
    
}