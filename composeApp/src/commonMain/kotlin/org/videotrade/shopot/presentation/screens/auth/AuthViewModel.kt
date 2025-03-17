package org.videotrade.shopot.presentation.screens.auth


import cafe.adriel.voyager.navigator.Navigator
import com.dokar.sonner.ToastType
import com.dokar.sonner.ToasterDefaults
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
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
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.videotrade.shopot.api.EnvironmentConfig.WEB_SOCKETS_URL
import org.videotrade.shopot.data.origin
import org.videotrade.shopot.domain.model.CallVerificationResponse
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.login.CountryName

class AuthViewModel : ViewModel(), KoinComponent {
    private val commonViewModel: CommonViewModel by inject()
    
    private val authWsSession = MutableStateFlow<DefaultClientWebSocketSession?>(null)
    
    val isCallPasswordSuccess = MutableStateFlow(false)
    var confirmNumber = MutableStateFlow("")
    var callId = MutableStateFlow("")
    
    fun connectWsAuth() {
        viewModelScope.launch {
            try {
                val httpClient = HttpClient {
                    install(WebSockets)
                }
                
                httpClient.webSocket(
                    method = HttpMethod.Get,
                    host = WEB_SOCKETS_URL,
                    port = 3005,
                    path = "/callPassword",
                ) {
                    authWsSession.value = this
                    
                    println("callPassword conncet")
                    
                    
                    for (frame in incoming) {
                        if (frame is Frame.Text) {
                            val text = frame.readText()
                            
                            val jsonElement = Json.parseToJsonElement(text)
                            
                            println("jsonElement $jsonElement")
                            
                            val event =
                                jsonElement.jsonObject["event"]?.jsonPrimitive?.content
                            
                            
                            when (event) {
                                "verificationCompleted" -> {
                                    println("verificationCompleted")
                                    
                                    commonViewModel.toaster.show(
                                        "Проверка прошла успешно подождите 5 сек",
                                        type = ToastType.Success,
                                        duration = ToasterDefaults.DurationDefault
                                    )
                                    
                                    
                                    
                                    isCallPasswordSuccess.value = true
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                println("Error connect callPassword $e")
            }
        }
        
        
    }
    
    
    fun handleAuth(
        authCase: String, navigator: Navigator, phone: String, selectCountryName: CountryName
    ) {
        
        viewModelScope.launch {
            val jsonContent = Json.encodeToString(
                buildJsonObject {
                    put("phone", phone)
                }
            )
            connectWsAuth()
            
            val response = origin().post("2fa/start", jsonContent) ?: return@launch
            
            val responseData: CallVerificationResponse = Json.decodeFromString(response)
            
            confirmNumber.value = responseData.confirmationNumber
            
            callId.value = responseData.callId
            
            println("jsonElement41414 $responseData")
            
            navigator.push(
                CallPasswordScreen(
                    phone,
                    authCase,
                    selectCountryName
                )
            )
        }
    }
    
}


