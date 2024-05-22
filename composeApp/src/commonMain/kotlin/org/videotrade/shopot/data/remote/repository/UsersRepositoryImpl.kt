package org.videotrade.shopot.data.remote.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.repository.ChatsRepository

class ChatsRepositoryImpl : ChatsRepository {
    
    
    private val _chats = MutableStateFlow<List<ChatItem>>(
        listOf(
        
        )
    )
    override val chats: StateFlow<List<ChatItem>> get() = _chats
    
    
    override fun getChats(): List<ChatItem> {
        
        return chats.value
        
    }
    
    
    override fun delChat(chat: ChatItem) {
//        _chats= _users.value.filter { it.id != user.id }
    }
    
    
    override fun addChat(chat: ChatItem) {
        
//        users.add(user)
    }
    
    override fun addChats(chatsInit: MutableList<ChatItem>) {
        _chats.value = chatsInit
        
    }
    
}