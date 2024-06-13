package org.videotrade.shopot.presentation.screens.test

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
class TestScreen : Screen {
    @Composable
    override fun Content() {
        MainScreen()
    }
}

class HomeScreen : Screen {
    @Composable
    override fun Content() {
        Box(modifier = Modifier.fillMaxSize(0.9F), contentAlignment = Alignment.Center) {
            Text("Home Screen")
        }
    }
}

class ProfileScreen : Screen {
    @Composable
    override fun Content() {
        Box(modifier = Modifier.fillMaxSize(0.9F), contentAlignment = Alignment.Center) {
            Text("Profile Screen")
        }
    }
}

class SettingsScreen : Screen {
    @Composable
    override fun Content() {
        Box(modifier = Modifier.fillMaxSize(0.9F), contentAlignment = Alignment.Center) {
            Text("Settings Screen")
        }
    }
}

@Composable
fun MainScreen() {
    val (currentScreen, setCurrentScreen) = remember { mutableStateOf<Screen>(HomeScreen()) }
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Display the current screen content
        Navigator(currentScreen) { navigator ->
            navigator.lastItem.Content()
        }
        
        
        // Bottom navigation bar
        BottomNavigation(currentScreen, setCurrentScreen)
    }
}

@Composable
fun BottomNavigation(currentScreen: Screen, setCurrentScreen: (Screen) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        BottomNavigationItem(
            icon = Icons.Default.Home,
            title = "Home",
            selected = currentScreen is HomeScreen,
            onClick = { setCurrentScreen(HomeScreen()) }
        )
        BottomNavigationItem(
            icon = Icons.Default.Person,
            title = "Profile",
            selected = currentScreen is ProfileScreen,
            onClick = {
                
                println("adasdadada")
                
                setCurrentScreen(ProfileScreen())
            }
        )
        BottomNavigationItem(
            icon = Icons.Default.Settings,
            title = "Settings",
            selected = currentScreen is SettingsScreen,
            onClick = { setCurrentScreen(SettingsScreen()) }
        )
    }
}

@Composable
fun BottomNavigationItem(icon: ImageVector, title: String, selected: Boolean, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.size(200.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = title, modifier = Modifier.size(24.dp))
            Text(title)
        }
    }
}
