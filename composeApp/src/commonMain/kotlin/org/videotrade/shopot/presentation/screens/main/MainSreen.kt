package org.videotrade.shopot.presentation.screens.main

import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import cafe.adriel.voyager.core.screen.Screen
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.http.HttpMethod
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

class MainScreen : Screen {
    // Инициализация HTTP клиента
    private val httpClient = HttpClient {
        install(WebSockets)
    }
    
    @Composable
    override fun Content() {
        val viewModel: MainViewModel = koinInject()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val coroutineScope = rememberCoroutineScope()
        val webSocketSession = remember { mutableStateOf<DefaultClientWebSocketSession?>(null) }
        val isConnected = remember { mutableStateOf(false) }
        
        // Управление жизненным циклом WebSocket
        DisposableEffect(Unit) {
            val job = coroutineScope.launch {
                if (!isConnected.value) {
                    try {
                        httpClient.webSocket(
                            method = HttpMethod.Get,
                            host = "192.168.31.223",
                            port = 3001,
                            path = "/message"
                        ) {
                            webSocketSession.value = this
                            isConnected.value = true
                            for (frame in incoming) {
                                println(frame)
                            }
                        }
                    } catch (e: Exception) {
                        isConnected.value = false
                        println("Ошибка соединения: $e")
                    }
                }
            }
            
            onDispose {
                job.cancel()
                coroutineScope.launch {
                    webSocketSession.value?.close()
                }
            }
        }
        
        Button(onClick = {
            coroutineScope.launch {
                if (isConnected.value && webSocketSession.value?.outgoing?.isClosedForSend == false) {
                    webSocketSession.value?.send(Frame.Text("dadadadada"))
                } else {
                    println("WebSocket session is closed or not ready for sending.")
                }
            }
        }, content = { Text("Отправить") })
    }
}
