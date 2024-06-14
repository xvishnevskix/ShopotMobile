package org.videotrade.shopot.presentation.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.transitions.SlideTransition
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.presentation.screens.chats.ChatsScreen
import org.videotrade.shopot.presentation.screens.main.MainScreen
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.chatNav
import shopot.composeapp.generated.resources.profileNav

object ChatsTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val title = "Chats"
            val icon: Painter = painterResource(Res.drawable.chatNav)
            
            
            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon
                )
            }
        }
    
    @Composable
    override fun Content() {
        Navigator(screen = ChatsScreen()) { navigator ->
            SlideTransition(navigator = navigator)
        }
    }
}
