package org.videotrade.shopot.presentation.components.Main

import Avatar
import CustomVectorImage
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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
import org.videotrade.shopot.domain.model.UserItem
import org.videotrade.shopot.presentation.screens.login.LoginScreen
import org.jetbrains.compose.resources.painterResource
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.drawer_contacts
import shopot.composeapp.generated.resources.person

import shopot.composeapp.generated.resources.randomUser

@Composable
fun Drawer(drawerState: DrawerState, chatState:  List<UserItem>) {
    val navigator = LocalNavigator.currentOrThrow

    val items = listOf(
        DrawerItem(
            Icons.Default.Call,

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

            Row(modifier = Modifier.padding(top = 40.dp, start = 30.dp, bottom = 60.dp)) {
                Avatar(
                    drawableRes = Res.drawable.randomUser,
                    size = 80.dp
                )


                Column(modifier = Modifier.padding(start = 16.dp, top = 16.dp)) {
                    Text(
                        "Антон Иванов",
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                        fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
                        letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                        lineHeight = 20.sp,
                        color = Color(0xFF000000)

                    )

                    Text(
                        "+ 7 (965) 568 - 15 - 98",

                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                        letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                        lineHeight = 20.sp,
                        color = Color(0xFF979797)

                    )
                }

            }

            items.forEach {
                NavigationDrawerItem(
                    label = {
                        Text(it.title,
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                            fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
                            letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                            lineHeight = 20.sp,
                            color = Color(0xFF000000))
                    },
                    selected = false,
                    icon = {
                        Icon(
                            imageVector =  it.icon,
                            contentDescription = it.title
                        )
                    },
                    onClick = it.onClick,
                    modifier = Modifier.padding(start = 15.dp)


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