package org.videotrade.shopot.data.remote.repository

import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.navigator.Navigator
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.websocket.close
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.videotrade.shopot.api.handleConnectWebSocket
import org.videotrade.shopot.domain.repository.WsRepository
import org.videotrade.shopot.domain.usecase.ChatUseCase
import org.videotrade.shopot.domain.usecase.ChatsUseCase
import org.videotrade.shopot.domain.usecase.ContactsUseCase

class WsRepositoryImpl : WsRepository, KoinComponent {
    
    private val isConnected = mutableStateOf(false)
    
    private val _wsSession = MutableStateFlow<DefaultClientWebSocketSession?>(null)
    override val wsSession: StateFlow<DefaultClientWebSocketSession?> get() = _wsSession
    
    
    override suspend fun connectionWs(userId: String, navigator: Navigator) {
        val chatUseCase: ChatUseCase by inject()
        val chatsUseCase: ChatsUseCase by inject()
        val contactsUseCase: ContactsUseCase by inject()
        
        println("aaaaaaaa1111111 $userId")
        
        handleConnectWebSocket(
            navigator,
            _wsSession,
            isConnected,
            userId,
            chatUseCase,
            chatsUseCase,
            contactsUseCase,
            
        )
        
        
    }
    
    override suspend fun getWsSession(): DefaultClientWebSocketSession? {
        
        return wsSession.value
    }
    
    
    override fun setWsSession(wsSession: DefaultClientWebSocketSession) {
        
        _wsSession.value = wsSession
    }
    
    override suspend fun clearData() {
        _wsSession.value?.close()
        _wsSession.value = null
        isConnected.value = false
    }
}