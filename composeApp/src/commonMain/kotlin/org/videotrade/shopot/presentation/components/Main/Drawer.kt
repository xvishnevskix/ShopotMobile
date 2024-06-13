package org.videotrade.shopot.presentation.components.Main

//import CustomVectorImage

import Avatar
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.jetbrains.compose.resources.Font
import org.videotrade.shopot.api.delValueInStorage
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.presentation.screens.login.SignInScreen
import org.videotrade.shopot.presentation.screens.main.MainViewModel
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.person

@Composable
fun Drawer(drawerState: DrawerState, viewModel: MainViewModel) {
    val navigator = LocalNavigator.currentOrThrow
    
    
    val profileState = viewModel.profile.collectAsState(
        initial = ProfileDTO(
            firstName = "Unknow",
            lastName = "Unknow",
        )
    ).value
    
    
    val items = listOf(
//        DrawerItem(
//            Icons.Default.Call,
//
//            "Контакты"
//        ) {},
//        DrawerItem(
//            Icons.Default.Settings,
//
//
//            "Настройки"
//
//        ) {},
        DrawerItem(
            Icons.AutoMirrored.Filled.ExitToApp,
            
            
            "Выход"
        
        ) {
            viewModel.leaveApp(navigator)
        }
    )
    
    
    
    
    
    
    
    MainContentComponent(drawerState, viewModel)
            
            
}


data class DrawerItem(
    
    val icon: ImageVector,
    
    val title: String,
    
    val onClick: () -> Unit
)


