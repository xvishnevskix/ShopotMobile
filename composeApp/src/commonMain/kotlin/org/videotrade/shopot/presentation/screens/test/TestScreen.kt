package org.videotrade.shopot.presentation.screens.test

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.screen.Screen
import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.http.HttpMethod
import io.ktor.util.decodeBase64Bytes
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.koin.compose.koinInject
import org.koin.mp.KoinPlatform
import org.videotrade.shopot.api.EnvironmentConfig
import org.videotrade.shopot.api.decupsMessage
import org.videotrade.shopot.api.getValueInStorage
import org.videotrade.shopot.api.monitorWebSocketConnection
import org.videotrade.shopot.api.normalizePhoneNumber
import org.videotrade.shopot.api.reconnectWebSocket
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.repository.ChatRepository
import org.videotrade.shopot.multiplatform.AudioFactory
import org.videotrade.shopot.multiplatform.CipherWrapper
import org.videotrade.shopot.multiplatform.EncapsulationFileResult
import org.videotrade.shopot.multiplatform.FileProviderFactory
import org.videotrade.shopot.multiplatform.MusicType
import org.videotrade.shopot.multiplatform.PermissionsProviderFactory
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.main.MainViewModel
import org.videotrade.shopot.presentation.tabs.ChatsTab
import kotlin.random.Random


class TestScreen : Screen {
    @Composable
    override fun Content() {
        val scope = rememberCoroutineScope()

        
        
        MaterialTheme {
            SafeArea {
                Column {
                    Button(
                        onClick = {

                            val userId = getValueInStorage("profileId")

                            scope.launch {
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

                                    // Запускам повторное подключение
                                    if (userId != null) {
                                        reconnectWebSocket(userId)
                                    }
                                }
                            }

                        }
                    ) {
                        Text(
                             "Start",
                            color = Color.White
                        )
                    }


                    Button(
                        onClick = {

                        }
                    ) {
                        Text(
                            "Start",
                            color = Color.White
                        )
                    }

                }
            }
        }
    }
}


