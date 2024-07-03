package org.videotrade.shopot.domain.repository

import io.ktor.websocket.WebSocketSession
import kotlinx.coroutines.flow.StateFlow
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.model.MessageItem

interface ChatsRepository {

    
    val chats: StateFlow<List<ChatItem>>
    
    
    fun getChats(): List<ChatItem>
    
    suspend fun getChatsInBack(wsSession: WebSocketSession, userId: String)
    
    fun updateLastMessageChat(messageItem: MessageItem)
    fun updateReadLastMessageChat(messageItem: MessageItem)
    
    fun setZeroUnread(chat: ChatItem)
    fun setCurrentChat(chatValue: String)
    
    
    fun delChat(chat: ChatItem)
    fun addChat(chat: ChatItem)
    fun addChats(chatsInit: MutableList<ChatItem>)
    
    fun clearData()
}