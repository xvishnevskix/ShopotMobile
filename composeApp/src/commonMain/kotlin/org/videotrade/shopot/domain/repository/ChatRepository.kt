package org.videotrade.shopot.domain.repository

import org.videotrade.shopot.domain.model.MessageItem

interface ChatRepository {


    fun getMessages(): List<MessageItem>

    fun delMessage(message: MessageItem)
    fun addMessage(message: MessageItem)


}