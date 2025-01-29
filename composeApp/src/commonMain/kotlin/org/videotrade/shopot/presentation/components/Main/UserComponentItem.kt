package org.videotrade.shopot.presentation.components.Main

import Avatar
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.api.formatTimestamp
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.presentation.components.Chat.getCallStatusString
import org.videotrade.shopot.presentation.screens.chat.ChatScreen
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.main.MainViewModel
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.group
import shopot.composeapp.generated.resources.message_double_check
import shopot.composeapp.generated.resources.message_single_check

@Composable
fun UserComponentItem(
    chat: ChatItem,
    commonViewModel: CommonViewModel,
    mainViewModel: MainViewModel
) {
    val viewModel: ChatViewModel = koinInject()
    val profile = mainViewModel.profile.collectAsState().value
    val colors = MaterialTheme.colorScheme


    
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(2.dp))
            .background(colors.surface).fillMaxWidth().clickable {
                mainViewModel.setCurrentChat(chat.id)
                mainViewModel.setZeroUnread(chat)
                viewModel.clearMessages()
                viewModel.setCurrentChat(chat)
                commonViewModel.mainNavigator.value?.push(ChatScreen())
            },
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            
            Avatar(
                icon = chat.icon,
                size = 56.dp
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.Top
            ) {
                val fullName = if (chat.personal) {
                    if (chat.isSavedContact == false) "+${chat.phone}" else "${chat.firstName.orEmpty()} ${chat.lastName.orEmpty()}".trim()
                        .ifBlank { "+${chat.phone}" }
                } else {
                    chat.groupName.orEmpty()
                }
                
                Row() {
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
                            maxLines = 1, // Ограничиваем одной строкой
                            overflow = TextOverflow.Ellipsis, // Устанавливаем многоточие
                            modifier = Modifier.widthIn(max = 160.dp)
                        
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
                            maxLines = 1, // Ограничиваем одной строкой
                            overflow = TextOverflow.Ellipsis, // Устанавливаем многоточие
                            modifier = Modifier.widthIn(max = 160.dp)
                        
                        )
                    }
                    if (!chat.personal) {
                        Spacer(modifier = Modifier.width(7.dp))
                        Image(
                            painter = painterResource(Res.drawable.group),
                            contentDescription = "Avatar",
                            modifier = Modifier.size(width = 18.dp, height = 15.dp),
                            colorFilter = ColorFilter.tint(colors.primary)
                        )
                    }
                }
                println("${chat.lastMessage} chat.lastMessage?.fromUser")
                if (chat.lastMessage?.fromUser == profile.id) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(MokoRes.strings.you),
                        textAlign = TextAlign.Start,
                        fontSize = 16.sp,
                        lineHeight = 16.sp,
                        fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                        fontWeight = FontWeight(400),
                        color = colors.primary,
                        letterSpacing = TextUnit(0F, TextUnitType.Sp),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                Text(
                    text = chat.lastMessage?.let {
                        MessageContent(message = it)
                    } ?: stringResource(MokoRes.strings.start_conversation),
                    textAlign = TextAlign.Start,
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                    fontWeight = FontWeight(400),
                    color = colors.secondary,
                    letterSpacing = TextUnit(0F, TextUnitType.Sp),
                    maxLines = 1, // Ограничиваем одной строкой
                    overflow = TextOverflow.Ellipsis, // Устанавливаем многоточие
                    modifier = Modifier.widthIn(max = 200.dp),
                )
                
                
            }
            
        }
        
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxHeight()
        ) {
            
            if (chat.lastMessage !== null) {
                Text(
                    formatTimestamp(chat.lastMessage!!.created),
                    textAlign = TextAlign.End,
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                    fontWeight = FontWeight(400),
                    color = colors.secondary,
                    letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                    
                    )
            }
            Spacer(modifier = Modifier.height(12.dp))
            
            if (chat.lastMessage?.fromUser == profile.id) {
                Column(
                    modifier = Modifier
                ) {
                    if (chat.lastMessage?.fromUser == profile.id) {
                        if (chat.lastMessage?.anotherRead == true) {
                            Image(
                                painter = painterResource(Res.drawable.message_double_check),
                                contentDescription = null,
                                modifier = Modifier.size(width = 17.7.dp, height = 8.5.dp),
                            )
                        } else {
                            Image(
                                modifier = Modifier.size(width = 12.7.dp, height = 8.5.dp),
                                painter = painterResource(Res.drawable.message_single_check),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(colors.secondary)
                            )
                        }
                    }
                    
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier
                ) {
                    
                    
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
//                        .background(if (boxText.isEmpty()) Color.Transparent else Color(0xFF2A293C))
                            .background(Color(0xFFCAB7A3)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (chat.unread !== 0) {
                            Text(
                                text = "${chat.unread}",
                                modifier = Modifier
                                    .padding(start = 8.dp, end = 8.dp, top = 3.dp, bottom = 3.dp),
                                textAlign = TextAlign.Center,
                                fontSize = 16.sp,
                                lineHeight = 16.sp,
                                fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                                fontWeight = FontWeight(500),
                                color = Color(0xFFFFFFFF),
                                letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                
                                )
                        }
                    }
                }
            }
            
            
        }
        
        
    }
    
}


@Composable
fun MessageContent(message: MessageItem): String {
    return when {
        message.callInfo != null -> {
            getCallStatusString(message.callInfo!!.status)
        }
        message.attachments == null || message.attachments?.isEmpty() == true -> {
            message.content ?: stringResource(MokoRes.strings.start_conversation)
        }
        else -> {
            when (message.attachments!![0].type) {
                "audio/mp4" -> stringResource(MokoRes.strings.audio)
                "image" -> stringResource(MokoRes.strings.photo)
                "sticker" -> stringResource(MokoRes.strings.sticker)
                else -> stringResource(MokoRes.strings.file)
            }
        }
    }
}

