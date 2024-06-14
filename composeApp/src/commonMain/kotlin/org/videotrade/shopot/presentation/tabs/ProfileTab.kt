package org.videotrade.shopot.presentation.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.transitions.SlideTransition
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.presentation.screens.main.MainScreen
import org.videotrade.shopot.presentation.screens.main.MainViewModel
import org.videotrade.shopot.presentation.screens.profile.ProfileScreen
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.profileNav

object ProfileTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val title = "Profile"
            val icon: Painter = painterResource(Res.drawable.profileNav)
            
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
        val viewModel: MainViewModel = koinInject()
        
        Navigator(screen = ProfileScreen(viewModel.profile.value)) { navigator ->
            SlideTransition(navigator = navigator)
        }
    }
}
