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
import org.koin.mp.KoinPlatform
import org.videotrade.shopot.api.encupsMessage
import org.videotrade.shopot.data.origin
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.domain.usecase.ChatUseCase
import org.videotrade.shopot.domain.usecase.ProfileUseCase
import org.videotrade.shopot.domain.usecase.WsUseCase
import org.videotrade.shopot.multiplatform.AudioFactory
import org.videotrade.shopot.multiplatform.CipherWrapper

class ChatViewModel : ViewModel(), KoinComponent {
    private val chatUseCase: ChatUseCase by inject()
    private val profileUseCase: ProfileUseCase by inject()
    private val wsUseCase: WsUseCase by inject()
    
    private val _messages = MutableStateFlow<List<MessageItem>>(listOf())
    
    val messages: StateFlow<List<MessageItem>> = _messages.asStateFlow()
    
    val profile = MutableStateFlow(ProfileDTO())
    
    
    val _currentChat = MutableStateFlow<ChatItem?>(null)
    val currentChat: StateFlow<ChatItem?> get() = _currentChat.asStateFlow()
    
    val ws = MutableStateFlow<DefaultClientWebSocketSession?>(null)
    
    
    val audioRecorder = MutableStateFlow(AudioFactory.createAudioRecorder())
    
    var isRecording = MutableStateFlow(false)
    
    
    var downloadProgress = MutableStateFlow(0f)
    
    init {
        
        
        viewModelScope.launch {
            
            
            chatUseCase.getMessages().collect {
                
                println("it313123131 $it")
                _messages.value = it
            }
            
            
        }
    }
    
    
    fun setCurrentChat(chat: ChatItem) {
        _currentChat.value = chat
    }
    
    fun setIsRecording(isRecordingNew: Boolean) {
        isRecording.value = isRecordingNew
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
    
    fun setCount(count: Int) {
        viewModelScope.launch {
            chatUseCase.setCount(count)
        }
    }
    
    fun implementCount() {
        viewModelScope.launch {
            chatUseCase.implementCount()
        }
    }
    
    
    fun sendMessage(
        content: String? = null,
        fromUser: String,
        chatId: String,
        notificationToken: String?,
        attachments: List<String>? = null,
        login: String? = null,
        isCipher: Boolean,
    ) {
        viewModelScope.launch {
            var contentSort = ""
            
            
            if (content !== null && isCipher) {
                val cipherWrapper: CipherWrapper = KoinPlatform.getKoin().get()
                
                val resEncups = encupsMessage(content, cipherWrapper)
                
                contentSort = Json.encodeToString(resEncups)
            } else {
                contentSort = content!!
            }
            
            chatUseCase.sendMessage(
                MessageItem(
                    content = contentSort,
                    fromUser = fromUser,
                    chatId = chatId,
                    anotherRead = false,
                    iread = false,
                    attachments = null
                ),
                attachments
            )
            println("сообщениесообщениесообщениесообщение")
            sendNotify("Новое сообщение от $login ", content, notificationToken)
        }
    }
    
    
    fun sendAttachments(
        content: String?,
        fromUser: String,
        chatId: String,
        contentType: String,
        fileName: String,
        fileDir: String
        ) {
        viewModelScope.launch {
            val fileId = origin().sendFile(
                fileDir,
                contentType,
                fileName,
            )
            
            if (fileId !== null)
                sendMessage(
                    content = content,
                    fromUser = fromUser,
                    chatId = chatId,
                    notificationToken = null,
                    attachments = listOf(fileId),
                    isCipher = false
                )
        }
    }
    
    fun sendLargeFileAttachments(
        content: String? = null,
        fromUser: String,
        chatId: String,
        uploadId: String,
        fileId: String
    ) {
        viewModelScope.launch {
            chatUseCase.sendUploadMessage(
                MessageItem(
                    content = content,
                    fromUser = fromUser,
                    chatId = chatId,
                    uploadId = uploadId,
                    anotherRead = false,
                    iread = false,
                    attachments = null
                ),
                listOf(fileId)
            )
        }
    }
    
    
    fun sendNotify(
        title: String,
        content: String? = "Уведомление",
        notificationToken: String?
    ) {
        viewModelScope.launch {
            println("Уведомление ${notificationToken}")
            
            if (notificationToken !== null) {
                val jsonContent = Json.encodeToString(
                    buildJsonObject {
                        put("title", title)
                        put("body", content)
                        put("notificationToken", notificationToken)
                        
                    }
                )
                
                println("Уведомление ${jsonContent}")
                
                origin().post<Any>("notification/notify", jsonContent)
            }
        }
    }
    
    fun addMessage(message: MessageItem) {
        
        chatUseCase.addMessage(message)
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

