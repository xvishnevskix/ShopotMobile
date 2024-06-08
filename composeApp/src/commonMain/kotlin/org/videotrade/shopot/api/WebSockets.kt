package org.videotrade.shopot.api

import androidx.compose.runtime.MutableState
import cafe.adriel.voyager.navigator.Navigator
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
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.usecase.ChatUseCase
import org.videotrade.shopot.domain.usecase.ChatsUseCase
import org.videotrade.shopot.domain.usecase.ContactsUseCase
import org.videotrade.shopot.presentation.screens.main.MainScreen

suspend fun handleConnectWebSocket(
    navigator: Navigator,
    webSocketSession: MutableStateFlow<DefaultClientWebSocketSession?>,
    isConnected: MutableState<Boolean>,
    userId: String,
    chatUseCase: ChatUseCase,
    chatsUseCase: ChatsUseCase,
    contactsUseCase: ContactsUseCase
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
                                        
                                        println("getUserChatsgetUserChats $jsonElement")
                                        
                                        val dataJson = jsonElement.jsonObject["data"]?.jsonArray
                                        
                                        val chats = mutableListOf<ChatItem>()
                                        if (dataJson != null) {
                                            
                                            
                                            fun normalizePhoneNumber(phone: String): String {
                                                return phone.replace(Regex("[^0-9]"), "")
                                            }
                                            
                                            val contactsMap =
                                                contactsUseCase.contacts.value.associateBy {
                                                    normalizePhoneNumber(it.phone)
                                                }
                                            
                                            
                                            println("sortChat $dataJson")
                                            
                                            for (chatItem in dataJson) {
                                                val chat: ChatItem =
                                                    Json.decodeFromString(chatItem.toString())
                                                
                                                
                                                val normalizedChatPhone =
                                                    normalizePhoneNumber(chat.phone)
                                                
                                                val contact = contactsMap[normalizedChatPhone]
                                                
                                                if (contact != null) {
                                                    val sortChat = chat.copy(
                                                        firstName = "${contact.firstName}",
                                                        lastName = "${contact.lastName}"
                                                    )
                                                    println("sortChat $sortChat")
                                                    chats.add(sortChat)
                                                } else {
                                                    chats.add(chat)
                                                    
                                                }
                                                
                                            }
                                            
                                            println("chats $chats")
                                            
                                            chatsUseCase.addChats(chats) // Инициализация сообщений
                                        }
                                        
                                    } catch (e: Exception) {
                                        Logger.d("Error228: $e")
                                    }
                                    
                                }
                                
                                "getMessages" -> {
                                    try {
                                        println("jsonElement111 $jsonElement")
                                        
                                        
                                        val dataJson =
                                            jsonElement.jsonObject["data"]?.jsonArray
                                        
                                        
                                        val messages = mutableListOf<MessageItem>()
                                        
                                        if (dataJson != null) {
                                            
                                            for (messageItem in dataJson) {
                                                
                                                
                                                val message: MessageItem =
                                                    Json.decodeFromString(messageItem.toString())
                                                
                                                
                                                
                                                messages.add(message)
                                                
                                            }
                                            
                                            
                                            
                                            chatUseCase.initMessages(messages)// Инициализация сообщений
                                        }
                                        
                                    } catch (e: Exception) {
                                        
                                        Logger.d("Error228: $e")
                                    }
                                    
                                }
                                
                                "messageSent" -> {
                                    try {
                                        
                                        
                                        
                                        val messageJson =
                                            jsonElement.jsonObject["message"]?.jsonObject
                                        
                                        
                                        
                                        if (messageJson != null) {
                                            
                                            
                                            println("tttt ${messageJson}")
                                            
                                            
                                            val message: MessageItem =
                                                Json.decodeFromString(messageJson.toString())
                                            
                                            
                                            
                                            chatUseCase.addMessage(message)// Инициализация сообщений
                                            
                                            chatsUseCase.updateLastMessageChat(message)// Инициализация сообщений
                                            
                                        }
                                        
                                    } catch (e: Exception) {
                                        
                                        Logger.d("Error228: $e")
                                    }
                                    
                                }
                                
                                "messageDeleted" -> {
                                    try {
                                        
                                        
                                        println("messagePoka $jsonElement")
                                        
                                        val dataJson =
                                            jsonElement.jsonObject["data"]?.jsonObject
                                        
                                        
                                        
                                        if (dataJson != null) {
                                            
                                            val messageJson =
                                                jsonElement.jsonObject["data"]?.jsonObject
                                            
                                            val message: MessageItem =
                                                Json.decodeFromString(messageJson.toString())
                                            
                                            
                                            println("messagePoka $message")
                                            
                                            
                                            chatUseCase.addMessage(message)// Инициализация сообщений
                                            
                                            
                                        }
                                        
                                    } catch (e: Exception) {
                                        
                                        Logger.d("Error228: $e")
                                    }
                                    
                                    
                                }
                                
                                "messageRemoved" -> {
                                    try {
                                        
                                        
                                        println("messagePoka $jsonElement")
                                        
                                        val messageJson =
                                            jsonElement.jsonObject["message"]?.jsonObject
                                        
                                        
                                        
                                        if (messageJson != null) {
                                            
                                            
                                            val message: MessageItem =
                                                Json.decodeFromString(messageJson.toString())
                                            
                                            
                                            chatUseCase.delMessage(message)// Инициализация сообщений
                                        }
                                        
                                    } catch (e: Exception) {
                                        
                                        Logger.d("Error228: $e")
                                    }
                                    
                                    
                                }
                                
                                
                                "messageReadNotification" -> {
                                    try {
                                        
                                        
                                        val messageJson =
                                            jsonElement.jsonObject["message"]?.jsonObject
                                        
                                        println("messageReadNotification1 $messageJson")
                                        
                                        if (messageJson != null) {


                                            val messageId =
                                                messageJson["id"]?.jsonPrimitive?.content
                                            
                                            
                                            if (messageId != null) {
                                                chatUseCase.readMessage(messageId)
                                            }
                                        }
                                        
                                    } catch (e: Exception) {
                                        
                                        Logger.d("Error228: $e")
                                    }
                                    
                                    
                                }
                                
                                "createChat" -> {
                                    
                                    try {
                                        
                                        
                                        val dataJson =
                                            jsonElement.jsonObject["data"]?.jsonObject
                                        
                                        
                                        if (dataJson != null) {
                                            
                                            
                                            val chat =
                                                Json.decodeFromString<ChatItem>(dataJson.toString())
                                            
                                            println("createChat1 $chat")
                                            
                                            
                                            chatsUseCase.addChat(chat)
                                            
//                                            navigator.push(MainScreen())
                                        }
                                        
                                    } catch (e: Exception) {
                                        
                                        Logger.d("Error228: $e")
                                    }
                                    
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