package org.videotrade.shopot

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import org.videotrade.shopot.presentation.screens.call.IncomingCallScreen
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.intro.IntroScreen
import org.videotrade.shopot.presentation.screens.main.MainViewModel
import org.videotrade.shopot.theme.AppTheme

@Composable
internal fun App() = AppTheme {
    val viewModel: MainViewModel = koinInject()
    val commonViewModel: CommonViewModel = koinInject()
    
    
//    val nav = if (commonViewModel.appIsActive.value) {
//        IntroScreen()
//    } else {
//        IncomingCallScreen("", "", "", "", "")
//    }
    
    KoinContext {
        
        Navigator(
//            nav
                    IntroScreen()
            
//            TestScreen()
//            ChatScreen(                ChatItem(
//                    "", true, "", "", "", "", 0, "", MessageItem(
//                        "", "", "", "", "", 0,
//                        listOf(), false, "", true, true, null
//                    ), "", ""
//                )
//            )
        ) { navigator ->
            SlideTransition(navigator)
        }
        
    }
}


