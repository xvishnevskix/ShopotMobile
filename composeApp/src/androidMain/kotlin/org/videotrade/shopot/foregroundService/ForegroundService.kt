package org.videotrade.shopot.foregroundService

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.http.HttpMethod
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.videotrade.shopot.api.getValueInStorage
import org.videotrade.shopot.multiplatform.NotificationHelper

class ForegroundService : Service() {
    
    override fun onCreate() {
        super.onCreate()
        startForeground(1, createNotification())
        CoroutineScope(Dispatchers.IO).launch {
            handleBackgroundWebSocket()
        }
    }
    
    private fun createNotification(): Notification {
        val channelId = "background_channel"
        val channelName = "Background Service"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
        
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Foreground Service")
            .setContentText("Running in the background")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()
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
                                NotificationHelper.showNotification(
                                    applicationContext,
                                    "Privet Sergey",
                                    "Все работает"
                                )
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
