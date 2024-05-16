package org.videotrade.shopot.presentation.screens.chat


import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.websocket.Frame
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.domain.usecase.ChatUseCase
import org.videotrade.shopot.domain.usecase.ProfileUseCase
import org.videotrade.shopot.domain.usecase.WsUseCase

class ChatViewModel : ViewModel(), KoinComponent {
    private val chatUseCase: ChatUseCase by inject()
    private val profileUseCase: ProfileUseCase by inject()
    private  val wsUseCase: WsUseCase by inject()
    
    private val _messages = MutableStateFlow<List<MessageItem>>(listOf())
    
    val messages: StateFlow<List<MessageItem>> = _messages.asStateFlow()
    
    val profile = MutableStateFlow<ProfileDTO?>(null)
    val ws = MutableStateFlow<DefaultClientWebSocketSession?>(null)
    
    
    init {
        
        
        profile.value = profileUseCase.getProfile()
        
        
        viewModelScope.launch {
            
            
            chatUseCase.getMessages().collect {
                _messages.value = it
            }
            
            
        }
    }
    
    
    fun wsConnect() {
        
        viewModelScope.launch {
            
            wsUseCase.connectionWs("cf9b66e2-9e18-4342-bbc0-c5144a593f71")
            
        }
        
    }
    
    
    fun getMessagesBack(chatId: String) {
        viewModelScope.launch {

            
            
            try {
                
                val _ws = wsUseCase.getWsSession() ?: return@launch
                
                
                @Serializable
                data class getMessegesDTO(
                    val action: String,
                    val chatId: String? = null,
                )
//
                val getMesseges = getMessegesDTO(
                    "getMessages",
                    chatId,
                )
                
                
                val jsonMessage = Json.encodeToString(getMessegesDTO.serializer(), getMesseges)
                
                
                
                ws.value = _ws
                _ws.send(Frame.Text(jsonMessage))
                
            } catch (e: Exception) {
                println("Failed to send message: ${e.message}")
            }
        }
    }
    
    fun addMessage(message: MessageItem) {
        viewModelScope.launch {
            chatUseCase.addMessage(message)
        }
    }
    
    
    fun deleteMessage(message: MessageItem) {
        viewModelScope.launch {
            chatUseCase.delMessage(message)
        }
    }
}


