package org.videotrade.shopot.presentation.screens.common

import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.dokar.sonner.ToasterState
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.github.vinceglb.filekit.core.PickerType
import io.ktor.client.HttpClient
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
import org.videotrade.shopot.api.getValueInStorage
import org.videotrade.shopot.data.origin
import org.videotrade.shopot.domain.usecase.CommonUseCase
import org.videotrade.shopot.domain.usecase.ProfileUseCase
import org.videotrade.shopot.domain.usecase.WsUseCase
import org.videotrade.shopot.multiplatform.CipherWrapper
import org.videotrade.shopot.multiplatform.EncapsulationFileResult
import org.videotrade.shopot.multiplatform.FileProviderFactory
import org.videotrade.shopot.multiplatform.getFbToken
import org.videotrade.shopot.multiplatform.getPlatform
import org.videotrade.shopot.presentation.screens.intro.IntroScreen
import org.videotrade.shopot.presentation.screens.intro.IntroViewModel

class CommonViewModel : ViewModel(), KoinComponent {
    private val wsUseCase: WsUseCase by inject()
    private val profileUseCase: ProfileUseCase by inject()
    private val commonUseCase: CommonUseCase by inject()
    
    val toaster = ToasterState(viewModelScope)

//    val showButtonNav = MutableStateFlow(true)
    
    val mainNavigator = MutableStateFlow<Navigator?>(null)
    val tabNavigator = MutableStateFlow<TabNavigator?>(null)
    
    val isRestartApp = MutableStateFlow(false)
    
    val appIsActive = MutableStateFlow(false)
    
    private val _isReconnectionWs = MutableStateFlow(false)
    
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
    
    
    fun cipherShared(userId: String?, navigator: Navigator) {
        viewModelScope.launch {
            val httpClient = HttpClient {
                install(WebSockets)
            }
            try {
                httpClient.webSocket(
                    method = HttpMethod.Get,
                    host = WEB_SOCKETS_URL,
                    port = 3050,
                    path = "/crypto?userId=$userId",
                    
                    ) {
                    
                    println("start cipherShared $userId")
                    
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
                                            }
                                        )
                                        
                                        println(
                                            "successSharedSecret111 ${
                                                EncapsulationFileResult(
                                                    result.sharedSecret,
                                                    result.sharedSecret
                                                )
                                            }"
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
                wsUseCase.wsSession.value?.send(Frame.Text(jsonContent))
                
            } catch (e: Exception) {
                println("Failed to send message: ${e.message}")
            }
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
                        put("platform", getPlatform().name)
                    }
                )
                
                println("Уведомление ${jsonContent}")
                
                origin().post("notification/notify", jsonContent)
            }
        }
    }
}
