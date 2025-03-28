package org.videotrade.shopot.domain.usecase

import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.repository.ChatRepository

class ChatUseCase : KoinComponent {
    private val repository: ChatRepository by inject()

    val currentChat: StateFlow<ChatItem?> get() = repository.currentChat

    val userStatuses: StateFlow<Map<String, Pair<String, Long>>> = repository.userStatuses


    fun setMessagePage(page: Int) {
        return repository.setMessagePage(page)

    }

    fun implementCount() {
        return repository.implementCount()

    }


    fun getMessages(): StateFlow<List<MessageItem>> {
        return repository.getMessages()
    }


    suspend fun sendReadMessage(messageId: String, userId: String) {
        return repository.sendReadMessage(messageId, userId)
    }


    suspend fun getMessagesBack(chatId: String) {
        return repository.getMessagesBack(chatId)
    }


    fun initMessages(messages: List<MessageItem>) {
        return repository.initMessages(messages)
    }

    suspend fun delMessage(message: MessageItem) {
        return repository.delMessage(message)
    }

    fun delMessageById(messageId: String, chatId: String) {
        return repository.delMessageById(messageId, chatId)
    }


    fun readMessage(messageId: String) {
        return repository.readMessage(messageId)
    }

    fun clearMessages() {
        return repository.clearMessages()
    }


    fun addMessage(message: MessageItem) {
        return repository.addMessage(message)
    }

    fun updateUploadMessage(message: MessageItem) {
        return repository.updateUploadMessage(message)
    }


    suspend fun sendMessage(
        message: MessageItem,
        attachments: List<String>?,
        answerMessageId: String?
    ) {
        return repository.sendMessage(message, attachments = attachments, answerMessageId)
    }

    suspend fun sendUploadMessage(
        message: MessageItem,
        attachments: List<String>?,
        answerMessageId: String?,
        fileType: String
    ) {
        return repository.sendUploadMessage(
            message,
            attachments = attachments,
            answerMessageId,
            fileType
        )
    }


    fun clearData() {
        repository.clearData()
    }


    fun setCurrentChat(chat: ChatItem) {
        return repository.setCurrentChat(chat)
    }




    suspend fun startListeningForStatusUpdates() {
        repository.listenForUserStatusUpdates()
    }

    suspend fun sendUserStatus(action: String) {
        repository.sendUserStatus(action)
    }



    suspend fun sendTypingStart() = sendUserStatus("startTyping")
    suspend fun sendTypingEnd() = sendUserStatus("stopTyping")
    suspend fun sendFileUploadStart() = sendUserStatus("startSendingFile")
    suspend fun sendFileUploadEnd() = sendUserStatus("stopSendingFile")
    suspend fun sendStickerChoosingStart() = sendUserStatus("startChoosingSticker")
    suspend fun sendStickerChoosingEnd() = sendUserStatus("stopChoosingSticker")
    suspend fun sendVoiceRecordingStart() = sendUserStatus("startRecordingVoice")
    suspend fun sendVoiceRecordingEnd() = sendUserStatus("stopRecordingVoice")
}