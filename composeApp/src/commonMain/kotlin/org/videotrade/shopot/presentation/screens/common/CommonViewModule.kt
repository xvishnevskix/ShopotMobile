package org.videotrade.shopot.presentation.screens.common

import cafe.adriel.voyager.navigator.Navigator
import com.dokar.sonner.ToasterState
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.http.HttpMethod
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
import org.videotrade.shopot.api.addValueInStorage
import org.videotrade.shopot.domain.usecase.CommonUseCase
import org.videotrade.shopot.domain.usecase.WsUseCase
import org.videotrade.shopot.multiplatform.CipherWrapper

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
    
    fun cipherShared() {
        viewModelScope.launch {
            val httpClient = HttpClient {
                install(WebSockets)
            }
            try {
                httpClient.webSocket(
                    method = HttpMethod.Get,
                    host = "localhost",
                    port = 3001,
                    
                    ) {
                    
                    
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
                            val action =
                                jsonElement.jsonObject["action"]?.jsonPrimitive?.content
                            
                            
                            when (action) {
                                "answerPublicKey" -> {
                                    val cipherWrapper: CipherWrapper = KoinPlatform.getKoin().get()
                                    
                                    val publicKeyString =
                                        jsonElement.jsonObject["publicKey"]?.jsonPrimitive?.content
                                    
                                    val publicKeyBytes =
                                        publicKeyString?.decodeBase64()?.toByteArray()
                                    
                                    val result = publicKeyBytes?.let {
                                        cipherWrapper.getSharedSecretCommon(
                                            it
                                        )
                                    }
                                    
                                    
                                    if (result !== null) {
//                                        val answerPublicKeyJsonContent = Json.encodeToString(
//                                            buildJsonObject {
//                                                put("action", "sendCipherText")
//                                                put("ciphertext", result.ciphertext)
//                                            }
//                                        )
//
//                                        addValueInStorage(
//                                            "sharedSecret",
//                                            result.sharedSecret.toString()
//                                        )
//
//                                        send(Frame.Text(answerPublicKeyJsonContent))
                                    }
                                    
                                    
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
