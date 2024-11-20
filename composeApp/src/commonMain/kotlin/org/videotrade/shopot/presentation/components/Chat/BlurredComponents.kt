package org.videotrade.shopot.presentation.components.Chat

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.api.formatTimeOnly
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.chat_forward
import shopot.composeapp.generated.resources.double_message_check
import shopot.composeapp.generated.resources.menu_copy
import shopot.composeapp.generated.resources.menu_delete
import shopot.composeapp.generated.resources.message_double_check
import shopot.composeapp.generated.resources.message_single_check

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlurredMessageOverlay(
    chat: ChatItem,
    profile: ProfileDTO,
    viewModel: ChatViewModel,
    selectedMessage: MessageItem?,
    selectedMessageY: Int,
    onDismiss: () -> Unit,

) {

    val messageSenderName = if (selectedMessage?.fromUser  == profile.id) {
        stringResource(MokoRes.strings.you)
    } else {
        selectedMessage?.phone?.let {
            val findContact = viewModel.findContactByPhone(it)
            if (findContact != null) {
                "${findContact.firstName} ${findContact.lastName}"
            } else {
                "+${selectedMessage.phone}"
            }
        } ?: ""
    }

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
                .clickable { onDismiss() }
            ,
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
                        chat = chat,
                        messageSenderName,
                        message = message,
                        profile = profile,
                        viewModel = viewModel,
                        onClick = {},
                        visible = visible,
                        onDismiss,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageBlurBox(
    chat: ChatItem,
    messageSenderName: String,
    message: MessageItem,
    profile: ProfileDTO,
    viewModel: ChatViewModel,
    onClick: () -> Unit,
    visible: Boolean,
    onDismiss: () -> Unit,

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

    val editOptions =  getChatEditOptions(chatId = chat.chatId, messageSenderName = messageSenderName)

    val density = LocalDensity.current


    var boxHeightDp by remember { mutableStateOf(0.dp) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { layoutCoordinates ->
                val heightInDp = with(density) { layoutCoordinates.size.height.toDp() }
                if (heightInDp != boxHeightDp) {
                    boxHeightDp = heightInDp
                    val heightInPx = with(density) { heightInDp.toPx().toInt() }
                    viewModel.updateBoxHeight(heightInPx)
                }
            }
    ) {

        println("boxHeightDp messageBlur ${boxHeightDp}")

        Column(
            modifier = Modifier
                .offset(x = firstColumnOffsetX)
                .fillMaxWidth(0.8f)
                ,
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
                    Surface(
                        modifier = Modifier.wrapContentSize(),
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomEnd = if (message.fromUser == profile.id) 0.dp else 16.dp,
                            bottomStart = if (message.fromUser == profile.id) 16.dp else 0.dp,
                        ),
                        color = if (message.attachments?.isNotEmpty() == true && message.attachments!![0].type == "sticker") {
                            Color.Transparent  // Прозрачный цвет для стикеров
                        } else {
                            if (message.fromUser == profile.id) Color(0xFFCAB7A3)  // Цвет для сообщений от текущего пользователя
                            else Color(0xFFF7F7F7)  // Цвет для сообщений от других пользователей
                        }
                    ) {
                        message.content?.let {
                            MessageFormat(message, profile, onClick)
                        }
                    }
                }



            Row(
                horizontalArrangement = if (message.fromUser == profile.id) Arrangement.End else Arrangement.Start,
                modifier = Modifier

                    .fillMaxWidth().clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null // Убирает эффект нажатия
                    ) {

                    }
            ) {

                if (message.created.isNotEmpty())
                    Text(
                        text = formatTimeOnly(message.created),
                        style = TextStyle(
                            fontSize = 16.sp,
                            lineHeight = 16.sp,
                            fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                            fontWeight = FontWeight(400),
                            color = Color(0x80373533),
                            letterSpacing = TextUnit(0F, TextUnitType.Sp),
                        ),
                        modifier = Modifier.padding(),
                    )


                if (message.fromUser == profile.id)
                    if (message.anotherRead) {
                        Image(
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .size(width = 17.7.dp, height = 8.5.dp),
                            painter = painterResource(Res.drawable.message_double_check),
                            contentDescription = null,
                        )
                    } else {
                        Image(
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .size(width = 12.7.dp, height = 8.5.dp),
                            painter = painterResource(Res.drawable.message_single_check),
                            contentDescription = null,
                        )
                    }



            }
        }


        //меню
        Column(
            modifier = Modifier
                .offset(y = secondColumnOffsetY)
                .padding(top = 4.dp)
                .fillMaxWidth(0.57f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
        ) {
            editOptions.forEachIndexed { index, editOption ->
                Column {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .border(width = 1.dp, color = Color(0x33373533), shape = RoundedCornerShape(size = 16.dp))
                            .width(197.dp)
                            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
                            .clickable {
                                editOption.onClick(viewModel, message, clipboardManager)
                                onDismiss()
                            }

                    ) {
                        Text(
                            text = editOption.text,
                            fontSize = 16.sp,
                            lineHeight = 16.sp,
                            fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                            fontWeight = FontWeight(400),
                            color = editOption.color,
                            letterSpacing = TextUnit(0F, TextUnitType.Sp),
                        )
                        Image(
                            painter = painterResource(editOption.imagePath),
                            contentDescription = null,
                            modifier = editOption.modifier.size(18.dp),
                            colorFilter = ColorFilter.tint(editOption.color)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}




