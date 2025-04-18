package org.videotrade.shopot.presentation.components.ProfileComponents


import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.model.GroupUserRole
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.presentation.components.Call.CallBar
import org.videotrade.shopot.presentation.components.Common.BackIcon
import org.videotrade.shopot.presentation.components.Common.ModalDialogWithoutText
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import org.videotrade.shopot.presentation.screens.group.GroupEditScreen
import org.videotrade.shopot.presentation.screens.group.GroupViewModel
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.delete_account
import shopot.composeapp.generated.resources.edit_pencil
import shopot.composeapp.generated.resources.menu_delete
import shopot.composeapp.generated.resources.profile_exit
import shopot.composeapp.generated.resources.setting_dots


@Composable
fun GroupProfileHeader(
    text: String,
    profile: ProfileDTO,
    chat: ChatItem,
    isEdit: Boolean,
) {
    val navigator = LocalNavigator.currentOrThrow
    val colors = MaterialTheme.colorScheme
    val groupViewModel: GroupViewModel = koinInject()
    val groupUserRole = groupViewModel.groupUserRole.collectAsState().value
    val isMember = groupUserRole === GroupUserRole.MEMBER
    val showLeaveModal = remember { mutableStateOf(false) }
    val showEditPopup = remember { mutableStateOf(false) }

    val popupOptions = listOf(
        GroupEditOption.edit.toPopupDTO(
            text = stringResource(MokoRes.strings.edit),
            onClick = { navigator.push(GroupEditScreen(profile, chat)) },
            imagePath = Res.drawable.edit_pencil,
            modifier = Modifier,
            color = colors.primary
        ),
        GroupEditOption.remove.toPopupDTO(
            text = stringResource(MokoRes.strings.leave_group)
            ,
            onClick = { },
            imagePath = Res.drawable.profile_exit,
            modifier = Modifier,
            color = Color.Red
        )
    )



    Column {

        EditOptionsPopup(
            isVisible = showEditPopup.value,
            popupDTO = popupOptions,
            onDismissRequest = { showEditPopup.value = false }
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 40.dp).background(colors.background),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            
            ) {
            Box(
                modifier = Modifier.clip(CircleShape).padding(12.dp).clickable {
                    navigator.pop()
                },
            ) {
                BackIcon()
            }
            Text(
                text,
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                lineHeight = 16.sp,
                fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                fontWeight = FontWeight(500),
                color = colors.primary,
                letterSpacing = TextUnit(0F, TextUnitType.Sp),
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

//                Box(
//                    modifier = Modifier.clip(CircleShape).padding(12.dp).clickable {
//                        navigator.push(GroupEditScreen(profile, chat))
//                    })
//                {
//                    Image(
//                        modifier = Modifier,
//                        painter = painterResource(Res.drawable.profile_exit),
//                        contentDescription = null,
//                        contentScale = ContentScale.Crop,
//                        colorFilter = ColorFilter.tint(Color.Red)
//                    )
//                }
//
//                Spacer(modifier = Modifier.width(6.dp))

                if (!isMember && isEdit) {
                    Box(
                        modifier = Modifier.clip(CircleShape).padding(12.dp).clickable {
//                            navigator.push(GroupEditScreen(profile, chat))
                            showEditPopup.value = true
                        })
                    {
                        Image(
                            modifier = Modifier,
                            painter = painterResource(Res.drawable.setting_dots),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            colorFilter = ColorFilter.tint(colors.primary)
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier.clip(CircleShape).padding(12.dp).clickable {
                            showLeaveModal.value = true
                        })
                    {
                        Image(
                            modifier = Modifier,
                            painter = painterResource(Res.drawable.profile_exit),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            colorFilter = ColorFilter.tint(Color.Red)
                        )
                    }
                }
            }
            
        }
        Box(Modifier.padding(bottom = 23.dp)) {
            CallBar()
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
    
}


data class PopupOption(
    val text: String,
    val onClick: () -> Unit,
    val imagePath: DrawableResource,
    val modifier: Modifier = Modifier,
    val color: Color = Color(0xFF373533),
)
enum class GroupEditOption() {
    edit,
    remove;

    fun toPopupDTO(text: String, onClick: () -> Unit, imagePath: DrawableResource, modifier: Modifier, color: Color): PopupOption = when (this) {
        edit -> PopupOption(text,onClick,imagePath, modifier, color)
        remove -> PopupOption(text, onClick, imagePath, modifier, color)
    }
}

@Composable
fun EditOptionsPopup(
    isVisible: Boolean,
    popupDTO: List<PopupOption>,
    onDismissRequest: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme

    if (isVisible) {
        Popup(
            alignment = Alignment.TopEnd,
            offset = IntOffset(20, 200), // настроить под якорь
            onDismissRequest = onDismissRequest
        ) {
            Crossfade(targetState = isVisible, label = "EditPopup") { visible ->
                if (visible) {
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(colors.background)
                            .border(
                                width = 1.dp,
                                color = colors.onSecondary,
                                shape = RoundedCornerShape(size = 16.dp)
                            )
                            .shadow(1.dp)
                            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
                    ) {
                        popupDTO.forEach { item ->
                            Column {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .border(
                                            width = 1.dp,
                                            color = colors.onSecondary,
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .width(197.dp)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            item.onClick()
                                            onDismissRequest()
                                        }
                                        .padding(
                                            start = 16.dp,
                                            end = 16.dp,
                                            top = 16.dp,
                                            bottom = 16.dp
                                        )
                                ) {
                                    Text(
                                        text = item.text,
                                        fontSize = 16.sp,
                                        lineHeight = 16.sp,
                                        fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                        fontWeight = FontWeight(400),
                                        color = colors.primary,
                                        letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                    )
                                    Image(
                                        painter = painterResource(item.imagePath),
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        colorFilter = ColorFilter.tint(item.color)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

