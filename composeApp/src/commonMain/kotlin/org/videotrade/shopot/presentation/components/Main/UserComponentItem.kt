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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.compose.stringResource
import dev.icerock.moko.resources.desc.StringDesc
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.api.formatTimestamp
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.presentation.screens.chat.ChatScreen
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.main.MainViewModel
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.chat_group
import shopot.composeapp.generated.resources.double_message_check
import shopot.composeapp.generated.resources.single_message_check

@Composable
fun UserComponentItem(
    chat: ChatItem,
    commonViewModel: CommonViewModel,
    mainViewModel: MainViewModel
) {
    val viewModel: ChatViewModel = koinInject()
    val profile = mainViewModel.profile.collectAsState().value

    Row(
        modifier = Modifier.padding(bottom = 12.dp).fillMaxWidth().clickable {
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



                Text(
                    text = chat.lastMessage?.let {
                        MessageContent(message = it)
                    }?.takeIf { it.isNotEmpty() }?.let {
                        if (it.length > 32) "${it.take(29)}..." else it
                    } ?: stringResource(MokoRes.strings.start_conversation),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                    letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                    lineHeight = 20.sp,
                    color = Color(0xFF979797),
                    modifier = Modifier.padding(top = 5.dp)
                )


            }

        }

        Row {
            Column(
                modifier = Modifier.padding(top = 12.dp, end = 5.dp)
            ) {
                if (chat.lastMessage?.fromUser == profile.id) {
                    if (chat.lastMessage?.anotherRead == true) {
                        Image(
                            modifier = Modifier.size(14.dp),
                            painter = painterResource(Res.drawable.double_message_check),
                            contentDescription = null,
                        )
                    } else {
                        Image(
                            modifier = Modifier.size(14.dp),
                            painter = painterResource(Res.drawable.single_message_check),
                            contentDescription = null,
                        )
                    }
                }

            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 9.dp)
            ) {
                if (chat.lastMessage !== null) {
                    Text(
                        formatTimestamp(chat.lastMessage!!.created),
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                        letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                        lineHeight = 20.sp,
                        color = Color(0xFF979797),

                        )
                }
//                Text(
//                    formatTimestamp(chat.sortedDate),
//                    textAlign = TextAlign.Center,
//                    fontSize = 14.sp,
//                    fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
//                    letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
//                    lineHeight = 20.sp,
//                    color = Color(0xFF979797),
//
//                    )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
//                        .background(if (boxText.isEmpty()) Color.Transparent else Color(0xFF2A293C))
                        .background(Color(0xFF2A293C))
                ) {
                    if (chat.unread !== 0) {
                        Text(
                            text = "${chat.unread}",
                            modifier = Modifier
                                .padding(start = 6.dp, end = 6.dp, top = 0.dp, bottom = 0.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 10.sp,
                            fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
                            letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                            lineHeight = 20.sp,
                            color = Color(0xFFFFFFFF),

                            )
                    }
                }
            }

        }


    }

}


@Composable
fun MessageContent(message: MessageItem): String {
    return if (message.attachments == null || message.attachments?.isEmpty() == true) {
        message.content ?: stringResource(MokoRes.strings.start_conversation)
    } else {

        when (message.attachments!![0].type) {
            "audio/mp4" -> stringResource(MokoRes.strings.audio)
            "image" -> stringResource(MokoRes.strings.photo)
            else -> stringResource(MokoRes.strings.file)
        }


    }
}
