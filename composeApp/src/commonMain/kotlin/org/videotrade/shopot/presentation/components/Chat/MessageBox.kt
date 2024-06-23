package org.videotrade.shopot.presentation.components.Chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.api.formatTimestamp
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.double_message_check
import shopot.composeapp.generated.resources.single_message_check

@Composable
fun MessageBox(
    viewModel: ChatViewModel,
    message: MessageItem,
    profile: ProfileDTO,
    onClick: () -> Unit,
    onPositioned: (LayoutCoordinates) -> Unit,
    isVisible: Boolean
) {
    val isReadByMe = remember { mutableStateOf(false) }
    
    LaunchedEffect(viewModel.messages.value) {
        if (message.fromUser == profile.id) {
            if (message.anotherRead) {
                isReadByMe.value = true
            }
        } else {
            if (!message.iread) {
                viewModel.sendReadMessage(message.id)
            }
        }
    }
    
    Column(
        modifier = Modifier
            .onGloballyPositioned(onPositioned)
            .alpha(if (isVisible) 1f else 0f) // Manage visibility with alpha
    ) {
        Box(
            contentAlignment = if (message.fromUser == profile.id) Alignment.CenterEnd else Alignment.CenterStart,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = { onClick() }
                    )
                }
        ) {
            Surface(
                modifier = Modifier.wrapContentSize().widthIn(max = 340.dp),
                shape = RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp,
                    bottomEnd = if (message.fromUser == profile.id) 0.dp else 20.dp,
                    bottomStart = if (message.fromUser == profile.id) 20.dp else 0.dp,
                ),
                shadowElevation = 4.dp,
                color = if (message.fromUser == profile.id) Color(0xFF2A293C) else Color(0xFFF3F4F6)
            ) {
                MessageFormat(message, profile)
            }
        }
        
        Row(
            horizontalArrangement = if (message.fromUser == profile.id) Arrangement.End else Arrangement.Start,
            modifier = Modifier
                .padding(start = 2.dp, end = 2.dp)
                .fillMaxWidth()
        ) {
            if (message.fromUser == profile.id)
                if (message.anotherRead) {
                    Image(
                        modifier = Modifier
                            .padding(top = 2.dp, end = 4.dp)
                            .size(14.dp),
                        painter = painterResource(Res.drawable.double_message_check),
                        contentDescription = null,
                    )
                } else {
                    Image(
                        modifier = Modifier
                            .padding(top = 2.dp, end = 4.dp)
                            .size(14.dp),
                        painter = painterResource(Res.drawable.single_message_check),
                        contentDescription = null,
                    )
                }
            
            Text(
                text = formatTimestamp(message.created),
                style = TextStyle(
                    color = Color.Gray,
                    fontSize = 16.sp
                ),
                modifier = Modifier.padding(),
            )
        }
    }
}
