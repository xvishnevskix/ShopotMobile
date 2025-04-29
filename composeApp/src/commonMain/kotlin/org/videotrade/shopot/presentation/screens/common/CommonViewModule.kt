package org.videotrade.shopot.presentation.screens.common

import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.TabNavigator
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.github.vinceglb.filekit.core.PickerType
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.http.HttpMethod
import io.ktor.util.encodeBase64
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import okio.ByteString.Companion.decodeBase64
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.mp.KoinPlatform
import org.videotrade.shopot.api.EnvironmentConfig.WEB_SOCKETS_URL
import org.videotrade.shopot.api.addValueInStorage
import org.videotrade.shopot.api.decupsMessage
import org.videotrade.shopot.api.getValueInStorage
import org.videotrade.shopot.data.origin
import org.videotrade.shopot.domain.model.WsReconnectionCase
import org.videotrade.shopot.domain.usecase.CommonUseCase
import org.videotrade.shopot.domain.usecase.ProfileUseCase
import org.videotrade.shopot.domain.usecase.WsUseCase
import org.videotrade.shopot.multiplatform.CipherWrapper
import org.videotrade.shopot.multiplatform.FileProviderFactory
import org.videotrade.shopot.multiplatform.getFbToken
import org.videotrade.shopot.presentation.screens.intro.IntroScreen
import org.videotrade.shopot.presentation.screens.intro.IntroViewModel
import org.videotrade.shopot.presentation.screens.test.sendMessageOrReconnect

class CommonViewModel : ViewModel(), KoinComponent {
    private val wsUseCase: WsUseCase by inject()
    private val profileUseCase: ProfileUseCase by inject()
    private val commonUseCase: CommonUseCase by inject()

//    val toaster = ToasterState(viewModelScope)

//    val showButtonNav = MutableStateFlow(true)

    val mainNavigator = MutableStateFlow<Navigator?>(null)
    val tabNavigator = MutableStateFlow<TabNavigator?>(null)

    val isRestartApp = MutableStateFlow(false)

    val appIsActive = MutableStateFlow(true)

    private val _isReconnectionWs = MutableStateFlow(false)


    val cryptoWsSession = MutableStateFlow<DefaultClientWebSocketSession?>(null)

    val isReconnectionWs: StateFlow<Boolean> = _isReconnectionWs

    fun setIsReconnectionWs(activeValue: Boolean) {
        println("Setting isReconnectionWs to $activeValue")
        _isReconnectionWs.value = activeValue
    }

    init {
        isReconnectionWs.onEach {
            println("isReconnectionWs updated: $it")

            if (it) {
                wsUseCase.disconnectWs()
            }
        }.launchIn(viewModelScope) // Подпишитесь на изменения
    }


    fun setAppIsActive(activeValue: Boolean) {
        appIsActive.value = activeValue
    }

    fun restartApp() {
        isRestartApp.value = true

        mainNavigator.value?.push(IntroScreen())
    }

    fun setMainNavigator(value: Navigator) {
        mainNavigator.value = value
        println("dsadaasaaaa")
        commonUseCase.setNavigator(value)
    }
    

    fun setTabNavigator(value: TabNavigator) {
        tabNavigator.value = value
    }

    fun connectionWs() {
        viewModelScope.launch {
            val profileId = getValueInStorage("profileId")

            if (profileId != null) {
                mainNavigator.value?.let { wsUseCase.connectionWs(profileId, it) }
            }
        }
    }

    fun updateNotificationToken() {

        viewModelScope.launch {


            val jsonContent = Json.encodeToString(
                buildJsonObject {
                    put("notificationToken", getFbToken())
                }
            )


            println("jsonContent321323 $jsonContent")

            origin().put("user/profile/edit", jsonContent)
        }
    }


