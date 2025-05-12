package org.videotrade.shopot.presentation.components.Main

import Avatar
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import org.videotrade.shopot.domain.model.GroupUserDTO
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.presentation.components.Chat.ChatStatus
import org.videotrade.shopot.presentation.components.Chat.getCallStatusString
import org.videotrade.shopot.presentation.components.Common.ModalDialogWithText
import org.videotrade.shopot.presentation.components.Common.ModalDialogWithoutText
import org.videotrade.shopot.presentation.components.Common.SwipeContainer
import org.videotrade.shopot.presentation.screens.chat.ChatScreen
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.group.GroupViewModel
import org.videotrade.shopot.presentation.screens.main.MainViewModel
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.group
import shopot.composeapp.generated.resources.message_double_check
import shopot.composeapp.generated.resources.message_single_check

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun UserComponentItem(
    chat: ChatItem,
    commonViewModel: CommonViewModel,
    mainViewModel: MainViewModel,
    groupUsers: List<GroupUserDTO>
) {
    val chatViewModel: ChatViewModel = koinInject()
    val profile = mainViewModel.profile.collectAsState().value
    val colors = MaterialTheme.colorScheme
    val status = chatViewModel.userStatuses.collectAsState().value[chat.userId]
    val firstDeleteModalVisible = remember { mutableStateOf(false) }
    val secondDeleteModalVisible = remember { mutableStateOf(false) }
    val showLeaveModal = remember { mutableStateOf(false) }
    val groupViewModel: GroupViewModel = koinInject()



    SwipeContainer(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        isVisible = true,
        isPersonal = chat.personal,
        onSwipeDelete = {
            if (chat.personal) {
                firstDeleteModalVisible.value = true
            } else {
                showLeaveModal.value = true
            }
        }
    ) {
        Row(
            modifier = Modifier
                //потом убрать для удаления
//                        .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(colors.surface).fillMaxWidth().clickable {
                    mainViewModel.setCurrentChat(chat.id)
                    mainViewModel.setZeroUnread(chat)
                    chatViewModel.clearMessages()
                    chatViewModel.setCurrentChat(chat)
                    commonViewModel.mainNavigator.value?.push(ChatScreen())
                },
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.Top
            ) {
                
                if (chat.personal) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Avatar(
                            icon = chat.icon,
                            size = 56.dp
                        )
                        androidx.compose.animation.AnimatedVisibility(
                            visible = status != "OFFLINE" && status != null,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .size(12.dp)
                                        .border(
                                            width = 2.dp,
                                            color = Color.White,
                                            shape = RoundedCornerShape(6.dp)
                                        )
                                        .background(
                                            color = Color(0xFF5AE558),
                                            shape = RoundedCornerShape(6.dp)
                                        )
                                )
                            }
                        }
                    }
                } else {
                    GroupAvatar(users = groupUsers)
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(
                    modifier = Modifier,
                    verticalArrangement = Arrangement.Top
                ) {

                    val displayName = if (chat.personal) {
                        val firstName = chat.firstName.orEmpty()
                        val lastName = chat.lastName.orEmpty()
                        val name = "$firstName $lastName".trim()
                        
                        when {
                            firstName.equals(
                                "Unknown",
                                ignoreCase = true
                            ) && lastName.isBlank() -> stringResource(MokoRes.strings.deleted_user)

                            name.isNotBlank() -> name
                            !chat.phone.isNullOrBlank() -> "+${chat.phone}"
                            else -> stringResource(MokoRes.strings.deleted_user)
                        }
                    } else {
                        chat.groupName?.takeIf { it.isNotBlank() } ?: "Deleted group"
                    }
                    
                    Row {
                        if (chat.personal) {
                            
                            
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
                    if (chat.lastMessage?.fromUser == profile.id) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(MokoRes.strings.you),
                            textAlign = TextAlign.Start,
                            fontSize = 14.sp,
                            lineHeight = 14.sp,
                            fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                            fontWeight = FontWeight(400),
                            color = colors.primary,
                            letterSpacing = TextUnit(0F, TextUnitType.Sp),
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    } else {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
                    AnimatedContent(
                        targetState = status == null || status == "OFFLINE" || status == "ONLINE" || !chat.personal,
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        },
                        label = "ChatStatusTransition"
                    ) { showMessage ->
                        if (showMessage) {
                            Text(
                                text = chat.lastMessage?.let {
                                    MessageContent(message = it)
                                } ?: stringResource(MokoRes.strings.start_conversation),
                                textAlign = TextAlign.Start,
                                fontSize = 14.sp,
                                lineHeight = 14.sp,
                                fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                fontWeight = FontWeight(400),
                                color = colors.secondary,
                                letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.widthIn(max = 200.dp),
                            )
                        } else {
                            chat.userId?.let { ChatStatus(it, chatViewModel) }
                        }
                    }
                    
                    
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
                        fontSize = 14.sp,
                        lineHeight = 14.sp,
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
                                        .padding(
                                            start = 8.dp,
                                            end = 8.dp,
                                            top = 3.dp,
                                            bottom = 3.dp
                                        ),
                                    textAlign = TextAlign.Center,
                                    fontSize = 14.sp,
                                    lineHeight = 14.sp,
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
    if (firstDeleteModalVisible.value) {
        ModalDialogWithoutText(
            onDismiss = { firstDeleteModalVisible.value = false },
            onConfirm = {
                firstDeleteModalVisible.value = false
                secondDeleteModalVisible.value = true
            },
            confirmText = stringResource(MokoRes.strings.delete),
            dismissText = stringResource(MokoRes.strings.cancel),
            title = "${stringResource(MokoRes.strings.delete_chat_with)} ${chat.firstName + " " + chat.lastName}?"
        )
    }
    if (secondDeleteModalVisible.value) {
        ModalDialogWithText(
            onDismiss = {
                secondDeleteModalVisible.value = false
            },
            onConfirm = {
                //                        onDelete()
                chatViewModel.deleteChat(chat)
                
                secondDeleteModalVisible.value = false
            },
            confirmText = stringResource(MokoRes.strings.delete),
            dismissText = stringResource(MokoRes.strings.cancel),
            title = stringResource(MokoRes.strings.attention),
            text = stringResource(MokoRes.strings.this_action_will_permanently_delete_all_messages_in_this_chat)
        )
    }
    if (showLeaveModal.value) {
        ModalDialogWithoutText(
            onDismiss = { showLeaveModal.value = false },
            onConfirm = {
                groupViewModel.leaveGroupChat(chatId = chat.chatId)
                showLeaveModal.value = false
            },
            confirmText = stringResource(MokoRes.strings.leave_group),
            dismissText = stringResource(MokoRes.strings.cancel),
            title = "${stringResource(MokoRes.strings.do_you_really_want_to_leave_the_group)} ${chat.groupName}?"
        )
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


