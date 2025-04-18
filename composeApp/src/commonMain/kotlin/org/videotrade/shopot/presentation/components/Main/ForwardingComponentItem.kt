package org.videotrade.shopot.presentation.components.Main

import Avatar
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.chat_group
import shopot.composeapp.generated.resources.group

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
    val colors = MaterialTheme.colorScheme
    
    Row(
        modifier = Modifier.padding(bottom = 12.dp).fillMaxWidth().clickable {
            mainViewModel.setCurrentChat(chat.id)
            mainViewModel.setZeroUnread(chat)
            viewModel.setCurrentChat(chat)
            chatViewModel.clearMessages()
            chatViewModel.setMessagePage(0)
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
            verticalAlignment = Alignment.Top
        ) {
            
            Avatar(
                icon = chat.icon,
                size = 60.dp
            )
            
            Column(
                modifier = Modifier.padding(start = 10.dp),
                verticalArrangement = Arrangement.Top
            ) {
                val fullName =
                    listOfNotNull(if (chat.personal) chat.firstName + " " + chat.lastName else chat.groupName)
                        .joinToString(" ")
                        .takeIf { it.isNotBlank() }
                        ?.let {
                            if (it.length > 25) "${it.take(22)}..." else it
                        } ?: ""

                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    if (chat.personal) {
                        val displayName = fullName.ifBlank { chat.phone!! }

                        Text(
                            text = displayName,
                            textAlign = TextAlign.Start,
                            fontSize = 16.sp,
                            lineHeight = 16.sp,
                            fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                            fontWeight = FontWeight(500),
                            color = colors.primary,
                            letterSpacing = TextUnit(0F, TextUnitType.Sp),
                        )
                    } else {
                        Text(
                            text = fullName,
                            textAlign = TextAlign.Start,
                            fontSize = 16.sp,
                            lineHeight = 16.sp,
                            fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                            fontWeight = FontWeight(500),
                            color = colors.primary,
                            letterSpacing = TextUnit(0F, TextUnitType.Sp),
                        )
                    }
                    if (!chat.personal) {
                        Spacer(modifier = Modifier.width(7.dp))
                        Image(
                            painter = painterResource(Res.drawable.group),
                            contentDescription = "Avatar",
                            modifier = Modifier.size(width = 18.dp, height = 15.dp),
                            colorFilter =  ColorFilter.tint(colors.primary)

                        )
                    }
                }
                
            }
            
        }
        
        
        
    }
    
}

