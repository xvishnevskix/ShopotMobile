package org.videotrade.shopot.presentation.components.Chat

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.api.formatTimestamp
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.double_message_check
import shopot.composeapp.generated.resources.edit_pencil
import shopot.composeapp.generated.resources.single_message_check


//@Composable
//fun Chat(
//    viewModel: ChatViewModel, modifier: Modifier
//) {
//    val messagesState = viewModel.messages.collectAsState(initial = listOf()).value
//
//    val listState = rememberLazyListState()
//
////    LaunchedEffect(messagesState.size) {
////        if (messagesState.isNotEmpty()) {
////            listState.animateScrollToItem(messagesState.lastIndex)
////        }
////    }
//
//    LazyColumn(
//        state = listState,
//        reverseLayout = true, // Makes items start from the bottom
//        modifier = modifier
//    ) {
//        itemsIndexed(messagesState) { index, message ->
//            MessageBox(message)
//        }
//    }
//
//
//}


//@Composable
//fun MessageBox(message: MessageItem) {
//
//
//
//
//    Column {
//        Box(
////        contentAlignment = if (true) Alignment.CenterStart else Alignment.CenterEnd,
//            contentAlignment = if (message.fromUser == profile.id) Alignment.CenterEnd else Alignment.CenterStart,
//            modifier = Modifier
//                .padding(start = 2.dp ,end = 2.dp)
//                .fillMaxWidth()
//                .padding(vertical = 4.dp,)
//        ) {
//
//            if (message.fromUser == profile.id) {
//                Surface(
//                    modifier = Modifier
//                        .wrapContentSize(),
//                    shape = RoundedCornerShape(
//                        topStart = 20.dp,
//                        topEnd = 20.dp,
//                        bottomEnd = 0.dp,
//                        bottomStart = 20.dp
//                    ),
//                    shadowElevation = 4.dp,
//                    color = Color(0xFF2A293C)
//                ) {
//                    Text(
//                        text = message.content,
//                        style = MaterialTheme.typography.bodyLarge,
//                        modifier = Modifier.padding(start = 25.dp, end = 25.dp, top = 13.dp, bottom = 12.dp),
//                        textAlign = TextAlign.Start,
//                        fontSize = 16.sp,
//                        fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
//                        letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
//                        lineHeight = 20.sp,
//                        color = Color(0xFFFFFFFF),
//                    )
//
//                }
//            } else {
//                Surface(
//                    modifier = Modifier
//                        .wrapContentSize(),
//                    shape = RoundedCornerShape(
//                        topStart = 20.dp,
//                        topEnd = 20.dp,
//                        bottomEnd = 20.dp,
//                        bottomStart = 0.dp
//                    ),
//                    shadowElevation = 4.dp,
//                    color = Color(0xFFF3F4F6)
//                ) {
//                    Text(
//                        text = message.content,
//                        style = MaterialTheme.typography.bodyLarge,
//                        modifier = Modifier.padding(start = 25.dp, end = 25.dp, top = 13.dp, bottom = 12.dp),
//                        textAlign = TextAlign.Start,
//                        fontSize = 16.sp,
//                        fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
//                        letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
//                        lineHeight = 20.sp,
//                        color = Color(0xFF29303C),
//                    )
//
//                }
//            }
//        }
//
//        Row(
//            horizontalArrangement = if (message.fromUser == profile.id) Arrangement.End else Arrangement.Start,
//            modifier = Modifier
//                .padding(start = 2.dp ,end = 2.dp)
//                .fillMaxWidth()
//        ) {
//            Image(
//                modifier = Modifier.padding(top = 2.dp, end = 4.dp).size(14.dp),
//                painter = painterResource(Res.drawable.double_message_check),
//                contentDescription = null,
//            )
////                Image(
////                    modifier = Modifier.size(14.dp),
////                    painter = painterResource(Res.drawable.single_message_check),
////                    contentDescription = null,
////                )
//            Text(
//                text = "11:17",
//                style = MaterialTheme.typography.bodyLarge,
//                modifier = Modifier.padding(),
//                textAlign = TextAlign.End,
//                fontSize = 16.sp,
//                fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
//                letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
//                lineHeight = 20.sp,
//                color = Color(0xFF979797),
//            )
//        }
//    }
//}

data class EditOption(
    val text: String,
    val imagePath: DrawableResource,
    val onClick: (viewModule: ChatViewModel, message: MessageItem, clipboardManager: ClipboardManager) -> Unit,
    
    )


val editOptions = listOf(
//    EditOption(
//        text = "Переслать",
//        imagePath = Res.drawable.edit_pencil,
//        onClick = {
//
//
//        }
//    ),
//    EditOption(
//        text = "Изменить",
//        imagePath = Res.drawable.edit_pencil,
//        onClick = {}
//    ),
    EditOption(
        text = "Удалить",
        imagePath = Res.drawable.edit_pencil,
        onClick = { viewModule, message, _ ->
            viewModule.deleteMessage(message)
        }
    ),
    EditOption(
        text = "Копировать",
        imagePath = Res.drawable.edit_pencil,
        onClick = { _, message, clipboardManager ->
            
            
            clipboardManager.setText(AnnotatedString(message.content))
            
            
        }
    ),
    
    )

