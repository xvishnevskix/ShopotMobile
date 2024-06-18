package org.videotrade.shopot.presentation.components.Chat

import Avatar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.koin.compose.koinInject
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.multiplatform.PermissionsProviderFactory
import org.videotrade.shopot.presentation.components.Common.BackIcon
import org.videotrade.shopot.presentation.screens.call.CallScreen
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatHeader(chat: ChatItem, viewModel: ChatViewModel) {
    val interactionSource =
        remember { MutableInteractionSource() }  // Создаем источник взаимодействия
    val navigator = LocalNavigator.currentOrThrow
    val scope = rememberCoroutineScope()
    val commonViewModel: CommonViewModel = koinInject()
    Row(
        modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(bottom = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        
        ) {
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            
            
            BackIcon(Modifier.padding(start = 23.dp, end = 8.dp).clickable {
                
                viewModel.clearMessages()
                viewModel.setCount(0)
                navigator.pop()
                
                
            })
        }
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            
            Avatar(
                icon = chat.icon,
                size = 40.dp
            )
            
            Text(
                text = listOfNotNull(chat.firstName, chat.lastName)
                    .joinToString(" ")
                    .takeIf { it.isNotBlank() }
                    ?.let {
                        if (it.length > 30) "${it.take(27)}..." else it
                    } ?: "",
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
                letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                lineHeight = 20.sp,
                modifier = Modifier.padding(start = 16.dp),
            )
        }
        
        
        Box {
            Icon(
                imageVector = Icons.Default.Call,
                contentDescription = "Call",
                modifier = Modifier.padding(end = 23.dp).size(20.dp).clickable {
                    
                    scope.launch {
                        try {
                            val cameraPer =
                                PermissionsProviderFactory.create().getPermission("microphone")
                            
                            if (!cameraPer) return@launch
                            
                            
                            
                            viewModel.sendNotify(
                                "Звонок",
                                "от ${chat.firstName} ${chat.lastName}",
                                chat.notificationToken
                            )
                            
                            navigator.push(
                                CallScreen(
                                    chat.userId,
                                    "Call",
                                    ProfileDTO(
                                        firstName = chat.firstName,
                                        lastName = chat.firstName,
                                        id = chat.userId,
                                        phone = chat.phone,
                                    )
                                )
                            )
                            
                            
                        } catch (e: Exception) {
                            println("ERROR : $e")
                            
                        }
                    }
                    println("userID : ${chat.userId}")
                    
                    
                }
            )
        }
        
    }
}