package org.videotrade.shopot.presentation.screens.main

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import org.koin.compose.koinInject
import org.videotrade.shopot.presentation.components.Main.Drawer


class MainScreen : Screen {
    
    @Composable
    override fun Content() {
        val viewModel: MainViewModel = koinInject()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val navigator = LocalNavigator.current
        
        
        
        LaunchedEffect(Unit) {
            if (navigator != null) {
                viewModel.getNavigator(navigator)
                
                viewModel.getProfile()
                viewModel.loadUsers()
            }
        }
        Drawer(drawerState, viewModel)
    }
}


//        TabNavigator(BottomNavItem.CreateChat) { tabNavigator ->
//            Scaffold(
//                bottomBar = { BottomNavigationBar(tabNavigator) }
//            ) {
//                CurrentTab()
//            }
//        }


//        MainContentComponent(drawerState, viewModel)


//@Composable
//fun BottomNavBar(navigator: Navigator) {
//
//    val viewModel: MainViewModel = koinInject()
//    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
//
//    NavigationBar {
//        NavigationBarItem(
//            icon = { Icon(Icons.Default.Home, contentDescription = "Main") },
//            selected = navigator.lastItem is MainContentComponent,
//            onClick = {
//                navigator.push(MainContentComponent(drawerState, viewModel))
//            }
//        )
//        NavigationBarItem(
//            icon = { Icon(Icons.Default.Call, contentDescription = "Create Chat") },
//            selected = navigator.lastItem is CreateChatScreen,
//            onClick = {
//                navigator.replaceAll(CreateChatScreen())
//            }
//        )
//        NavigationBarItem(
//            icon = { Icon(Icons.Default.Settings, contentDescription = "Profile") },
//            selected = navigator.lastItem is ProfileScreen,
//            onClick = {
//                navigator.replaceAll(ProfileScreen())
//            }
//        )
//    }
//}


//sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) : Tab {
//
//    object CreateChat : BottomNavItem("create_chat", "Chat", Icons.Default.Call) {
//        @Composable
//        override fun Content() {
//            CreateChatScreen()
//        }
//
//        override val options: TabOptions
//            @Composable
//            get() = TabOptions(index = 1u, title = label)
//    }
//    object MainContent : BottomNavItem("main_content", "Main", Icons.Default.Call) {
//        @Composable
//        override fun Content() {
//            val viewModel: MainViewModel = koinInject()
//            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
//            MainContentComponent(drawerState, viewModel)
//        }
//
//        override val options: TabOptions
//            @Composable
//            get() = TabOptions(index = 0u, title = label)
//    }
//
//
//    object Profile : BottomNavItem("profile", "Profile", Icons.Default.Settings) {
//        @Composable
//        override fun Content() {
//            ProfileScreen()
//        }
//
//        override val options: TabOptions
//            @Composable
//            get() = TabOptions(index = 2u, title = label)
//    }
//}
//
//@Composable
//fun BottomNavigationBar(tabNavigator: TabNavigator) {
//    val items = listOf(
//        BottomNavItem.MainContent,
//        BottomNavItem.CreateChat,
//        BottomNavItem.Profile
//    )
//    NavigationBar {
//        items.forEach { item ->
//            NavigationBarItem(
//                icon = { Icon(item.icon, contentDescription = item.label) },
//                label = { Text(item.label) },
//                selected = item == tabNavigator.current,
//                onClick = { tabNavigator.current = item }
//            )
//        }
//    }
//}

