package org.videotrade.shopot.presentation.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import org.jetbrains.compose.resources.Font
import org.koin.compose.koinInject
import org.videotrade.shopot.api.getValueInStorage
import org.videotrade.shopot.presentation.screens.call.CallViewModel
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.tabs.ChatsTab
import org.videotrade.shopot.presentation.tabs.ContactsTab
import org.videotrade.shopot.presentation.tabs.ProfileTab
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular


class MainScreen : Screen {
    
    @Composable
    override fun Content() {
        val commonViewModel: CommonViewModel = koinInject()
        val navigator = LocalNavigator.currentOrThrow

        
        
        LaunchedEffect(Unit) {
            commonViewModel.setAppIsActive(true)
            commonViewModel.setMainNavigator(navigator)
        }
        
        
        
        MaterialTheme {
            TabNavigator(
                tab = ChatsTab
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomNavigation(
                            backgroundColor = Color(241, 238, 238),
                            modifier = Modifier
                                .background(Color(241, 238, 238))
                                .windowInsetsPadding(WindowInsets.navigationBars) // This line adds padding for the navigation bar
                            ,
                            elevation = 0.dp // Убирает тень снизу
                        ) {
                            TabNavigationItem(ProfileTab)
                            TabNavigationItem(ChatsTab)
                            TabNavigationItem(ContactsTab)
                        }
                    },
                    content = {
                        Column(modifier = Modifier.fillMaxSize()) {
                            CurrentTab()
                        }
                    },
                )
            }
        }
        
    }
    
}

@Composable
private fun RowScope.TabNavigationItem(tab: Tab) {
    val tabNavigator: TabNavigator = LocalTabNavigator.current
    
    BottomNavigationItem(
        selected = tabNavigator.current == tab,
        onClick = { tabNavigator.current = tab },
        icon = {
            tab.options.icon?.let { icon ->
                Icon(
                    modifier = Modifier
                        .size(30.dp)
                        .padding(bottom = 5.dp),
                    painter = icon,
                    contentDescription = tab.options.title,
                    tint = if (tabNavigator.current == tab) Color(3, 104, 255) else Color.Black
                )
            }
        },
        label = {
            Text(
                tab.options.title,
                textAlign = TextAlign.Center,
                fontSize = 13.sp,
                fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                lineHeight = 20.sp,
                color = if (tabNavigator.current == tab) Color(3, 104, 255) else Color.Black,
            )
        },
        modifier = Modifier.size(56.dp)
    )
//    Spacer(modifier = Modifier.height(50.dp))
}

