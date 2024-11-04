package org.videotrade.shopot.presentation.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
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
import org.videotrade.shopot.multiplatform.setScreenLockFlags
import org.videotrade.shopot.presentation.screens.call.CallViewModel
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.tabs.ChatsTab
import org.videotrade.shopot.presentation.tabs.ContactsTab
import org.videotrade.shopot.presentation.tabs.ProfileTab
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
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
        val currentScreen = navigator.lastItem
        
        
        println("currentScreen $currentScreen")
        
        
        MaterialTheme {
            TabNavigator(
                tab = ChatsTab
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomNavigation(
                            backgroundColor = Color.White,
                            modifier = Modifier
                                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                                .background(Color.White)
                                .windowInsetsPadding(WindowInsets.navigationBars) // This line adds padding for the navigation bar
                            ,
                            elevation = 0.dp // Убирает тень снизу
                        ) {
                            TabNavigationItem(ProfileTab, width = 18.dp, height = 18.dp)
                            TabNavigationItem(ChatsTab, width = 18.dp, height = 17.dp)
                            TabNavigationItem(ContactsTab, width = 18.dp, height = 15.dp)
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
private fun RowScope.TabNavigationItem(tab: Tab, width: Dp, height: Dp) {
    val tabNavigator: TabNavigator = LocalTabNavigator.current
    
    BottomNavigationItem(
        selected = tabNavigator.current == tab,
        onClick = { tabNavigator.current = tab },
        icon = {
            tab.options.icon?.let { icon ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(24.dp)
                ) {
                Icon(
                    modifier = Modifier
                        .size(24.dp)
                        .padding(bottom = 5.dp),
                    painter = icon,
                    contentDescription = tab.options.title,
                    tint = if (tabNavigator.current == tab) Color(0xFFCAB7A3) else Color(0xFF373533)
                )
                }
            }
        },
        label = {

                Text(
                    tab.options.title,
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                    fontWeight = FontWeight(500),
                    textAlign = TextAlign.Center,
                    color = if (tabNavigator.current == tab) Color(0xFFCAB7A3) else Color(0xFF373533),
                )

        },
        modifier = Modifier.size(56.dp)
    )
//    Spacer(modifier = Modifier.height(50.dp))
}

