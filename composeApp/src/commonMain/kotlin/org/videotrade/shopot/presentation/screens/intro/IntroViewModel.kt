package org.videotrade.shopot.presentation.screens.intro


import cafe.adriel.voyager.navigator.Navigator
import com.mmk.kmpnotifier.notification.NotifierManager
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
import org.videotrade.shopot.api.addValueInStorage
import org.videotrade.shopot.api.delValueInStorage
import org.videotrade.shopot.data.origin
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.domain.usecase.ContactsUseCase
import org.videotrade.shopot.domain.usecase.ProfileUseCase
import org.videotrade.shopot.domain.usecase.WsUseCase
import org.videotrade.shopot.multiplatform.BackgroundTaskManagerFactory
import org.videotrade.shopot.presentation.screens.login.SignInScreen
import org.videotrade.shopot.presentation.screens.main.MainScreen

class IntroViewModel : ViewModel(), KoinComponent {

    private val ContactsUseCase: ContactsUseCase by inject()


    private val profileUseCase: ProfileUseCase by inject()
    private val wsUseCase: WsUseCase by inject()

//
//    val _wsSession = MutableStateFlow<DefaultClientWebSocketSession?>(null)
//    val wsSession: StateFlow<DefaultClientWebSocketSession?> get() = _wsSession.asStateFlow()


    val profile = MutableStateFlow<ProfileDTO?>(null)

    private var isObserving = MutableStateFlow(true)

    val navigator = MutableStateFlow<Navigator?>(null)


    init {
        viewModelScope.launch {
            profile.collect { _ ->


                profile.value?.id?.let {

                    if (navigator.value !== null) {

                        println("navigator.value31311 $it")

                        observeWsConnection(it, navigator.value!!)
                        connectionWs(it, navigator.value!!)
                    }


                }


            }
        }
    }

    private fun chatsInit(navigator: Navigator) {

        viewModelScope.launch {

            downloadProfile(navigator)

        }
    }

    fun updateNotificationToken() {

        viewModelScope.launch {


            val jsonContent = Json.encodeToString(
                buildJsonObject {
                    put("notificationToken", NotifierManager.getPushNotifier().getToken())
                }
            )

            
            println("jsonContent321323 $jsonContent")

            origin().put("user/profile/edit", jsonContent)
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

    private fun connectionWs(userId: String, navigator: Navigator) {
        viewModelScope.launch {
            wsUseCase.connectionWs(userId, navigator)
        }
    }


    private fun downloadProfile(navigator: Navigator) {
        viewModelScope.launch {
            val profileCase = profileUseCase.downloadProfile()



            if (profileCase == null) {
                delValueInStorage("accessToken")
                delValueInStorage("refreshToken")

                return@launch

            }
            
            addValueInStorage("profileId", profileCase.id)
            
            
            
            BackgroundTaskManagerFactory.create().scheduleTask()
            
            
            
            profile.value = profileCase


        }
    }

    private fun observeWsConnection(userId: String, navigator: Navigator) {
        println("wsSessionIntrowsUseCase.wsSession ${wsUseCase.wsSession.value}")
        wsUseCase.wsSession
            .onEach { wsSessionNew ->
                if (profile.value?.id !== null && isObserving.value) {

                    if (wsSessionNew != null) {
                        println("wsSessionIntro $wsSessionNew")

                        stopObserving()

                        val jsonContent = Json.encodeToString(
                            buildJsonObject {
                                put("action", "getUserChats")
                                put("userId", profile.value?.id)
                            }
                        )

                        try {

                            println("jsonContent $jsonContent")

                            wsSessionNew.send(Frame.Text(jsonContent))

                            navigator.replace(MainScreen())

                            println("Message sent successfully")
                        } catch (e: Exception) {
                            println("Failed to send message: ${e.message}")
                        }


                    }
                }

            }
            .launchIn(viewModelScope)


    }


    fun stopObserving() {
        isObserving.value = false
    }

    fun startObserving() {
        isObserving.value = true
    }
}


