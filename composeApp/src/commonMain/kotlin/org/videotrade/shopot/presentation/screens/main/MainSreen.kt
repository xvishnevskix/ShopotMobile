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
        val navigator = LocalNavigator.current
        
        println("LocalNavigator ${LocalNavigator.current}")
        
        
        LaunchedEffect(Unit) {
            if (navigator != null) {
                viewModel.getNavigator(navigator)
                
                viewModel.getProfile()
                viewModel.loadUsers()
            }
            

            
            
            
            
        }
        
        Drawer(drawerState, viewModel)
    }
}
