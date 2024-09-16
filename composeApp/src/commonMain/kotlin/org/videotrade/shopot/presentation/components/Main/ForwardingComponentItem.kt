package org.videotrade.shopot.presentation.components.Main

import Avatar
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.main.MainViewModel
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.chat_group

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForwardingComponentItem(
    chat: ChatItem,
    commonViewModel: CommonViewModel,
    mainViewModel: MainViewModel,
    chatViewModel: ChatViewModel,
    scaffoldState: BottomSheetScaffoldState,
) {
    val viewModel: ChatViewModel = koinInject()
    val scope = rememberCoroutineScope()
    
    Row(
        modifier = Modifier.padding(bottom = 12.dp).fillMaxWidth().clickable {
            mainViewModel.setCurrentChat(chat.id)
            mainViewModel.setZeroUnread(chat)
            viewModel.setCurrentChat(chat)
            chatViewModel.clearMessages()
            chatViewModel.setCount(0)
//            viewModel.sendMessage(
//                content = viewModel.forwardMessage.value?.content,
//                fromUser = viewModel.profile.value.id,
//                chatId = chat.id,
//                notificationToken = chat.notificationToken,
//                attachments = emptyList(),
//                login = "${viewModel.profile.value.firstName} ${viewModel.profile.value.lastName}",
//                true,
//                true
//            )
//            viewModel.getProfile()
            viewModel.getMessagesBack(chat.id)

            viewModel.sendForwardMessage(viewModel.forwardMessage.value?.id!!, chat.chatId)
            scope.launch {
                scaffoldState.bottomSheetState.partialExpand()
            }

            
//            commonViewModel.mainNavigator.value?.push(ChatScreen())
        },
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            
            Avatar(
                icon = chat.icon,
                size = 60.dp
            )
            
            Column(
                modifier = Modifier.padding(start = 10.dp),
                verticalArrangement = Arrangement.Center
            ) {
                val fullName =
                    listOfNotNull(if (chat.personal) chat.firstName + " " + chat.lastName else chat.groupName)
                        .joinToString(" ")
                        .takeIf { it.isNotBlank() }
                        ?.let {
                            if (it.length > 25) "${it.take(22)}..." else it
                        } ?: ""
                
                Row() {
                    if (chat.personal) {
                        val displayName = fullName.ifBlank { chat.phone!! }

                        Text(
                            text = displayName,
                            textAlign = TextAlign.Start,
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
                            letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                            lineHeight = 20.sp,
                            color = Color(0xFF000000)
                        )
                    } else {
                        Text(
                            text = fullName,
                            textAlign = TextAlign.Start,
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
                            letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                            lineHeight = 20.sp,
                            color = Color(0xFF000000)
                        )
                    }
                    if (!chat.personal) {
                        Spacer(modifier = Modifier.width(7.dp))
                        Image(
                            painter = painterResource(Res.drawable.chat_group),
                            contentDescription = "Avatar",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                
            }
            
        }
        
        
        
    }
    
}

