package org.videotrade.shopot.data.remote.repository

import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.repository.ChatRepository

class ChatRepositoryImpl : ChatRepository {

    private var messages =
        mutableListOf(
            MessageItem("1", "Privet1",true, "", "Антон", "Иванов", "", "", "1"),
            MessageItem("1", "Privet",true, "", "Антон", "Иванов", "", "", "2"),
            MessageItem("1", "Privet",true, "", "Антон", "Иванов", "", "", "1"),
            MessageItem("1", "Privet",true, "", "Антон", "Иванов", "", "", "1"),
            MessageItem("2", "Poka",true, "", "Мансур", "Дандаев", "", "", "1")
        )


    override fun getMessages(): List<MessageItem> {

        return messages.toList()

    }


    override fun delMessage(message: MessageItem) {
        messages = messages.filter { it.id != message.id }.toMutableList()
    }


    override fun addMessage(message: MessageItem) {

        messages.add(message)
    }

}