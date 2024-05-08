package org.videotrade.shopot.api

import io.ktor.client.*
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.http.HttpMethod
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

suspend fun connectionWs() {
    
    
    val client = HttpClient {
        install(WebSockets)
    }
    
    
    
    
    client.webSocket(
        method = HttpMethod.Get,
        host = "ws://192.168.31.223:3000",
        port = 3000,
        path = "/test"
    ) {
        val messageOutputRoutine = launch {
            for (message in incoming) {
                message as? Frame.Text ?: continue
                val text = message.readText()
                println("Message from server: $text")
                // Здесь вы можете десериализовать сообщение и обработать его
            }
        }
        
        // Отправка сообщения на сервер
        send(Frame.Text("Hello from client!"))
        
        messageOutputRoutine.join()
    }
}
