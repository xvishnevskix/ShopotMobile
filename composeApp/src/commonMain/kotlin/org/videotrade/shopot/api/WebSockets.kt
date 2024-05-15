package org.videotrade.shopot.api

import androidx.compose.runtime.MutableState
import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.http.HttpMethod
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

suspend fun handleWebRTCWebSocket(
    webSocketSession: MutableState<DefaultClientWebSocketSession?>,
    isConnected: MutableState<Boolean>,
    userId: String

) {
    val httpClient = HttpClient {
        install(WebSockets)
        
    }
    
    if (!isConnected.value) {
        try {
            httpClient.webSocket(
                method = HttpMethod.Get,
                host = "192.168.31.223",
                port = 5050,
                path = "/chat",
                request = {
                    url.parameters.append("user_id", userId)
                }
            ) {
                webSocketSession.value = this
                isConnected.value = true
                
                
                val callOutputRoutine = launch {
                    
                    for (frame in incoming) {
                        if (frame is Frame.Text) {
                            val text = frame.readText()
                            val jsonElement = Json.parseToJsonElement(text)
                            val action = jsonElement.jsonObject["action"]?.jsonPrimitive?.content
                            
                            
                            when (action) {
                                "getMessages" -> {
                                
                                Logger.d { "getMessages $jsonElement" }
                                }
                                
                                "newCall" -> {
                                
                                
                                }
                                
                                "callAnswered" -> {
                                
                                
                                }
                                
                                "ICEcandidate" -> {
                                
                                
                                }
                            }
                        }
                    }
                    
                }
                
                callOutputRoutine.join()
            }
        } catch (e: Exception) {
            isConnected.value = false
            println("Ошибка соединения: $e")
        }
    }
}