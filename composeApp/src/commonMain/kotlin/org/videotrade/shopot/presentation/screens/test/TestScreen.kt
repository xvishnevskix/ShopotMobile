package org.videotrade.shopot.presentation.screens.test

import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import cafe.adriel.voyager.core.screen.Screen
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.videotrade.shopot.parkingProj.presentation.components.SafeArea


class TestScreen : Screen {
    @Composable
    override fun Content() {
        val scope = rememberCoroutineScope()
        val wsSession = MutableStateFlow<DefaultClientWebSocketSession?>(null)



        MaterialTheme {
            SafeArea {
                Button(onClick = {
                    scope.launch {
                    }
                }, content = {
                    Text("AAAAAA")
                })
            }
        }
    }
}





