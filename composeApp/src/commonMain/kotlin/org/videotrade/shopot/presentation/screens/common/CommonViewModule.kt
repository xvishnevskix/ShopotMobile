package org.videotrade.shopot.presentation.screens.common

import cafe.adriel.voyager.navigator.Navigator
import com.dokar.sonner.ToasterState
import com.mmk.kmpnotifier.notification.NotifierManager
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.http.HttpMethod
import io.ktor.util.encodeBase64
import io.ktor.utils.io.core.toByteArray
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.MutableStateFlow
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
import org.videotrade.shopot.api.EnvironmentConfig.webSocketsUrl
import org.videotrade.shopot.api.addValueInStorage
import org.videotrade.shopot.api.getValueInStorage
import org.videotrade.shopot.data.origin
import org.videotrade.shopot.domain.usecase.CommonUseCase
import org.videotrade.shopot.domain.usecase.WsUseCase
import org.videotrade.shopot.multiplatform.CipherWrapper
import org.videotrade.shopot.multiplatform.EncapsulationFileResult
import org.videotrade.shopot.presentation.screens.intro.IntroViewModel
import org.videotrade.shopot.presentation.screens.test.TestScreen

class CommonViewModel : ViewModel(), KoinComponent {
    private val wsUseCase: WsUseCase by inject()
    private val commonUseCase: CommonUseCase by inject()
    
    val toaster = ToasterState(viewModelScope)

//    val showButtonNav = MutableStateFlow(true)
    
    val mainNavigator = MutableStateFlow<Navigator?>(null)
    
    
    fun setMainNavigator(value: Navigator) {
        mainNavigator.value = value
        
        commonUseCase.setNavigator(value)
    }
    
    fun connectionWs(navigator: Navigator) {
        viewModelScope.launch {
            wsUseCase.connectionWs("11111", navigator)
        }
    }
    
    private fun updateNotificationToken() {
        
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
    
    
    fun cipherShared(userId: String?, navigator: Navigator) {
        viewModelScope.launch {
            val httpClient = HttpClient {
                install(WebSockets)
            }
            try {
                httpClient.webSocket(
                    method = HttpMethod.Get,
                    host = webSocketsUrl,
                    port = 3050,
                    path = "/crypto?userId=$userId",
                    
                    ) {
                    
                    println("jsonElement$userId")
                    
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
                                    
                                    
                                    val sharedSecret = getValueInStorage("sharedSecret")
                                    
                                    
                                    updateNotificationToken()
                                    
                                    
                                    navigator.push(TestScreen())
//                                    introViewModel.fetchContacts(navigator)
                                    
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                
                
            }
        }
    }
}
