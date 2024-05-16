package org.videotrade.shopot.presentation.components.Chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.touchlab.kermit.Logger
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.domain.model.UserItem
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.double_message_check

@Composable
fun Chat(
    chat: UserItem,
    viewModel: ChatViewModel,
) {
    val messagesState = viewModel.messages.collectAsState(initial = listOf()).value
    val profile = viewModel.profile.collectAsState(initial = ProfileDTO()).value
    val listState = rememberLazyListState()
    
    Logger.d("profile.id ${profile?.id}")
    
    LazyColumn(
        state = listState,
        reverseLayout = true, // Makes items start from the bottom
        modifier = Modifier
            .fillMaxSize()
//            .navigationBarsPadding()
    ) {
        itemsIndexed(messagesState) { index, message ->
            if (profile != null) {
                MessageBox(profile.id, message)
                
            }
        }
    }
}

@Composable
fun MessageBox(profileId: String, message: MessageItem) {
    Column {
        Box(
            contentAlignment = if (profileId == message.fromUser) Alignment.CenterEnd else Alignment.CenterStart,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            if (profileId == message.fromUser) {
                Surface(
                    modifier = Modifier.wrapContentSize(),
                    shape = RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomEnd = 0.dp,
                        bottomStart = 20.dp
                    ),
                    shadowElevation = 4.dp,
                    color = Color(0xFF2A293C)
                ) {
                    Text(
                        text = message.content,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(
                            start = 25.dp,
                            end = 25.dp,
                            top = 13.dp,
                            bottom = 12.dp
                        ),
                        textAlign = TextAlign.Start,
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                        letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                        lineHeight = 20.sp,
                        color = Color(0xFFFFFFFF),
                    )
                }
            } else {
                Surface(
                    modifier = Modifier.wrapContentSize(),
                    shape = RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomEnd = 20.dp,
                        bottomStart = 0.dp
                    ),
                    shadowElevation = 4.dp,
                    color = Color(0xFFF3F4F6)
                ) {
                    Text(
                        text = message.content,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(
                            start = 25.dp,
                            end = 25.dp,
                            top = 13.dp,
                            bottom = 12.dp
                        ),
                        textAlign = TextAlign.Start,
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                        letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                        lineHeight = 20.sp,
                        color = Color(0xFF29303C),
                    )
                }
            }
        }
        
        Row(
            horizontalArrangement = if (profileId == message.fromUser) Arrangement.End else Arrangement.Start,
            modifier = Modifier
                .padding(start = 2.dp, end = 2.dp)
                .fillMaxWidth()
        ) {
            Image(
                modifier = Modifier
                    .padding(top = 2.dp, end = 4.dp)
                    .size(14.dp),
                painter = painterResource(Res.drawable.double_message_check),
                contentDescription = null,
            )
            Text(
                text = "11:17",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(),
                textAlign = TextAlign.End,
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                lineHeight = 20.sp,
                color = Color(0xFF979797),
            )
        }
    }
}
