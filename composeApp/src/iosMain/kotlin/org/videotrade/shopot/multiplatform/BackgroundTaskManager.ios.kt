package org.videotrade.shopot.multiplatform

import kotlinx.coroutines.*
import platform.Foundation.*
import platform.UserNotifications.*
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.websocket.*
import org.videotrade.shopot.api.getValueInStorage
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class BackgroundService {
    
    init {
        startBackgroundTask()
    }
    
    @OptIn(DelicateCoroutinesApi::class)
    private fun startBackgroundTask() {
        GlobalScope.launch {
            handleBackgroundWebSocket()
        }
    }
    
    private suspend fun handleBackgroundWebSocket() {
        val httpClient = HttpClient {
            install(WebSockets)
        }
        
        val profileId = getValueInStorage("profileId")
        
        try {
            httpClient.webSocket(
                method = HttpMethod.Get,
                host = "192.168.31.223",
                port = 3001,
                path = "/message",
                request = {
                    profileId?.let { url.parameters.append("callerId", it) }
                }
            ) {
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        val text = frame.readText()
                        val jsonElement = Json.parseToJsonElement(text)
                        val type = jsonElement.jsonObject["type"]?.jsonPrimitive?.content
                        
                        when (type) {
                            "newCall" -> {
                                showNotification("Privet Sergey", "Все работает")
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun showNotification(title: String, message: String) {
        val content = UNMutableNotificationContent().apply {
            setTitle("Привет, Сергей")
            setBody("Все работает")
            setSound(UNNotificationSound.defaultSound())
            
        }
        
        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(1.0, false)
        val request = UNNotificationRequest.requestWithIdentifier("backgroundTask", content, trigger)
        
        UNUserNotificationCenter.currentNotificationCenter().addNotificationRequest(request) { error ->
            if (error != null) {
                println("Ошибка при добавлении уведомления: ${error.localizedDescription}")
            }
        }
    }
}


actual class BackgroundTaskManager {
    actual fun scheduleTask() {
    }
}

actual object BackgroundTaskManagerFactory {
    actual fun create(): BackgroundTaskManager {
        TODO("Not yet implemented")
    }
}