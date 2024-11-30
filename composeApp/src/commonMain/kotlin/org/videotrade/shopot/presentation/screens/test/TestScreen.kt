package org.videotrade.shopot.presentation.screens.test

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.screen.Screen
import org.koin.compose.koinInject
import org.videotrade.shopot.multiplatform.NetworkListener
import org.videotrade.shopot.multiplatform.NetworkStatus
import org.videotrade.shopot.presentation.components.Common.SafeArea

class TestScreen : Screen {
    @Composable
    override fun Content() {
        
        val networkListener: NetworkListener = koinInject()
        val networkStatus by networkListener.networkStatus.collectAsState(NetworkStatus.Disconnected)
        
        SafeArea {
            
            Column {
            }
            Text(
                text = networkStatus.toString(), color = Color.Black,
            )
            
        }
        
        
    }
}
