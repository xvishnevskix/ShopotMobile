package org.videotrade.shopot.presentation.screens.test

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.screen.Screen
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.http.HttpMethod
import io.ktor.websocket.DefaultWebSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.koin.mp.KoinPlatform
import org.videotrade.shopot.api.EnvironmentConfig
import org.videotrade.shopot.api.getValueInStorage
import org.videotrade.shopot.api.reconnectWebSocket
import org.videotrade.shopot.data.remote.repository.reconnectCallWebSocket
import org.videotrade.shopot.domain.model.WsReconnectionCase
import org.videotrade.shopot.domain.usecase.WsUseCase
import org.videotrade.shopot.presentation.components.Common.SafeArea


class TestScreen : Screen {
    @Composable
    override fun Content() {
        val scope = rememberCoroutineScope()
        val wsSession = MutableStateFlow<DefaultClientWebSocketSession?>(null)
        
        
        
        MaterialTheme {
            SafeArea {
                Column {
                    Button(
                        onClick = {
                            
                            val userId = getValueInStorage("profileId")
                            
                            scope.launch {
                                connectionWSS(userId, wsSession, {
                                    println("OOOOOP $it")
                                })
                            }
                            
                        }
                    ) {
                        Text(
                            "Start Connect",
                            color = Color.White
                        )
                    }
                    
                    
                    Button(
                        onClick = {
                            scope.launch {
                                val jsonContent = Json.encodeToString(
                                    buildJsonObject {
                                        put("action", "disconnect")
                                    }
                                )
                                println("jsonContent $jsonContent")
                                
                                wsSession.value?.send(Frame.Text(jsonContent))
                            }
                        }
                    ) {
                        Text(
                            "disconnect",
                            color = Color.White
                        )
                    }
                    
                    Button(
                        onClick = {
                            scope.launch {
                                val userId = getValueInStorage("profileId")
                                
                                val jsonContent = Json.encodeToString(
                                    buildJsonObject {
                                        put("action", "connectionTest")
                                    }
                                )
                                
                                println("jsonContent $jsonContent")
                                
                                println("connectionTest ${wsSession.value?.isActive}")
                                
                                sendMessageOrReconnect(
                                    wsSession = wsSession.value,
                                    jsonContent = jsonContent,
                                    wsReconnectionCase = WsReconnectionCase.ChatWs
                                )
                                
                                
                            }
                        }
                    ) {
                        Text(
                            "connectionTest",
                            color = Color.White
                        )
                    }
                    
                }
            }
        }
    }
}


suspend fun connectionWSS(
    userId: String?,
    wsSession: MutableStateFlow<DefaultClientWebSocketSession?>,
    onConnectionResult: (Boolean) -> Unit
) {
    val httpClient = HttpClient {
        install(WebSockets)
    }
    
    try {
        httpClient.webSocket(
            method = HttpMethod.Get,
            host = EnvironmentConfig.WEB_SOCKETS_URL,
            port = 5050,
            path = "/chat?userId=$userId"
        ) {
            println("WebSocket connected successfully")
            
            wsSession.value = this
            
            // ✅ Успешное соединение
            onConnectionResult(true)
            
            val callOutputRoutine = launch {
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        val text = frame.readText()
                        println("NewFrame $text")
                    }
                }
            }
            
            callOutputRoutine.join()
        }
    } catch (e: Exception) {
        println("Ошибка соединения: $e")
        
        // ❌ Ошибка соединения
        onConnectionResult(false)
        
        // Повторное подключение
        if (userId != null) {
            reconnectWebSocket(userId)
        }
    }
}


suspend fun sendMessageOrReconnect(
    wsSession: DefaultClientWebSocketSession?,
    jsonContent: String,
    wsReconnectionCase: WsReconnectionCase
) {
    val wsUseCase: WsUseCase = KoinPlatform.getKoin().get()
    
    
    val userId = getValueInStorage("profileId")
    
    if (wsSession?.isActive == false) {
        if (userId != null) {
            if(wsUseCase.processingReconnect.value) return
            
            wsUseCase.processingReconnect.value = true
            
            val reconnect: suspend (
                String,
                suspend (Boolean, DefaultWebSocketSession) -> Unit
            ) -> Unit = when (wsReconnectionCase) {
                WsReconnectionCase.ChatWs -> ::reconnectWebSocket
                WsReconnectionCase.CallWs -> ::reconnectCallWebSocket
            }
            
            reconnect(userId) { isConnected, newWsSession ->
                if (isConnected) {
                    CoroutineScope(Dispatchers.IO).launch {
                        wsUseCase.processingReconnect.value = false
                        
                        newWsSession.send(Frame.Text(jsonContent))
                    }
                }
            }
        }
    } else {
        wsSession?.send(Frame.Text(jsonContent))
    }
}

