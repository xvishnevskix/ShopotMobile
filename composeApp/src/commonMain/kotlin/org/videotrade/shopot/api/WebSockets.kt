package org.videotrade.shopot.api

import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.http.HttpMethod
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.launch

suspend fun connectionWs(setSession: (DefaultClientWebSocketSession?) -> Unit) {
    
    
    val client = HttpClient {
        install(WebSockets)
    }
    
    
    try {
        client.webSocket(
            method = HttpMethod.Get,
            host = "192.168.31.223",
            port = 3001,
            path = "/message",
        ) {
            setSession(this)
            
//            val messageOutputRoutine = launch {
//                for (message in incoming) {
//                    message as? Frame.Text ?: continue
//                    val text = message.readText()
//                    println("Message from server: $text")
//                }
//            }
//
////            send(Frame.Text("Hello from client!"))
//            messageOutputRoutine.join()
        }
        
    } catch (e: Exception) {
        println("Error111: $e")
        
    }
    
    
}
