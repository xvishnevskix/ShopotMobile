package org.videotrade.shopot.presentation.screens.chats

import androidx.compose.material.DrawerValue
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
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
        
        
        
        LaunchedEffect(Unit) {
            if (commonViewModel.mainNavigator.value != null) {
                viewModel.getNavigator(commonViewModel.mainNavigator.value!!)
                
                viewModel.getProfile()
                viewModel.loadUsers()
            }
        }
        
        MainContentComponent(viewModel, commonViewModel)
        
    }
}