package org.videotrade.shopot.domain.usecase

import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.websocket.WebSocketSession
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.repository.ChatsRepository

class ChatsUseCase : KoinComponent {
    private val repository: ChatsRepository by inject()
    
    val chats: StateFlow<List<ChatItem>> get() = repository.chats
    val currentChat: StateFlow<String> get() = repository.currentChat
    
    val isLoadingChats: StateFlow<Boolean> get() = repository.isLoadingChats
    
    
    
    
    fun getChats(): List<ChatItem> {
        
        
        return repository.getChats()
    }
    
    suspend fun getChatsInBack(wsSession: DefaultClientWebSocketSession, userId: String) {
        return repository.getChatsInBack(wsSession, userId)
    }
    
    fun updateLastMessageChat(messageItem: MessageItem) {
        return repository.updateLastMessageChat(messageItem)
    }
    
    fun updateReadLastMessageChat(messageItem: MessageItem) {
        return repository.updateReadLastMessageChat(messageItem)
    }
    
    
    fun delChat(user: ChatItem) {
        return repository.delChat(user)
    }
    
    fun setZeroUnread(chat: ChatItem) {
        return repository.setZeroUnread(chat)
    }
    
    fun setCurrentChat(chatValue: String) {
        return repository.setCurrentChat(chatValue)
    }
    
    
    fun addChat(user: ChatItem) {
        return repository.addChat(user)
    }
    
    fun addChats(chatsInit: MutableList<ChatItem>) {
        return repository.addChats(chatsInit)
    }
    
    fun clearData() {
        repository.clearData()
    }
    
    fun setIsLoadingValue(loadingValue: Boolean) {
        repository.setIsLoadingValue(loadingValue)
    }
}