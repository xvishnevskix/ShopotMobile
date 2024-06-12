package org.videotrade.shopot.presentation.screens.permissions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.jetbrains.compose.resources.Font
import org.koin.compose.koinInject
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.screens.common.ToasterViewModel
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Medium


class PermissionsScreen : Screen {
    
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val toasterViewModel: ToasterViewModel = koinInject()
        var isCheckContacts by remember { mutableStateOf("Нажмите чтобы проверить разрешение на контакты") }


//        LaunchedEffect(key1 = Unit) {
//
//
//
//
//                val contactsNative = PermissionsProviderFactory.create().getPermission("contacts")
//                val cameraNative = PermissionsProviderFactory.create().getPermission("camera")
//                val microPhoneNative =
//                    PermissionsProviderFactory.create().getPermission("microphone")
//
//                println("второй")
//
//                println("adasdadada $contactsNative $cameraNative $microPhoneNative")
//                if (!contactsNative || !cameraNative || !microPhoneNative) {
//                    toasterViewModel.toaster.show("Добавьте все разрешения")
//
//                    return@LaunchedEffect
//                }
//
//
//        }
        
        val items = listOf(
            PermissionItem(
                Icons.Default.Face,
                "Contacts",
                isCheckContacts,
                
                ) {
                println("sdadadada")
                isCheckContacts = "Готово"
            }
        )
        
        
        Box(modifier = Modifier.background(color = MaterialTheme.colorScheme.background)) {
            SafeArea {
                
                Column {
                    
                    
                    Column {
                        
                        Text(
                            "Включите разрешения",
                            fontSize = 28.sp,
                            fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
                            letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                            lineHeight = 20.sp,
                            modifier = Modifier.padding(bottom = 5.dp),
                        )
                        Text(
                            "Разрешения уведомлений и контактов позволяют вам узнавать, когда приходят сообщения, и помогают найти людей, которых вы знаете.\n" +
                                    "Контакты шифруются, так что сервис Signal не может их видеть,",
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                            fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Medium)),
                            letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                            lineHeight = 24.sp,
                            modifier = Modifier.padding(bottom = 5.dp),
                            fontWeight = FontWeight.W400,
                            color = Color(151, 151, 151)
                        )
                        
                        
                    }
                    
                    
                    items.forEach {
                        
                        PermissionItem(it.title, it.body, it.icon)
                    }
                    
                }
                
                
            }
        }
        
    }
    
    
}


@Composable
fun PermissionItem(title: String, body: String, icon: ImageVector) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.padding(end = 20.dp)
        )
        
        Column {
            Text(
                title,
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Medium)),
                letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                lineHeight = 24.sp,
                modifier = Modifier.padding(bottom = 5.dp),
                fontWeight = FontWeight.W400,
                color = Color(151, 151, 151)
            )
            Text(
                body,
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Medium)),
                letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                lineHeight = 24.sp,
                modifier = Modifier.padding(bottom = 5.dp),
                fontWeight = FontWeight.W400,
                color = Color(151, 151, 151)
            )
        }
        
        
    }
    
}


data class PermissionItem(
    val icon: ImageVector,
    val title: String,
    val body: String,
    val onClick: () -> Unit
)
