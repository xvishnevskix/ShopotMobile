package org.videotrade.shopot.domain.repository

import kotlinx.coroutines.flow.StateFlow
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.model.MessageItem

interface ChatsRepository {

    
    val chats: StateFlow<List<ChatItem>>
    
    
    fun getChats(): List<ChatItem>
    
    
    fun updateLastMessageChat(messageItem: MessageItem)
    fun delChat(chat: ChatItem)
    fun addChat(chat: ChatItem)
    fun addChats(chatsInit: MutableList<ChatItem>)
    

}