@Composable
fun Chat(
    viewModel: ChatViewModel,
    profile: ProfileDTO,
    modifier: Modifier,
    onMessageClick: (MessageItem, Int) -> Unit,
    hiddenMessageId: String?
) {
    val messagesState = viewModel.messages.collectAsState(initial = listOf()).value
    val listState = rememberLazyListState()
    
    LazyColumn(
        state = listState,
        reverseLayout = true,
        modifier = modifier,
    ) {
        itemsIndexed(messagesState) { _, message ->
            
            var messageY by remember { mutableStateOf(0) }
            val isVisible = message.id != hiddenMessageId
            MessageBox(
                viewModel = viewModel,
                message = message,
                profile = profile,
                onClick = { onMessageClick(message, messageY) },
                onPositioned = { coordinates ->
                    messageY = coordinates.positionInParent().y.toInt()
                },
                isVisible = isVisible
            )
        }
    }
}

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
    val isSendRead = remember { mutableStateOf(false) }
    
    
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
                .padding(start = 2.dp, end = 2.dp)
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = { onClick() }
                    )
                }
        ) {
            if (message.fromUser == profile.id) {
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
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 16.sp
                        ),
                        modifier = Modifier.padding(
                            start = 25.dp,
                            end = 25.dp,
                            top = 13.dp,
                            bottom = 12.dp
                        ),
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
                        style = TextStyle(
                            color = Color.Black,
                            fontSize = 16.sp
                        ),
                        modifier = Modifier.padding(
                            start = 25.dp,
                            end = 25.dp,
                            top = 13.dp,
                            bottom = 12.dp
                        ),
                    )
                }
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

@Composable
fun BlurredMessageOverlay(
    profile: ProfileDTO,
    viewModel: ChatViewModel,
    selectedMessage: MessageItem?,
    selectedMessageY: Int,
    onDismiss: () -> Unit
) {
    selectedMessage?.let { message ->
        var visible by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            visible = true
        }
        
        val alpha by animateFloatAsState(
            targetValue = if (visible) 0.5f else 0f,
            animationSpec = tween(durationMillis = 200)
        )
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = alpha))
                .clickable { onDismiss() },
        ) {
            Box(
                modifier = Modifier
                    .offset(y = with(LocalDensity.current) { selectedMessageY.toDp() })
                    .padding(16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.Transparent
                ) {
                    MessageBlurBox(
                        message = message,
                        profile = profile,
                        viewModel = viewModel,
                        onClick = {},
                        visible = visible
                    )
                }
            }
        }
    }
}

@Composable
fun MessageBlurBox(
    message: MessageItem,
    profile: ProfileDTO,
    viewModel: ChatViewModel,
    onClick: () -> Unit,
    visible: Boolean
) {
    val clipboardManager = LocalClipboardManager.current
    
    val transition = updateTransition(targetState = visible, label = "MessageBlurBoxTransition")
    val orientation: Dp = if (message.fromUser == profile.id) 100.dp else -75.dp
    val firstColumnOffsetX by transition.animateDp(
        transitionSpec = { tween(durationMillis = 300, easing = FastOutSlowInEasing) },
        label = "FirstColumnOffsetX"
    ) { state ->
        if (state) 0.dp else orientation
    }
    
    val secondColumnOffsetY by transition.animateDp(
        transitionSpec = { tween(durationMillis = 300, easing = FastOutSlowInEasing) },
        label = "SecondColumnOffsetY"
    ) { state ->
        if (state) 0.dp else 200.dp
    }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .offset(x = firstColumnOffsetX)
                .fillMaxWidth(0.5f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = if (message.fromUser == profile.id) Alignment.CenterEnd else Alignment.CenterStart,
                modifier = Modifier
                    .padding(start = 2.dp, end = 2.dp)
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable(onClick = onClick)
            ) {
                if (message.fromUser == profile.id) {
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
                            style = TextStyle(
                                color = Color.White,
                                fontSize = 16.sp
                            ),
                            modifier = Modifier.padding(
                                start = 25.dp,
                                end = 25.dp,
                                top = 13.dp,
                                bottom = 12.dp
                            ),
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
                            style = TextStyle(
                                color = Color.Black,
                                fontSize = 16.sp
                            ),
                            modifier = Modifier.padding(
                                start = 25.dp,
                                end = 25.dp,
                                top = 13.dp,
                                bottom = 12.dp
                            ),
                        )
                    }
                }
            }
            Row(
                horizontalArrangement = if (message.fromUser == profile.id) Arrangement.End else Arrangement.Start,
                modifier = Modifier
                    .padding(start = 2.dp, end = 2.dp)
                    .fillMaxWidth()
            ) {
                Image(
                    modifier = Modifier
                        .padding(
                            start = if (message.fromUser == profile.id) 70.dp else 0.dp,
                            top = 2.dp,
                            end = 4.dp
                        )
                        .size(14.dp),
                    painter = painterResource(Res.drawable.double_message_check),
                    contentDescription = null,
                )
            }
        }
        
        Column(
            modifier = Modifier
                .offset(y = secondColumnOffsetY)
                .padding(top = 4.dp)
                .fillMaxWidth(0.5f)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
        ) {
            editOptions.forEachIndexed { index, editOption ->
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp)
                        .fillMaxWidth().clickable {
                            
                            
                            editOption.onClick(viewModel, message, clipboardManager)
                        }
                ) {
                    Text(
                        text = editOption.text,
                        textAlign = TextAlign.Center,
                        fontSize = 13.sp,
                        fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                        letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                        lineHeight = 20.sp,
                        color = Color(0xFF000000)
                    )
                    Image(
                        painter = painterResource(editOption.imagePath),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
                if (index < editOptions.size - 1) {
                    Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
                } else {
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}