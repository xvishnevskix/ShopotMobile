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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.videotrade.shopot.api.EnvironmentConfig.webSocketsUrl
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.usecase.ChatUseCase
import org.videotrade.shopot.domain.usecase.ChatsUseCase

suspend fun handleWebRTCWebSocket(
    webSocketSession: MutableStateFlow<DefaultClientWebSocketSession?>,
    isConnected: MutableState<Boolean>,
    userId: String,
    chatUseCase: ChatUseCase,
    ChatsUseCase: ChatsUseCase

) {
    
    
    val httpClient = HttpClient {
        install(WebSockets)
        
    }
    
    if (!isConnected.value) {
        
        
        try {
            httpClient.webSocket(
                method = HttpMethod.Get,
                host = webSocketsUrl,
                port = 5050,
                path = "/chat?userId=$userId",
                
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
                                "getUserChats" -> {
                                    try {
                                        
                                        val messagesJson =
                                            jsonElement.jsonObject["data"]?.jsonArray
                                        
                                        
                                        val chats = mutableListOf<ChatItem>()
                                        if (messagesJson != null) {
                                            
                                            for (chatItem in messagesJson) {
                                                
                                                
                                                
                                                val chatJson =
                                                    chatItem.jsonObject["chat"]?.jsonObject
                                                
                                                println("messages31312222 $chatJson")
                                                
                                                
                                                val chat: ChatItem =
                                                    Json.decodeFromString(chatJson.toString())
                                                
                                                println("messages31312222 $chat")
                                                
                                                
                                                chats.add(chat)
                                                
                                            }
                                            
                                            println("chats $chats")

                                       ChatsUseCase.addChats(chats)// Инициализация сообщений
                                        }
                                        
                                    } catch (e: Exception) {
                                        
                                        Logger.d("Error228: $e")
                                    }
                                    
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

//                @Serializable
//                data class sendMessage(
//                    var action: String,
//                    var content: String,
//                    var fromUser: String,
//                    var chatId: String,
//                )
//
//                val jsonMessageMess =
//                    Json.encodeToString(
//                        sendMessage.serializer(), sendMessage(
//                            "sendMessage",
//                            "Privet",
//                            "10f609c6-df91-4cbc-afc7-30c175cc1111",
//                            "306e5bbb-e2db-4480-9f85-ca0a4b1b7a0b"
//                        )
//                    )
//
//
//                send(Frame.Text(jsonMessageMess))
//

//                @Serializable
//                data class getMessages(
//                    var action: String,
//                    var chatId: String,
//                )
//
//
//                val jsonMessage =
//                    Json.encodeToString(
//                        getMessages.serializer(), getMessages(
//                            "getMessages", chatId = "306e5bbb-e2db-4480-9f85-ca0a4b1b7a0b"
//                        )
//                    )
//
//                try {
//
//
//                    send(Frame.Text(jsonMessage))
//                    println("Message sent successfully")
//                } catch (e: Exception) {
//                    println("Failed to send message: ${e.message}")
//                }
//
//
                callOutputRoutine.join()
            }
        } catch (e: Exception) {
            isConnected.value = false
            println("Ошибка соединения: $e")
        }
    }
}