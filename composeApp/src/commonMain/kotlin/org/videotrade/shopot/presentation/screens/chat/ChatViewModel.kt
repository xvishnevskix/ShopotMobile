package org.videotrade.shopot.presentation.screens.chat


import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.videotrade.shopot.data.origin
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.domain.usecase.ChatUseCase
import org.videotrade.shopot.domain.usecase.ProfileUseCase
import org.videotrade.shopot.domain.usecase.WsUseCase

class ChatViewModel : ViewModel(), KoinComponent {
    private val chatUseCase: ChatUseCase by inject()
    private val profileUseCase: ProfileUseCase by inject()
    private val wsUseCase: WsUseCase by inject()

    private val _messages = MutableStateFlow<List<MessageItem>>(listOf())

    val messages: StateFlow<List<MessageItem>> = _messages.asStateFlow()

    val messagesA: StateFlow<List<MessageItem>> = chatUseCase.getMessages()


    val profile = MutableStateFlow(ProfileDTO())
    val ws = MutableStateFlow<DefaultClientWebSocketSession?>(null)


    init {


        viewModelScope.launch {


            chatUseCase.getMessages().collect {

                println("it313123131 $it")
                _messages.value = it
            }


        }
    }


    fun sendReadMessage(messageId: String) {
        viewModelScope.launch {
            chatUseCase.sendReadMessage(messageId, profile.value.id)
        }
    }


    fun getMessagesBack(chatId: String) {
        viewModelScope.launch {
            chatUseCase.getMessagesBack(chatId)
        }
    }

    fun sendMessage(
        content: String? = null,
        fromUser: String,
        chatId: String,
        notificationToken: String?,
        attachments: List<String>? = null
    ) {
        viewModelScope.launch {
            chatUseCase.sendMessage(
                MessageItem(
                    content = content,
                    fromUser = fromUser,
                    chatId = chatId,
                    anotherRead = false,
                    iread = false,
                    attachments = null
                ),
                attachments
            )

            sendNotify("Новое сообщение", content, notificationToken)

        }
    }


    fun sendAttachments(
        content: String?,
        fromUser: String,
        chatId: String,
        file: ByteArray
    ) {
        viewModelScope.launch {


            val fileId = origin().sendFile(
                "file/upload",
                file, "image"
            )


            if (fileId !== null)
                sendMessage(
                    content,
                    fromUser,
                    chatId,
                    notificationToken = null,
                    listOf(fileId.id)
                )
        }
    }


    fun sendNotify(
        title: String,
        content: String? = "Уведомление",
        notificationToken: String?
    ) {
        viewModelScope.launch {
            if (notificationToken !== null) {
                val jsonContent = Json.encodeToString(
                    buildJsonObject {
                        put("title", title)
                        put("body", content)
                        put("notificationToken", notificationToken)

                    }
                )

                origin().post<Any>("notification/notify", jsonContent)
            }
        }
    }

    fun deleteMessage(message: MessageItem) {
        viewModelScope.launch {
            chatUseCase.delMessage(message)
        }
    }

    fun clearMessages() {
        viewModelScope.launch {
            chatUseCase.clearMessages()
        }
    }

    fun getProfile() {
        viewModelScope.launch {
            profile.value = profileUseCase.getProfile()!!
        }
    }


}


