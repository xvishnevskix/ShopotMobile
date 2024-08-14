package org.videotrade.shopot
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.presentation.screens.chat.ChatScreen
import org.videotrade.shopot.presentation.screens.intro.IntroScreen
import org.videotrade.shopot.presentation.screens.main.MainViewModel
import org.videotrade.shopot.presentation.screens.test.TestScreen
import org.videotrade.shopot.theme.AppTheme

@Composable
internal fun App() = AppTheme {
    val viewModel: MainViewModel = koinInject()
    KoinContext {
        
        Navigator(
            IntroScreen()
//            TestScreen()
//            ChatScreen(//                ChatItem(
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


