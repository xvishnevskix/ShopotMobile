package org.videotrade.shopot.domain.repository

import kotlinx.coroutines.flow.StateFlow
import org.videotrade.shopot.domain.model.ChatItem

interface ChatsRepository {

    
    val chats: StateFlow<List<ChatItem>>
    
    
    fun getChats(): List<ChatItem>

    fun delChat(chat: ChatItem)
    fun addChat(chat: ChatItem)
    fun addChats(chat: MutableList<ChatItem>)
    

}