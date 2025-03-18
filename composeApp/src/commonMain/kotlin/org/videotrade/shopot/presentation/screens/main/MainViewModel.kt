package org.videotrade.shopot.presentation.screens.main

import cafe.adriel.voyager.navigator.Navigator
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import kotlinx.coroutines.delay
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
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.mp.KoinPlatform
import org.videotrade.shopot.api.delValueInStorage
import org.videotrade.shopot.data.origin
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.domain.model.SearchDto
import org.videotrade.shopot.domain.usecase.CallUseCase
import org.videotrade.shopot.domain.usecase.ChatsUseCase
import org.videotrade.shopot.domain.usecase.ProfileUseCase
import org.videotrade.shopot.domain.usecase.WsUseCase
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.intro.WelcomeScreen
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

    private val _globalSearchResults = MutableStateFlow<List<SearchDto>>(emptyList())
    val globalSearchResults: StateFlow<List<SearchDto>> = _globalSearchResults
    
    
    val isLoadingChats = chatsUseCase.isLoadingChats
    
    
    val navigator = MutableStateFlow<Navigator?>(null)
    
    
    init {
        viewModelScope.launch {
            
            
            observeUsers()
            profile.collect {
                
                getWsSession()
                
                
                println("userIDWWWWW : ${commonViewModel.mainNavigator.value}")
                

//                    callUseCase.connectionWs(
//                        it.id
//                    )
                
            }
            
            
        }
    }
    
    fun getChatsInBack(
        defaultClientWebSocketSession: DefaultClientWebSocketSession? = null,
        userId: String? = null
    ) {

        viewModelScope.launch {
            chatsUseCase.setIsLoadingValue(true)

            try {
                if (defaultClientWebSocketSession != null) {
                    chatsUseCase.getChatsInBack(defaultClientWebSocketSession, userId!!)
                    observeUsers()
                    return@launch
                }

                if (wsUseCase.wsSession.value != null && wsUseCase.wsSession.value!!.isActive) {
                    chatsUseCase.getChatsInBack(wsUseCase.wsSession.value!!, profile.value.id)
                    observeUsers()
                }
            } catch (_: Exception) {
            
            }
            finally {
                chatsUseCase.setIsLoadingValue(false)
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
//        _isLoadingChats.value = true
        try {
            chatsUseCase.chats.onEach { newUsers ->
                _chats.value = sortChatsByLastMessageCreated(newUsers)
            }.launchIn(viewModelScope)
        } finally {
//            _isLoadingChats.value = false
        }
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
                // Преобразование даты в epochSeconds
                parseDateTime(it)?.epochSeconds
            } ?: 0
        }
    }
    
    // Функция для проверки изменений
    fun hasChatsChanged(oldChats: List<ChatItem>, newChats: List<ChatItem>): Boolean {
        println("oldChats.size != newChats.size ${oldChats.size} ${ newChats.size}")
        if (oldChats.size != newChats.size) return true // Проверяем размер списков
        
        return oldChats.zip(newChats).any { (old, new) ->
            old.id != new.id || // Проверяем идентификатор
                    old.unread != new.unread || // Изменение количества непрочитанных сообщений
                    old.sortedDate != new.sortedDate || // Изменение даты сортировки
                    old.lastMessage != new.lastMessage // Проверяем последнее сообщение
        }
    }
    
//    fun loadUsers() {
//        viewModelScope.launch {
//            _isLoadingChats.value = true
//
//            val newChats = sortChatsByLastMessageCreated(chatsUseCase.chats.value ?: emptyList())
//            val oldChats = _chats.value ?: emptyList() // Текущий список чатов
//
//            // Проверяем, изменились ли чаты
//            if (!hasChatsChanged(oldChats, newChats)) {
//                _isLoadingChats.value = false
//                // Если изменений нет, выходим из функции
//                return@launch
//            }
//
//            // Устанавливаем состояние загрузки только при обновлении
//            _isLoadingChats.value = true
//            try {
//                _chats.update { newChats }
//            } finally {
////                _isLoadingChats.value = false
//            }
//        }
//    }
    
    fun loadUsers() {
        viewModelScope.launch {
//           chatsUseCase.setIsLoadingValue(true)
            try {
                println("prrrr ${chatsUseCase.chats.value}")
                _chats.update { chatList ->
                    sortChatsByLastMessageCreated(chatList)
                }
            } finally {
//                chatsUseCase.setIsLoadingValue(false)
            }
        }
    }


    
    
    fun leaveApp(navigator: Navigator, isArchive: Boolean) {
        viewModelScope.launch {
            val jsonContent = Json.encodeToString(
                buildJsonObject {
                }
            )

            if (isArchive) {
                origin().post("user/removeNotificationToken", jsonContent)
              val response = origin().post("archive", jsonContent)
                println("sdgehefsdf ${response}")
            } else {
                origin().post("user/removeNotificationToken", jsonContent)
            }
            
            _chats.value = emptyList()
            _wsSession.value = null
            
            profileUseCase.clearData()
            wsUseCase.clearData()
            callUseCase.clearData()
            chatsUseCase.clearData()
            
            delValueInStorage("accessToken")
            delValueInStorage("refreshToken")


            commonViewModel.delSharedSecret()
            navigator.replace(WelcomeScreen())
            
        }
    }




    fun searchUsers(query: String) {
        viewModelScope.launch {
            try {
                val jsonContent = Json.encodeToString(
                    buildJsonObject {
                    }
                )

                val response: String? = origin().post("user/search?login=${query}", jsonContent)


                val searchResults: List<SearchDto> = response?.let {
                    Json.decodeFromString(it)
                } ?: emptyList()

                _globalSearchResults.value = searchResults
            } catch (e: Exception) {
                e.printStackTrace()
                _globalSearchResults.value = emptyList()
            }
        }
    }

    fun clearGlobalResults() {
        _globalSearchResults.value = emptyList()
    }

}