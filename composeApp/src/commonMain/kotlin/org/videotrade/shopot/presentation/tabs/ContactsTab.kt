package org.videotrade.shopot.presentation.tabs

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.transitions.SlideTransition
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.presentation.screens.apps.AppsScreen
import org.videotrade.shopot.presentation.screens.contacts.CreateChatScreen
import org.videotrade.shopot.presentation.screens.main.MainScreen
import org.videotrade.shopot.presentation.screens.profile.ProfileScreen
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.apps
import shopot.composeapp.generated.resources.contacts_nav

object ContactsTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(MokoRes.strings.
            contacts
//                applications
            )
            val icon: Painter = painterResource(Res.drawable.
            contacts_nav
//                apps
            )
            
            
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
        Navigator(screen =
        CreateChatScreen()
//        AppsScreen()
        ) { navigator ->
            SlideTransition(navigator = navigator)
            
            
        }
    }
}
