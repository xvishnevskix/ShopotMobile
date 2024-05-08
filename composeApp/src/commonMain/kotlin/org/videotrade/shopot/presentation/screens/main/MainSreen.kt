package org.videotrade.shopot.presentation.screens.main

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.launch
import org.videotrade.shopot.api.connectionWs
import org.videotrade.shopot.presentation.screens.main.MainViewModel
import org.koin.compose.koinInject
import org.videotrade.shopot.presentation.components.Main.Drawer


class MainScreen : Screen {
    
    
    @Composable
    override fun Content() {
        
        val viewModel: MainViewModel = koinInject()
        val chatState = viewModel.chats.collectAsState(initial = listOf()).value
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        
        
        val coroutineScope = rememberCoroutineScope()
  
        
        coroutineScope.launch {
//            connectionWs()
            
        }
        

        
        Drawer(drawerState, chatState)
        
        
    }
}
