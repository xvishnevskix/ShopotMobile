package org.videotrade.shopot.presentation.screens.main

import cafe.adriel.voyager.navigator.Navigator
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.mp.KoinPlatform
import org.videotrade.shopot.api.delValueInStorage
import org.videotrade.shopot.data.origin
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.domain.usecase.CallUseCase
import org.videotrade.shopot.domain.usecase.ChatsUseCase
import org.videotrade.shopot.domain.usecase.ProfileUseCase
import org.videotrade.shopot.domain.usecase.WsUseCase
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.login.SignInScreen

class MainViewModel : ViewModel(), KoinComponent {
    private val profileUseCase: ProfileUseCase by inject()
    private val wsUseCase: WsUseCase by inject()
    private val callUseCase: CallUseCase by inject()
    private val chatsUseCase: ChatsUseCase by inject()
    val commonViewModel: CommonViewModel = KoinPlatform.getKoin().get()
    
    val _wsSession = MutableStateFlow<DefaultClientWebSocketSession?>(null)
    val wsSession: StateFlow<DefaultClientWebSocketSession?> get() = _wsSession.asStateFlow()

//    val callWsSession: Flow<DefaultClientWebSocketSession?> get() = callUseCase.wsSession
    
    
    private val _chats = MutableStateFlow<List<ChatItem>>(emptyList())
    
    val chats: StateFlow<List<ChatItem>> = _chats.asStateFlow()
    
    
    val profile = MutableStateFlow(ProfileDTO())
    
    
    val navigator = MutableStateFlow<Navigator?>(null)
    
    
    init {
        viewModelScope.launch {
            
            
            observeUsers()
            profile.collect {
                
                getWsSession()
                
                
                println("userIDWWWWW : ${commonViewModel.mainNavigator.value}")
                
                commonViewModel.mainNavigator.value?.let { navigator ->
                    callUseCase.connectionWs(
                        it.id,
                        navigator
                    )
                }
                
            }
            
            
        }
    }
    
    fun getChatsInBack(
        defaultClientWebSocketSession: DefaultClientWebSocketSession? = null,
        userId: String? = null
    ) {
        viewModelScope.launch {
            if (defaultClientWebSocketSession !== null) {
                chatsUseCase.getChatsInBack(defaultClientWebSocketSession, userId!!)
                observeUsers()
                
                return@launch
            }
            
            
            if (wsUseCase.wsSession.value !== null && wsUseCase.wsSession.value!!.isActive) {
                chatsUseCase.getChatsInBack(wsUseCase.wsSession.value!!, profile.value.id)
                observeUsers()
            }
        }
        
    }
    
    
    private fun List<Int>.toLocalDateTimeOrNull(): LocalDateTime? {
        return if (this.size >= 6) {
            LocalDateTime(
                this[0],
                this[1],
                this[2],
                this[3],
                this[4],
                this[5],
                this.getOrNull(6) ?: 0
            )
        } else {
            null
        }
    }
    
    private val EARLY_DATE = LocalDateTime(1970, 1, 1, 0, 0, 0)
    
    private fun observeUsers() {
        chatsUseCase.chats.onEach { newUsers ->
            _chats.value = sortChatsByLastMessageCreated(newUsers)
        }.launchIn(viewModelScope)
    }
    
    
    fun getNavigator(navigatorGet: Navigator) {
        println("aasaasasasa")
        navigator.value = navigatorGet
    }
    
    
    private fun getWsSession() {
        viewModelScope.launch {
            
            _wsSession.value = wsUseCase.getWsSession() ?: return@launch
            
        }
    }
    
    
    fun getProfile() {
        viewModelScope.launch {
            profile.value = profileUseCase.getProfile() ?: return@launch
        }
    }
    
    
    fun downloadProfile() {
        viewModelScope.launch {
            val profileCase = profileUseCase.downloadProfile() ?: return@launch
            
            
            profile.value = profileCase
            
            
        }
    }
    
    
    fun setZeroUnread(chat: ChatItem) {
        viewModelScope.launch {
            chatsUseCase.setZeroUnread(chat)
        }
    }
    
    
    fun setCurrentChat(chatValue: String) {
        viewModelScope.launch {
            chatsUseCase.setCurrentChat(chatValue)
        }
    }
    
    private fun parseDateTime(created: List<Int>): Instant? {
        return if (created.size == 7) {
            LocalDateTime(
                created[0], created[1], created[2],
                created[3], created[4], created[5], created[6]
            ).toInstant(TimeZone.UTC)
        } else {
            null
        }
    }

//    private fun sortChatsByLastMessageCreated(chats: List<ChatItem>): List<ChatItem> {
//        return chats.sortedByDescending { chatItem ->
//            chatItem.lastMessage?.created?.let { parseDateTime(it)?.epochSeconds } ?: 0
//        }
//    }
    
    fun sortChatsByLastMessageCreated(chats: List<ChatItem>): List<ChatItem> {
        return chats.sortedByDescending { chatItem ->
            chatItem.sortedDate.let {
                parseDateTime(it)?.epochSeconds
            } ?: 0
        }
    }
    
    fun loadUsers() {
        viewModelScope.launch {
            println("prrrr ${chatsUseCase.chats.value}")
            _chats.update { chatList ->
                sortChatsByLastMessageCreated(chatList)
            }
        }
    }
    
    
    fun leaveApp(navigator: Navigator) {
        viewModelScope.launch {
            val jsonContent = Json.encodeToString(
                buildJsonObject {
                }
            )
            
            
            origin().post("user/removeNotificationToken", jsonContent)
            
            _chats.value = emptyList()
            _wsSession.value = null
            
            profileUseCase.clearData()
            wsUseCase.clearData()
            callUseCase.clearData()
            chatsUseCase.clearData()
            
            delValueInStorage("accessToken")
            delValueInStorage("refreshToken")
            
  
            
            navigator.replace(SignInScreen())
            
        }
    }
}