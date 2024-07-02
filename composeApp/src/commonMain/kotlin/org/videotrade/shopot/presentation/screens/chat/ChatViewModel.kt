package org.videotrade.shopot.presentation.screens.chat


import androidx.compose.runtime.MutableState
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
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.domain.usecase.ChatUseCase
import org.videotrade.shopot.domain.usecase.ProfileUseCase
import org.videotrade.shopot.domain.usecase.WsUseCase
import org.videotrade.shopot.multiplatform.AudioFactory

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
        userId: String? = null,
        notificationToken: String?,
        attachments: List<String>? = null,
        login: String? = null
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
            
            sendNotify("Новое сообщение от $login ", content, notificationToken)


//            val httpClient = HttpClient {
//                install(WebSockets)
//
//            }
//
//            println("aaaaaa3123131 ${userId} $fromUser")
//
//
//            var profileId = getValueInStorage("profileId")
//
//            try {
//                httpClient.webSocket(
//                    method = HttpMethod.Get,
//                    host = "192.168.31.223",
//                    port = 3001,
//                    path = "/message",
//                    request = {
//                        profileId?.let { url.parameters.append("callerId", it) }
//                    }
//                ) {
//
//                    val jsonContent = Json.encodeToString(
//                        buildJsonObject {
//                            put("type", "call")
//                            put("calleeId", userId)
//
//                        }
//                    )
//
//                    send(Frame.Text(jsonContent))
//                }
//            } catch (e: Exception) {
//
//            }
        }
    }
    
    
    fun sendAttachments(
        content: String?,
        fromUser: String,
        chatId: String,
        contentType: String,
        fileName: String,
        fileDir: String? = null,
        fileBytes: ByteArray? = null,
        
        ) {
        viewModelScope.launch {
            
            println("send Audio")
            val fileId = origin().sendFile(
                "file/upload",
                fileDir,
                contentType,
                fileName,
                fileBytes
            )
            
            println("fileId ${fileId}")
            
            if (fileId !== null)
                sendMessage(
                    content = content,
                    fromUser = fromUser,
                    chatId = chatId,
                    notificationToken = null,
                    attachments = listOf(fileId.id)
                )
        }
    }
    
    fun sendLargeFile(
        content: String?,
        fromUser: String,
        chatId: String,
        contentType: String,
        fileName: String,
        fileDir: String? = null,
        progress: MutableState<Float>,
    ) {
        viewModelScope.launch {
            
            val fileId = origin().sendLargeFile(
                "file/upload",
                fileDir,
                contentType,
                fileName,
                progress
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

