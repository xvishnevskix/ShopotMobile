package org.videotrade.shopot.presentation.screens.test

import androidx.compose.material.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.http.HttpMethod
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.channels.consumeEach
import org.koin.compose.koinInject
import org.videotrade.shopot.multiplatform.getAppLifecycleObserver
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.screens.common.CommonViewModel

class TestScreen : Screen {
    @Composable
    override fun Content() {
        val scope = rememberCoroutineScope()
        val commonViewModel: CommonViewModel = koinInject()
        val navigator = LocalNavigator.currentOrThrow
        
        
        
        LaunchedEffect(Unit) {
            
            
        }
        
 
        
        MaterialTheme {
            
            SafeArea {
                Button(content = {
                    Text("Connect")
                }, onClick = {
                    val httpClient = HttpClient {
                        install(WebSockets)
                    }
                    
                    scope.launch {
                        commonViewModel.connectionWs(navigator)
                    }
                })
            }
        }
    }
}
