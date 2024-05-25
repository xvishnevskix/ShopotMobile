package org.videotrade.shopot.presentation.screens.intro


import cafe.adriel.voyager.navigator.Navigator
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.websocket.Frame
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.domain.usecase.ChatsUseCase
import org.videotrade.shopot.domain.usecase.ContactsUseCase
import org.videotrade.shopot.domain.usecase.ProfileUseCase
import org.videotrade.shopot.domain.usecase.WsUseCase
import org.videotrade.shopot.presentation.screens.login.SignInScreen
import org.videotrade.shopot.presentation.screens.main.MainScreen

class IntroViewModel : ViewModel(), KoinComponent {
    
    private val ContactsUseCase: ContactsUseCase by inject()
    
    
    private val profileUseCase: ProfileUseCase by inject()
    private val WsUseCase: WsUseCase by inject()
    
    
    val _wsSession = MutableStateFlow<DefaultClientWebSocketSession?>(null)
    val wsSession: StateFlow<DefaultClientWebSocketSession?> get() = _wsSession.asStateFlow()
    
    
    val profile = MutableStateFlow<ProfileDTO?>(null)
    
    
//    init {
//        viewModelScope.launch {
//            println("dadsadada1")
//
//            downloadProfile()
//
//            profile.collect { updatedProfile ->
//
//                profile.value?.id?.let {
//                    println("dadsadada2")
//                    observeWsConnection(it)
//
//                    connectionWs(it)
//
//
//                }
//
//
//            }
//        }
//    }
    
    private fun chatsInit(navigator: Navigator) {
        
        viewModelScope.launch {
            println("dadsadada1")
            
            downloadProfile()
            
            profile.collect { updatedProfile ->
                
                profile.value?.id?.let {
                    println("dadsadada2")
                    observeWsConnection(it, navigator)
                    
                    connectionWs(it)
                    
                    
                }
                
                
            }
        }
    }
    
    
    fun fetchContacts(navigator: Navigator) {
        viewModelScope.launch {
            
            
            val contacts = ContactsUseCase.fetchContacts()
            println("response3131 $contacts")
            
            
            if (contacts != null) {
                
                chatsInit(navigator)
                
            } else {
                navigator.push(SignInScreen())
                
                
            }
            
        }
    }
    
    fun connectionWs(userId: String) {
        viewModelScope.launch {
            WsUseCase.connectionWs(userId)
        }
    }
    
    
    fun downloadProfile() {
        viewModelScope.launch {
            val profileCase = profileUseCase.downloadProfile() ?: return@launch
            
            
            profile.value = profileCase
            
            
        }
    }
    
    private fun observeWsConnection(userId: String, navigator: Navigator) {
        WsUseCase.wsSession
            .onEach { wsSessionNew ->
                
                if (wsSessionNew != null) {
                    println("wsSessionNew $wsSessionNew")
                    _wsSession.value = wsSessionNew
                    
                    
                    val jsonContent = Json.encodeToString(
                        buildJsonObject {
                            put("action", "getUserChats")
                            put("userId", userId)
                        }
                    )
                    
                    try {
                        
                        println("jsonContent $jsonContent")
                        
                        wsSessionNew.send(Frame.Text(jsonContent))
                        
                        
                        navigator.push(MainScreen())
                        
                        println("Message sent successfully")
                    } catch (e: Exception) {
                        println("Failed to send message: ${e.message}")
                    }
                    
                    
                }
                
            }
            .launchIn(viewModelScope)
    }
    
}