    fun cipherShared(userId: String?, navigator: Navigator?) {
        viewModelScope.launch {
            println("cipherShared")
            try {
                val httpClient = HttpClient {
                    install(WebSockets)
                }

                httpClient.webSocket(
                    method = HttpMethod.Get,
                    host = WEB_SOCKETS_URL,
                    port = 3050,
                    path = "/crypto?userId=$userId",
                ) {
                    println("start cipherShared $userId")


                    cryptoWsSession.value = this

                    val jsonContent = Json.encodeToString(
                        buildJsonObject {
                            put("action", "getKeys")
                        }
                    )

                    send(Frame.Text(jsonContent))

                    for (frame in incoming) {
                        if (frame is Frame.Text) {
                            val text = frame.readText()

                            val jsonElement = Json.parseToJsonElement(text)

                            println("jsonElement $jsonElement")

                            val action =
                                jsonElement.jsonObject["action"]?.jsonPrimitive?.content


                            when (action) {
                                "answerPublicKey" -> {
                                    val cipherWrapper: CipherWrapper = KoinPlatform.getKoin().get()

                                    val publicKeyString =
                                        jsonElement.jsonObject["publicKey"]?.jsonPrimitive?.content

                                    val publicKeyBytes =
                                        publicKeyString?.decodeBase64()?.toByteArray()

                                    println("publicKeyBytes ${publicKeyBytes?.encodeBase64()}")


                                    val result = publicKeyBytes?.let {
                                        cipherWrapper.getSharedSecretCommon(
                                            it
                                        )
                                    }
                                    

                                    if (result !== null) {
                                        val answerPublicKeyJsonContent = Json.encodeToString(
                                            buildJsonObject {
                                                put("action", "sendCipherText")
                                                put("cipherText", result.ciphertext.encodeBase64())
                                                put(
                                                    "deviceId", getValueInStorage("deviceId")
                                                )
                                            }
                                        )

                                        println(
                                            "answerPublicKeyJsonContent111 $answerPublicKeyJsonContent"
                                        )
                                        
                                        


                                        addValueInStorage(
                                            "sharedSecret",
                                            result.sharedSecret.encodeBase64()
                                        )

                                        send(Frame.Text(answerPublicKeyJsonContent))
                                    }


                                }

                                "successSharedSecret" -> {

                                    val introViewModel: IntroViewModel =
                                        KoinPlatform.getKoin().get()

                                    addValueInStorage("profileId", userId!!)

                                    updateNotificationToken()

                                    introViewModel.fetchContacts(navigator)

                                }

                                "encryptedSharedSecret" -> {
                                    println(
                                        "encryptedSharedSecret $jsonElement"
                                    )
                                    val secret =
                                        jsonElement.jsonObject["secret"]?.jsonObject

                                    println(
                                        "encryptedSharedSecret $secret"
                                    )

                                    val cipherWrapper: CipherWrapper = KoinPlatform.getKoin().get()

                                    if (secret != null) {
                                        val sharedSecretDecups =
                                            decupsMessage(secret.toString(), )


                                        if (sharedSecretDecups != null) {
                                            addValueInStorage(
                                                "sharedSecret",
                                                sharedSecretDecups
                                            )

                                            val introViewModel: IntroViewModel =
                                                KoinPlatform.getKoin().get()

                                            addValueInStorage("profileId", userId!!)

                                            updateNotificationToken()

                                            introViewModel.fetchContacts(navigator)
                                        }
                                    }

                                }

                            }
                        }
                    }
                }
            } catch (e: Exception) {
                println("Error connect $e")
            }
        }


    }


    fun sendImage() {
        viewModelScope.launch {
            val filePick = FileProviderFactory.create()
                .pickFile(PickerType.Image)
            if (filePick !== null) {

            }
        }
    }


    suspend fun delSharedSecret() {
        if (cryptoWsSession.value !== null) {
            val jsonContent = Json.encodeToString(
                buildJsonObject {
                    put("action", "deleteSharedSecret")
                    put("userId", getValueInStorage("profileId"))
                    put("deviceId", getValueInStorage("deviceId"))
                }
            )
            println("jsonContent $jsonContent")
            cryptoWsSession.value?.send(Frame.Text(jsonContent))
        }
    }


    fun sendSocket() {
        viewModelScope.launch {
            try {
                val jsonContent = Json.encodeToString(
                    buildJsonObject {
                        put("action", "forwardMessage")
                        put("chatId", "a5800a2f-6637-4095-bbe2-327298b04bd6")
                        put("messageId", "97c35b59-a131-41e3-866a-dc34721b7b22")
                        put("userId", profileUseCase.getProfile().id)


                    }
                )
                println("jsonContent $jsonContent")
                
                sendMessageOrReconnect(
                    wsUseCase.wsSession.value,
                    jsonContent,
                    WsReconnectionCase.ChatWs
                )
            } catch (e: Exception) {
                println("Failed to send message: ${e.message}")
            }
        }
    }

    fun sendNotify(
        title: String,
        content: String? = "Уведомление",
        fromUser: String?,
        chatId: String,
        personal: Boolean
    ) {
        viewModelScope.launch {
            println("Уведомление ${fromUser}")

            if (fromUser !== null) {
                val jsonContent = Json.encodeToString(
                    buildJsonObject {
                        put("title", title)
                        put("body", content)
                        put("fromUser", fromUser)
                        put("chatId", chatId)
                        put("personal", personal)
                    }
                )

                println("Уведомление ${jsonContent}")

                origin().post("notification/notify", jsonContent)
            }
        }
    }
}
