package org.videotrade.shopot.presentation.screens.intro


import cafe.adriel.voyager.navigator.Navigator
import com.mmk.kmpnotifier.notification.NotifierManager
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
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
import org.videotrade.shopot.domain.usecase.ChatsUseCase
import org.videotrade.shopot.domain.usecase.ContactsUseCase
import org.videotrade.shopot.domain.usecase.ProfileUseCase
import org.videotrade.shopot.domain.usecase.WsUseCase
import org.videotrade.shopot.multiplatform.getBuildVersion
import org.videotrade.shopot.presentation.screens.common.UpdateScreen
import org.videotrade.shopot.presentation.screens.login.SignInScreen
import org.videotrade.shopot.presentation.screens.main.MainScreen
import org.videotrade.shopot.presentation.screens.test.TestScreen

class IntroViewModel : ViewModel(), KoinComponent {

    private val contactsUseCase: ContactsUseCase by inject()
    private val profileUseCase: ProfileUseCase by inject()
    private val wsUseCase: WsUseCase by inject()
    private val chatsUseCase: ChatsUseCase by inject()

//
//    val _wsSession = MutableStateFlow<DefaultClientWebSocketSession?>(null)
//    val wsSession: StateFlow<DefaultClientWebSocketSession?> get() = _wsSession.asStateFlow()


    val profile = MutableStateFlow<ProfileDTO?>(null)

    private var isObserving = MutableStateFlow(true)

    val navigator = MutableStateFlow<Navigator?>(null)


    init {
        viewModelScope.launch {
//            profile.onEach { _ ->
//
//                println("navigator.value1111111")
//
//                profile.value?.id?.let {
//                    println("navigator.value222222 $it")
//
//                    if (navigator.value !== null) {
//
//                        println("navigator.value333333 $it")
//
//
//                    }
//
//
//                }
//
//
//            }.launchIn(viewModelScope)

            observeWsConnection()


        }
    }

    private fun chatsInit(navigator: Navigator) {

        viewModelScope.launch {

            downloadProfile(navigator)

        }
    }

    fun updateNotificationToken() {

        try {
            viewModelScope.launch {


                val jsonContent = Json.encodeToString(
                    buildJsonObject {
                        put("notificationToken", NotifierManager.getPushNotifier().getToken())
                    }
                )


                println("jsonContent321323 $jsonContent")

                origin().put("user/profile/edit", jsonContent)
            }
        } catch (e: Exception) {

        }
    }


    fun fetchContacts(navigator: Navigator) {
        viewModelScope.launch {


            val contacts = contactsUseCase.fetchContacts()
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

            println("profileCase $profileCase")

            if (profileCase == null) {
                delValueInStorage("accessToken")
                delValueInStorage("refreshToken")

                return@launch

            } else {
                addValueInStorage("profileId", profileCase.id)

                println("profileCase $profileCase")

                profile.value = profileCase

                connectionWs(profileCase.id, navigator)
            }

        }
    }

    private fun observeWsConnection() {
        println("wsSessionIntrowsUseCase.wsSession ${wsUseCase.wsSession.value}")
        wsUseCase.wsSession
            .onEach { wsSessionNew ->
                println("wsSessionNew ${wsUseCase.wsSession.value} ${profile.value?.id} ${isObserving.value}")


                if (profile.value?.id !== null && isObserving.value) {

                    if (wsSessionNew != null) {
                        println("wsSessionIntro $wsSessionNew")

                        stopObserving()

//                        val jsonContent = Json.encodeToString(
//                            buildJsonObject {
//                                put("action", "getUserChats")
//                                put("userId", profile.value?.id)
//                            }
//                        )
//
//                        try {
//
//                            println("jsonContent $jsonContent")
//
//                            wsSessionNew.send(Frame.Text(jsonContent))
//
//                            navigator.value?.replace(MainScreen())
//
//                            println("Message sent successfully")
//                        } catch (e: Exception) {
//                            println("Failed to send message: ${e.message}")
//                        }
//
                        chatsUseCase.getChatsInBack(wsSessionNew, profile.value!!.id)

//                        navigator.value?.replace(MainScreen())
                        navigator.value?.replace(TestScreen())


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


    suspend fun checkVersion(): Boolean {
//            val getVersion = origin().get<String>("auth/createVersion")
        val getVersion = 12

        val op = getBuildVersion()

        return getVersion > op
    }
}


