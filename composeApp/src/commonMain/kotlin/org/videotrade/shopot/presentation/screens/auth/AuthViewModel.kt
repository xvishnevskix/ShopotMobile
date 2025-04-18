package org.videotrade.shopot.presentation.screens.auth


import cafe.adriel.voyager.navigator.Navigator
import com.dokar.sonner.ToastType
import com.dokar.sonner.ToasterDefaults
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.videotrade.shopot.api.EnvironmentConfig
import org.videotrade.shopot.api.EnvironmentConfig.WEB_SOCKETS_URL
import org.videotrade.shopot.api.navigateToScreen
import org.videotrade.shopot.data.origin
import org.videotrade.shopot.domain.model.CallVerificationResponse
import org.videotrade.shopot.multiplatform.getHttpClientEngine
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
        authCase: String,
        navigator: Navigator,
        phone: String,
        selectCountryName: CountryName,
        serverUnavailable: String,
        toasterViewModel: CommonViewModel,
    ) {
        
        viewModelScope.launch {
            try {
                when (phone) {
                    "+79990000000" -> {
                        navigateToScreen(
                            navigator,
                            CallPasswordScreen(
                                phone,
                                authCase,
                                selectCountryName
                            )
                        )
                    }
                    
                    "+79899236226" -> {
                        navigateToScreen(
                            navigator,
                            CallPasswordScreen(
                                phone,
                                authCase,
                                selectCountryName
                            )
                        )
                        return@launch
                    }
                    
                    "+79388899885" -> {
                        navigateToScreen(
                            navigator,
                            CallPasswordScreen(
                                phone,
                                authCase,
                                selectCountryName
                            )
                        )
                        return@launch
                    }
                }
                
                val jsonContent = Json.encodeToString(
                    buildJsonObject {
                        put("phone", phone)
                    }
                )
                connectWsAuth()
                val client = HttpClient(getHttpClientEngine()) {

                }
                val response: HttpResponse = client.post("${EnvironmentConfig.SERVER_URL}2fa/start") {
                    contentType(ContentType.Application.Json)
                    setBody(jsonContent)
                }
//                val response = origin().post("2fa/start", jsonContent)

                if (response.status.isSuccess()) {
                    val responseBody = response.bodyAsText()

                    val responseData: CallVerificationResponse = Json.decodeFromString(responseBody)

                    confirmNumber.value = responseData.confirmationNumber

                    callId.value = responseData.callId

                    println("jsonElement41414 $responseData")

                    navigateToScreen(
                        navigator,
                        CallPasswordScreen(
                            phone,
                            authCase,
                            selectCountryName
                        )
                    )
                } else {
                    when (response.status.value) {

                        404 -> {
                            toasterViewModel.toaster.show(
                                serverUnavailable,
                                type = ToastType.Error,
                                duration = ToasterDefaults.DurationDefault
                            )
                        }
                        500 -> {
                            toasterViewModel.toaster.show(
                                serverUnavailable,
                                type = ToastType.Error,
                                duration = ToasterDefaults.DurationDefault
                            )
                        }
                        502 -> {
                            toasterViewModel.toaster.show(
                                serverUnavailable,
                                type = ToastType.Error,
                                duration = ToasterDefaults.DurationDefault
                            )
                        }
                        503 -> {
                            toasterViewModel.toaster.show(
                                serverUnavailable,
                                type = ToastType.Error,
                                duration = ToasterDefaults.DurationDefault
                            )
                        }
                        504 -> {
                            toasterViewModel.toaster.show(
                                serverUnavailable,
                                type = ToastType.Error,
                                duration = ToasterDefaults.DurationDefault
                            )
                        }
                        else -> {
                            toasterViewModel.toaster.show(
                                "Неизвестная ошибка. Попробуйте, пожалуйста, позже.",
                                type = ToastType.Error,
                                duration = ToasterDefaults.DurationDefault
                            )
                        }
                    }
                }

            } catch (e: Exception) {
                println("2fa/start $e")


            }
        }
    }
    
}


