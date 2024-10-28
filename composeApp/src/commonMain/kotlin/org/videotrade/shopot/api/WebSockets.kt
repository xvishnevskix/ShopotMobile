package org.videotrade.shopot.api

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
import org.koin.mp.KoinPlatform
import org.videotrade.shopot.api.EnvironmentConfig.webSocketsUrl
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.usecase.ChatUseCase
import org.videotrade.shopot.domain.usecase.ChatsUseCase
import org.videotrade.shopot.domain.usecase.ContactsUseCase
import org.videotrade.shopot.multiplatform.AudioFactory
import org.videotrade.shopot.multiplatform.CipherWrapper
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.main.MainViewModel
import org.videotrade.shopot.presentation.tabs.ChatsTab

suspend fun handleConnectWebSocket(
    navigator: Navigator,
    webSocketSession: MutableStateFlow<DefaultClientWebSocketSession?>,
    isConnected: MutableStateFlow<Boolean>,
    userId: String,
    chatUseCase: ChatUseCase,
    chatsUseCase: ChatsUseCase,
    contactsUseCase: ContactsUseCase,
    cipherWrapper: CipherWrapper
) {


    val httpClient = HttpClient {
        install(WebSockets)

    }

    println("isConnected.value ${isConnected.value}")

    if (!isConnected.value) {
        println("isConnected.value ${isConnected.value}")

        try {
            httpClient.webSocket(
                method = HttpMethod.Get,
                host = webSocketsUrl,
                port = 5050,
                path = "/chat?userId=$userId",

                ) {
                val mainViewModel: MainViewModel = KoinPlatform.getKoin().get()


                webSocketSession.value = this
                isConnected.value = true

                mainViewModel.getChatsInBack(this, userId)

                val callOutputRoutine = launch {

                    for (frame in incoming) {
                        if (frame is Frame.Text) {

                            val text = frame.readText()

                            println("NewFrame $text")

                            val jsonElement = Json.parseToJsonElement(text)
                            val action = jsonElement.jsonObject["action"]?.jsonPrimitive?.content


                            when (action) {
                                "getUserChats" -> {
                                    try {

                                        println("getUserChatsgetUserChats $jsonElement")

                                        val dataJson = jsonElement.jsonObject["data"]?.jsonArray

                                        val chats = mutableListOf<ChatItem>()

                                        println("chatSize ${dataJson?.size}")

                                        val mainViewModel: MainViewModel =
                                            KoinPlatform.getKoin().get()


//                                        commonViewModel.toaster.show("chats size ${dataJson?.size}")

                                        if (dataJson != null) {

                                            val contactsMap =
                                                contactsUseCase.contacts.value.associateBy {
                                                    normalizePhoneNumber(it.phone)
                                                }


                                            println("sortChat $dataJson")

                                            for (chatItem in dataJson) {
                                                println("chat $chatItem")

                                                val chat: ChatItem =
                                                    Json.decodeFromString(chatItem.toString())
                                                println("chat $chat")


                                                var newChat = chat

                                                if (chat.lastMessage?.content?.isNotBlank() == true) {
                                                    val lastMessageContent = decupsMessage(
                                                        chat.lastMessage?.content!!,
                                                        cipherWrapper
                                                    )

                                                    println("lastMessageContent")

                                                    newChat = chat.copy(
                                                        lastMessage = chat.lastMessage!!.copy(
                                                            content = lastMessageContent
                                                        )
                                                    )

                                                }


                                                if (chat.personal) {
                                                    val normalizedChatPhone =
                                                        newChat.phone?.let { normalizePhoneNumber(it) }

                                                    val contact = contactsMap[normalizedChatPhone]

                                                    if (contact != null) {
                                                        val sortChat = newChat.copy(
                                                            firstName = "${contact.firstName}",
                                                            lastName = "${contact.lastName}"
                                                        )
                                                        println("sortChat $sortChat")
                                                        chats.add(sortChat)
                                                    } else {
                                                        chats.add(newChat)

                                                    }
                                                } else {
                                                    chats.add(newChat)
                                                }

                                            }

                                            println("chats $chats")


                                            chatsUseCase.addChats(
                                                mainViewModel.sortChatsByLastMessageCreated(
                                                    chats
                                                ).toMutableList()
                                            ) // Инициализация сообщений

                                        }

                                    } catch (e: Exception) {
                                        Logger.d("Error228: $e")
                                    }

                                }

                                "getMessages" -> {
                                    try {
                                        val dataJson =
                                            jsonElement.jsonObject["data"]?.jsonArray

                                        println("getMessages111111 ${dataJson?.size}")

                                        val messages = mutableListOf<MessageItem>()

                                        if (dataJson != null) {

                                            for (messageItem in dataJson) {
                                                val message: MessageItem =
                                                    Json.decodeFromString(messageItem.toString())

                                                var messageNew = message

                                                // Декодируем content, если оно не пустое
                                                if (!message.content.isNullOrBlank()) {
                                                    val decups = decupsMessage(message.content, cipherWrapper)
                                                    messageNew = messageNew.copy(content = decups)
                                                }

                                                // Декодируем answerMessage.content, если оно не пустое
                                                message.answerMessage?.let { answerMessage ->
                                                    if (!answerMessage.content.isNullOrBlank()) {
                                                        val decupsAnswerMessage = decupsMessage(answerMessage.content, cipherWrapper)
                                                        if (decupsAnswerMessage != null) {
                                                            val updatedAnswerMessage = answerMessage.copy(content = decupsAnswerMessage)
                                                            messageNew = messageNew.copy(answerMessage = updatedAnswerMessage)
                                                        }
                                                    }
                                                }
8
                                                messages.add(messageNew)

                                            }


                                            chatUseCase.implementCount()
                                            chatUseCase.initMessages(messages)// Инициализация сообщений
                                        }

                                    } catch (e: Exception) {

                                        Logger.d("Error228: $e")
                                    }

                                }

                                "messageSent" -> {
                                    try {
                                        val messageJson = jsonElement.jsonObject["message"]?.jsonObject

                                        if (messageJson != null) {
                                            println("ttttaaaaa $messageJson")
                                            println("currentChat ${chatsUseCase.currentChat.value}")

                                            val message: MessageItem = Json.decodeFromString(messageJson.toString())
                                            var messageNew = message

                                            // Декодируем content, если оно не пустое
                                            if (!message.content.isNullOrBlank()) {
                                                val decups = decupsMessage(message.content, cipherWrapper)
                                                messageNew = messageNew.copy(content = decups)
                                            }

                                            // Декодируем answerMessage.content, если оно не пустое
                                            message.answerMessage?.let { answerMessage ->
                                                if (!answerMessage.content.isNullOrBlank()) {
                                                    val decupsAnswerMessage = decupsMessage(answerMessage.content, cipherWrapper)
                                                    if (decupsAnswerMessage != null) {
                                                        val updatedAnswerMessage = answerMessage.copy(content = decupsAnswerMessage)
                                                        messageNew = messageNew.copy(answerMessage = updatedAnswerMessage)
                                                    }
                                                }
                                            }

                                            // Проверяем, совпадает ли currentChat с message.chatId
                                            if (chatsUseCase.currentChat.value == message.chatId) {
                                                chatUseCase.addMessage(messageNew)
                                            }

                                            // Обновляем последнее сообщение в чате
                                            chatsUseCase.updateLastMessageChat(messageNew)
                                             val musicPlayer = AudioFactory.createMusicPlayer()
                                            
                                            musicPlayer.play("newmess", false)
                                            
                                        }

                                    } catch (e: Exception) {
                                        Logger.d("Error228: $e")
                                    }
                                }


                                "sendUploadMessage" -> {
                                    try {
                                        val messageJson =
                                            jsonElement.jsonObject["message"]?.jsonObject



                                        if (messageJson != null) {


                                            println("tttt ${messageJson}")


                                            val message: MessageItem =
                                                Json.decodeFromString(messageJson.toString())

                                            println("message ${userId} ${message.fromUser}")


                                            var messageNew = message


                                            var answerMessage = ""

                                            if (message.answerMessage?.content?.isNotBlank() == true) {
                                                val decupsAnswerMessage = decupsMessage(
                                                    message.answerMessage?.content!!,
                                                    cipherWrapper
                                                )

                                                if (decupsAnswerMessage != null) {
                                                    answerMessage = decupsAnswerMessage
                                                }

                                            }

                                            messageNew = message.copy(
                                                answerMessage = if (message.answerMessage !== null)
                                                    message.answerMessage!!.copy(
                                                        content = answerMessage
                                                    ) else null
                                            )


                                            if (userId == messageNew.fromUser) {

                                                val uploadId =
                                                    jsonElement.jsonObject["uploadId"]?.jsonPrimitive?.content

                                                println("uploadId ${uploadId}")


                                                chatUseCase.updateUploadMessage(
                                                    messageNew.copy(
                                                        uploadId = uploadId
                                                    )
                                                )// Инициализация сообщений

                                            } else {
                                                println("message2 ${message}")


                                                if (chatsUseCase.currentChat.value == messageNew.chatId) {

                                                    chatUseCase.addMessage(messageNew)// Инициализация сообщений
                                                }
                                            }

                                            chatsUseCase.updateLastMessageChat(messageNew)// Инициализация сообщений
                                            
                                            
                                            val musicPlayer = AudioFactory.createMusicPlayer()
                                            
                                            musicPlayer.play("newmess", false)
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

                                            val message: MessageItem =
                                                Json.decodeFromString(messageJson.toString())


                                            val messageId =
                                                messageJson["id"]?.jsonPrimitive?.content


                                            if (messageId != null) {
                                                chatUseCase.readMessage(messageId)
                                            }

                                            if (message.fromUser == userId) {
                                                var messageNew = message

                                                if (message.content?.isNotBlank() == true) {
                                                    messageNew = message.copy(
                                                        content = decupsMessage(
                                                            message.content,
                                                            cipherWrapper
                                                        )
                                                    )
                                                }
//
                                                chatsUseCase.updateReadLastMessageChat(messageNew)
                                            } else {
//                                                chatsUseCase.updateReadLastMessageChat(message)

                                            }


                                        }

                                    } catch (e: Exception) {

                                        Logger.d("Error228: $e")
                                    }


                                }

                                "createChat" -> {
                                    try {
                                        println("createChat")

                                        val dataJson =
                                            jsonElement.jsonObject["data"]?.jsonObject


                                        if (dataJson != null) {

                                            println("createChatdataJson $dataJson")


                                            val chat =
                                                Json.decodeFromString<ChatItem>(dataJson.toString())

                                            println("createChat1 $chat")



                                            fun normalizePhoneNumber(phone: String): String {
                                                return phone.replace(Regex("[^0-9]"), "")
                                            }

                                            val contactsMap =
                                                contactsUseCase.contacts.value.associateBy {
                                                    normalizePhoneNumber(it.phone)
                                                }


                                            val normalizedChatPhone =
                                                chat.phone?.let { normalizePhoneNumber(it) }

                                            val contact = contactsMap[normalizedChatPhone]

                                            if (contact != null) {
                                                val sortChat = chat.copy(
                                                    firstName = "${contact.firstName}",
                                                    lastName = "${contact.lastName}"
                                                )
                                                println("sortChat $sortChat")
                                                chatsUseCase.addChat(sortChat)
                                            } else {
                                                chatsUseCase.addChat(chat)
                                            }

//                                            navigator.push(MainScreen())
                                        }

                                    } catch (e: Exception) {

                                        Logger.d("Error228: $e")
                                    }

                                }

                                "createGroupChat" -> {
                                    try {
                                        val commonViewModel: CommonViewModel =
                                            KoinPlatform.getKoin().get()

                                        println("createGroupChat ${jsonElement}")

                                        val dataJson =
                                            jsonElement.jsonObject["data"]?.jsonObject


                                        if (dataJson != null) {
                                            val chat =
                                                Json.decodeFromString<ChatItem>(dataJson.toString())

                                            chatsUseCase.addChat(chat)

                                            commonViewModel.tabNavigator.value?.current = ChatsTab
                                        }

                                    } catch (e: Exception) {

                                        Logger.d("Error228: $e")
                                    }

                                }

                                "messageForwarded" -> {
                                    try {
                                        val messageJson =
                                            jsonElement.jsonObject["message"]?.jsonObject

                                        if (messageJson != null) {

                                            println("ttttaaaaa ${messageJson}")

                                            println("currentChat ${chatsUseCase.currentChat.value}")


                                            val message: MessageItem =
                                                Json.decodeFromString(messageJson.toString())


                                            var messageNew = message

                                            if (message.content?.isNotBlank() == true) {
                                                messageNew = message.copy(
                                                    content = decupsMessage(
                                                        message.content,
                                                        cipherWrapper
                                                    )
                                                )
                                            }

                                            if (chatsUseCase.currentChat.value == message.chatId) {
                                                chatUseCase.addMessage(messageNew)
                                            }

                                            chatsUseCase.updateLastMessageChat(messageNew)// Инициализация сообщений
                                            
                                            val musicPlayer = AudioFactory.createMusicPlayer()
                                            
                                            musicPlayer.play("newmess", false)
                                        }

                                    } catch (e: Exception) {

                                        Logger.d("Error228: $e")
                                    }

                                }


//                                    {
//
//                                    val messageJson =
//                                        jsonElement.jsonObject["message"]?.jsonObject
//
//                                    val message =
//                                        Json.decodeFromString<MessageItem>(messageJson.toString())
//
//                                    chatUseCase.addMessage(message)
//                                }

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