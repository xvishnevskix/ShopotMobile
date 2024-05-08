package org.videotrade.shopot.presentation.components.Main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.videotrade.shopot.api.delValueInStorage
import org.videotrade.shopot.domain.model.UserItem
import org.videotrade.shopot.presentation.screens.login.LoginScreen
import org.jetbrains.compose.resources.painterResource
import shopot.composeapp.generated.resources.Res

import shopot.composeapp.generated.resources.randomUser

@Composable
fun Drawer(drawerState: DrawerState, chatState:  List<UserItem>) {
    val navigator = LocalNavigator.currentOrThrow

    val items = listOf(
        DrawerItem(
            Icons.Default.Contacts,

            "Контакты"
        ) {},
        DrawerItem(
            Icons.Default.Settings,


            "Настройки"

        ) {},
        DrawerItem(
            Icons.AutoMirrored.Filled.ExitToApp,


            "Выход"

        ) {
            leaveApp(navigator)

        }
    )




    ModalNavigationDrawer(drawerState = drawerState, drawerContent = {
        ModalDrawerSheet {

            Row(modifier = Modifier.padding(20.dp)) {
                Image(
                    modifier = Modifier.size(50.dp),
                    painter = painterResource(Res.drawable.randomUser),
                    contentDescription = null,

                    )


                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text(
                        "Антон Иванов",

                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold

                    )

                    Text(
                        "+ 7 (965) 568 - 15 - 98",

                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal

                    )
                }

            }

            items.forEach {
                NavigationDrawerItem(
                    label = {
                        Text(it.title)
                    },
                    selected = false,
                    icon = {
                        Icon(
                            imageVector = it.icon,
                            contentDescription = it.title
                        )
                    },
                    onClick = it.onClick


                )
            }
        }
    },
        content = {
            MainContentComponent(drawerState,chatState)



        })
}


data class DrawerItem(

    val icon: ImageVector,

    val title: String,

    val onClick: () -> Unit
)


fun leaveApp(navigator: Navigator) {

    delValueInStorage("token")
    delValueInStorage("refreshToken")

    navigator.push(LoginScreen())

}