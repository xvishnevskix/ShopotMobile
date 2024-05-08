package org.videotrade.shopot.domain.usecase

import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.repository.ChatRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ChatUseCase : KoinComponent {
    private val repository: ChatRepository by inject()

    fun getMessages(): List<MessageItem> {
        return repository.getMessages()
    }


    fun delMessage(message: MessageItem) {
        return repository.delMessage(message)
    }

    fun addMessage(message: MessageItem) {
        return repository.addMessage(message)
    }

}