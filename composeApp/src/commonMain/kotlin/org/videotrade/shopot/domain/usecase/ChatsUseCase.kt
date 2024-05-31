package org.videotrade.shopot.domain.usecase

import kotlinx.coroutines.flow.StateFlow
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.repository.ChatsRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.videotrade.shopot.domain.model.MessageItem

class ChatsUseCase : KoinComponent {
    private val repository: ChatsRepository by inject()
    
    val chats: StateFlow<List<ChatItem>> get() = repository.chats
    
    

    fun getChats(): List<ChatItem> {
        
        
        return repository.getChats()
    }
    
    
    
    fun updateLastMessageChat(messageItem: MessageItem) {
        return repository.updateLastMessageChat(messageItem)
    }
    fun delChat(user: ChatItem) {
        return repository.delChat(user)
    }

    fun addChat(user: ChatItem) {
        return repository.addChat(user)
    }
    
    fun addChats(chatsInit: MutableList<ChatItem>) {
        return repository.addChats(chatsInit)
    }

}