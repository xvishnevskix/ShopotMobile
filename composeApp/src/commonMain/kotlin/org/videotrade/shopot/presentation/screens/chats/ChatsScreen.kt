package org.videotrade.shopot.presentation.screens.chats

import androidx.compose.material.DrawerValue
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.TabNavigator
import org.koin.compose.koinInject
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.presentation.components.Main.Drawer
import org.videotrade.shopot.presentation.components.Main.MainContentComponent
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.main.MainViewModel

class ChatsScreen : Screen {
    
    @Composable
    override fun Content() {
        val viewModel: MainViewModel = koinInject()
        val commonViewModel: CommonViewModel = koinInject()
        val tabNavigator: TabNavigator = LocalTabNavigator.current
        
        
        
        LaunchedEffect(Unit) {
            if (commonViewModel.mainNavigator.value != null) {
                commonViewModel.setTabNavigator(tabNavigator)
                viewModel.getNavigator(commonViewModel.mainNavigator.value!!)
                
                viewModel.getProfile()
                viewModel.loadUsers()
                println("AAAAAA getMessagesBack")
                
            }
        }
        
        MainContentComponent(viewModel, commonViewModel)
        
    }
}