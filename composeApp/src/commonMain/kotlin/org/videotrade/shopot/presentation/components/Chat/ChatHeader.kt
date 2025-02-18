package org.videotrade.shopot.presentation.components.Chat

import Avatar
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.multiplatform.PermissionsProviderFactory
import org.videotrade.shopot.presentation.components.Call.CallBar
import org.videotrade.shopot.presentation.components.Common.BackIcon
import org.videotrade.shopot.presentation.components.Common.ReconnectionBar
import org.videotrade.shopot.presentation.components.Common.getParticipantCountText
import org.videotrade.shopot.presentation.screens.call.CallScreen
import org.videotrade.shopot.presentation.screens.call.CallViewModel
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.group.GroupProfileScreen
import org.videotrade.shopot.presentation.screens.profile.ProfileChatScreen
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.chat_call


@Composable
fun ChatHeader(chat: ChatItem, viewModel: ChatViewModel, profile: ProfileDTO) {
    val interactionSource =
        remember { MutableInteractionSource() }  // Создаем источник взаимодействия
    val navigator = LocalNavigator.currentOrThrow
    val scope = rememberCoroutineScope()
    val commonViewModel: CommonViewModel = koinInject()
    val callViewModel: CallViewModel = koinInject()
    val timer = callViewModel.timer.collectAsState()
    val groupUsers = viewModel.groupUsers.collectAsState().value
    val colors = MaterialTheme.colorScheme

    if (!chat.personal) {
        viewModel.loadGroupUsers(chat.chatId)
    }

    Column {
        Row(
            modifier = Modifier
                .padding(top = 10.dp)
                .padding(horizontal = 23.dp)
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(bottom = 10.dp)
                .background(colors.background),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            
            ) {
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
            ) {
                
                
                BackIcon(Modifier.pointerInput(Unit) {
                    
                    viewModel.clearMessages()
                    viewModel.setMessagePage(0)
                    navigator.pop()
                    
                    
                })
                
                Spacer(modifier = Modifier.width(21.dp))
                
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.padding(end = 5.dp).pointerInput(Unit) {
                        
                        if (chat.personal) {
                            navigator.push(ProfileChatScreen(chat))
                        } else {
                            
//                            viewModel.loadGroupUsers(chat.chatId)
                            navigator.push(GroupProfileScreen(profile, chat))
                            
                        }
                    }
                ) {
                    
                    Avatar(
                        icon = chat.icon,
                        size = 56.dp
                    )
                    
                    val fullName = if (chat.personal) {
                        if (chat.isSavedContact == false) "+${chat.phone}" else "${chat.firstName.orEmpty()} ${chat.lastName.orEmpty()}".trim()
                            .ifBlank { "+${chat.phone}" }
                    } else {
                        chat.groupName.orEmpty()
                    }.takeIf { it.isNotBlank() }
                        ?.let { if (it.length > 35) "${it.take(32)}..." else it }
                        ?: ""
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = fullName,
                            fontSize = 16.sp,
                            lineHeight = 16.sp,
//                                fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                            fontWeight = FontWeight(500),
                            color = colors.primary,
                            letterSpacing = TextUnit(0F, TextUnitType.Sp),
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (!chat.personal) {
                            ParticipantCountText(groupUsers.size)
                        }
                    }
                    
                    
                }
            }
            
            
            
            Box(
                modifier = Modifier
            ) {
                if (chat.personal)
                    
                    Image(
                        painter = painterResource(Res.drawable.chat_call),
                        contentDescription = null,
                        modifier = Modifier
                            .size(18.dp)
                            .pointerInput(Unit) {
                                
                                scope.launch {
                                    try {
                                        val cameraPer =
                                            PermissionsProviderFactory.create()
                                                .getPermission("microphone")
                                        
                                        if (!cameraPer) return@launch
//
//                                callViewModel.makeCallBackground(
//                                    chat.notificationToken!!,
//                                    chat.userId
//                                )


//                            commonViewModel.sendNotify(
//                                "Звонок",
//                                "от ${viewModel.profile.value.firstName} ${viewModel.profile.value.lastName}",
//                                chat.notificationToken
//                            )
//
                                        callViewModel.initWebrtc()
                                        callViewModel.setChatId(chat.chatId)
                                        callViewModel.setCalleeId(chat.userId)
                                        callViewModel.callScreenInfo.value = CallScreen(
                                            chat.userId,
                                            chat.icon,
                                            chat.firstName!!,
                                            chat.lastName!!,
                                            chat.phone!!,

                                        )
//
//                                    if (chat.firstName !== null && chat.lastName !== null && chat.phone !== null) {
//                                        println("aasdasdadadda ${chat.userId}  ${chat.firstName} ${chat.lastName} ${chat.userId} ${chat.phone} ${chat.icon}")
//                                        navigator.push(
//                                            CallScreen(
//                                                chat.userId,
//                                                "Call",
//                                                chat.icon,
//                                                chat.firstName!!,
//                                                chat.lastName!!,
//                                                chat.phone!!,
//                                            )
//                                        )
//                                    }
                                        
                                        if (chat.firstName !== null && chat.lastName !== null && chat.phone !== null) {
                                            commonViewModel.mainNavigator.value?.push(
                                                CallScreen(
                                                    chat.userId,
                                                    chat.icon,
                                                    chat.firstName!!,
                                                    chat.lastName!!,
                                                    chat.phone!!,
                                                    sendCall = true,

                                                )
                                            )
                                        }
//
                                    
                                    } catch (e: Exception) {
                                        println("ERROR : $e")
                                        
                                    }
                                }

                                
                                
                            },
                        colorFilter = ColorFilter.tint(colors.primary)
                    )
            }
            
        }
        ReconnectionBar()
        CallBar()
    }
}

@Composable
private fun ParticipantCountText(count: Int) {
    val colors = MaterialTheme.colorScheme

    Text(
        text = getParticipantCountText(count),
        fontSize = 16.sp,
        lineHeight = 16.sp,
        fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
        fontWeight = FontWeight(400),
        color = colors.secondary,
        letterSpacing = TextUnit(0F, TextUnitType.Sp),
    )
}

