package org.videotrade.shopot.presentation.screens.main

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.koinInject
import org.videotrade.shopot.presentation.components.Main.Drawer


class MainScreen : Screen {
    
    
    @Composable
    override fun Content() {
        
        val viewModel: MainViewModel = koinInject()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
//        val coroutineScope = rememberCoroutineScope()
        val navigator = LocalNavigator.currentOrThrow
        
        viewModel.getNavigator(navigator)
        
        
        
//        LaunchedEffect(viewModel.callWsSession.value) {
//
//
//            viewModel.callWsSession.collect {
//                println("op3131 ${it}")
//
//            }
//
//
//        }
//
        Drawer(drawerState, viewModel)
        
        
    }
}
