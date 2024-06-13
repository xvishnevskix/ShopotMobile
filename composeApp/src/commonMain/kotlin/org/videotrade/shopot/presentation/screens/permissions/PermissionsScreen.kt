package org.videotrade.shopot.presentation.screens.permissions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.koin.compose.koinInject
import org.videotrade.shopot.multiplatform.PermissionsProviderFactory
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.intro.IntroScreen
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Medium

class PermissionsScreen : Screen {
    
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val toasterViewModel: CommonViewModel = koinInject()
        
        val scope = rememberCoroutineScope()
        
        
        val items = listOf(
            PermissionItemDTO(
                "Контакты",
            ),
            PermissionItemDTO(
                "Уведомления",
            )
        )
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
            ) {
                Text(
                    text = "Включите разрешения",
                    fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Medium)),
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Разрешения уведомлений и контактов позволяют вам узнавать, когда приходят сообщения, и помогают найти людей, которых вы знаете.",
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Medium)),
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.CenterHorizontally) // Центрирование текста
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                LazyColumn {
                    itemsIndexed(items) { _, it ->
                        NotificationPreview(it.title)
                    }
                }
                Spacer(modifier = Modifier.height(102.dp))
                
                Button(
                    onClick = {
                        scope.launch {
                            val contactsNative =
                                PermissionsProviderFactory.create().getPermission("contacts")
                            
                            
                            if (contactsNative) {
                                
                            }
                            
                            
                            val permissionsNative =
                                PermissionsProviderFactory.create().getPermission("notifications")
                            
                            
                            if (permissionsNative) {
                                
                            }
                            
                            navigator.replace(IntroScreen())
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Включите разрешения",
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationPreview(title: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp)) // Округление углов элемента
            .background(Color(0xFF333333))
            .padding(16.dp)
    
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            color = Color.White
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
    }
    
    Spacer(modifier = Modifier.height(8.dp))
}

data class PermissionItemDTO(
    val title: String,
)
