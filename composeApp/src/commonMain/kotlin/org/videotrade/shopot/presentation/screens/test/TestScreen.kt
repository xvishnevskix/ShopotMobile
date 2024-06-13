package org.videotrade.shopot.presentation.screens.test

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp

import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.transitions.SlideTransition


class TestScreen : Screen {
    @Composable
    override fun Content() {
        MaterialTheme {
            TabNavigator(
                tab = HomeTab
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomNavigation {
                            TabNavigationItem(HomeTab)
                            TabNavigationItem(SettingsTab)
                        }
                    },
                    content = { CurrentTab() },
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
                    painter = icon,
                    contentDescription =
                    tab.options.title
                )
            }
        },
        label = {
            Text(
                text = tab.options.title
            )
        }
    )
}

object HomeTab : Tab {
    
    override val options: TabOptions
        @Composable
        get() {
            val title = "Home"
            val icon = rememberVectorPainter(Icons.Default.Home)
            
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
        Navigator(screen = HomeScreen()) { navigator ->
            SlideTransition(navigator = navigator)
        }
    }
}


class HomeScreen : Screen {
    
    @Composable
    override fun Content() {
        val navigator: Navigator = LocalNavigator.currentOrThrow
        
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            androidx.compose.material.Text(text = "Home")
            
            Button(onClick = {
                navigator.push(SecondScreen())
            }) {
                androidx.compose.material.Text(text = "Go to example details")
            }
        }
    }
}


object SettingsTab : Tab {
    
    override val options: TabOptions
        @Composable
        get() {
            val title = "Settings"
            val icon = rememberVectorPainter(Icons.Default.Settings)
            
            return remember {
                TabOptions(
                    index = 1u,
                    title = title,
                    icon = icon
                )
            }
        }
    
    @Composable
    override fun Content() {
        Navigator(screen = SettingsScreen()) { navigator ->
            SlideTransition(navigator = navigator)
        }
    }
}

class SettingsScreen : Screen {
    
    @Composable
    override fun Content() {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val navigator: Navigator = LocalNavigator.currentOrThrow
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable {
                    },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = rememberVectorPainter(Icons.Default.Info),
                    contentDescription = "Legal",
                )
                androidx.compose.material.Text(
                    modifier = Modifier.padding(16.dp),
                    text = "Legal"
                )
            }
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }
    }
}

class SecondScreen : Screen {
    @Composable
    override fun Content() {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Second Screen")
        }
    }
}