import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.resources.Font
import org.koin.compose.koinInject
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.model.GroupUserDTO
import org.videotrade.shopot.domain.model.GroupUserRole
import org.videotrade.shopot.presentation.components.Common.CustomCheckbox
import org.videotrade.shopot.presentation.components.Common.ModalDialogWithoutText
import org.videotrade.shopot.presentation.components.Common.SwipeContainer
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import org.videotrade.shopot.presentation.screens.group.GroupViewModel
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Res


@Composable
fun GroupUserCard(
    groupUser: GroupUserDTO,
    viewModel: ChatViewModel,
    isEdit: Boolean,
    chat: ChatItem,
    onRoleClick: (Offset) -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme
    val inContact = groupUser.phone.let {
        val findContact = viewModel.findContactByPhone(it)
        findContact != null
    }
    val modalVisible = remember { mutableStateOf(false) }
    val groupViewModel: GroupViewModel = koinInject()

    val isAdmin = groupUser.role == GroupUserRole.ADMIN
    val isOwner = groupUser.role == GroupUserRole.OWNER
    val swipeAlpha = remember { mutableStateOf(0f) }


    LaunchedEffect(swipeAlpha.value == 0f) {

    }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth().padding(horizontal = if (isOwner && isEdit) 12.dp else 0.dp)
    ) {
        SwipeContainer(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            isVisible = !isOwner && isEdit,
            isGroup = true,
            onSwipeDelete = {
                modalVisible.value = true
            },
            onRoleClick = onRoleClick,
            onSwipeProgress = { swipeAlpha.value = it }
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row {
                    Avatar(icon = groupUser.icon, size = 56.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(
                        modifier = Modifier,
                        verticalArrangement = Arrangement.Center
                    ) {
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        
                        if (groupUser.phone == viewModel.profile.value.phone) {
                            Row {
                                Text(
                                    stringResource(MokoRes.strings.you),
                                    fontSize = 16.sp,
                                    lineHeight = 16.sp,
                                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                                    fontWeight = FontWeight(500),
                                    color = colors.primary,
                                    letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                )

                                Spacer(modifier = Modifier.width(4.dp))

                                if (isEdit) {
                                    if (isOwner || isAdmin)
                                        Text(
                                            text = if (isOwner) stringResource(MokoRes.strings.owner) else stringResource(MokoRes.strings.admin),
                                            fontSize = 16.sp,
                                            lineHeight = 16.sp,
                                            fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                            fontWeight = FontWeight(400),
                                            color = colors.secondary,
                                            letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                            modifier = Modifier

//                                .alpha(0.7f - swipeAlpha.value) // Плавное исчезновение
                                        )
                                }
                            }
                            
                        } else {
                            if (inContact) {
                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "${groupUser.firstName} ${groupUser.lastName}",
                                            fontSize = 16.sp,
                                            lineHeight = 16.sp,
                                            fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                                            fontWeight = FontWeight(500),
                                            color = colors.primary,
                                            letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                        )

                                        Spacer(modifier = Modifier.width(4.dp))

                                        if (isEdit) {
                                            if (isOwner || isAdmin)
                                                Text(
                                                    text = if (isOwner) stringResource(MokoRes.strings.owner) else stringResource(MokoRes.strings.admin),
                                                    fontSize = 16.sp,
                                                    lineHeight = 16.sp,
                                                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                                    fontWeight = FontWeight(400),
                                                    color = colors.secondary,
                                                    letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                                    modifier = Modifier

//                                .alpha(0.7f - swipeAlpha.value) // Плавное исчезновение
                                                )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "+${groupUser.phone}",
                                        fontSize = 16.sp,
                                        lineHeight = 16.sp,
                                        fontFamily = if (inContact) FontFamily(Font(Res.font.ArsonPro_Regular)) else FontFamily(
                                            Font(Res.font.ArsonPro_Medium)
                                        ),
                                        fontWeight = FontWeight(400),
                                        color = if (inContact) colors.secondary else colors.primary,
                                        letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                    )
                                }
                                } else {

                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "+${groupUser.phone}",
                                        fontSize = 16.sp,
                                        lineHeight = 16.sp,
                                        fontFamily = if (inContact) FontFamily(Font(Res.font.ArsonPro_Regular)) else FontFamily(
                                            Font(Res.font.ArsonPro_Medium)
                                        ),
                                        fontWeight = FontWeight(400),
                                        color = if (inContact) colors.secondary else colors.primary,
                                        letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))

                                    if (isEdit) {
                                        if (isOwner || isAdmin)
                                            Text(
                                                text = if (isOwner) stringResource(MokoRes.strings.owner) else stringResource(MokoRes.strings.admin),
                                                fontSize = 16.sp,
                                                lineHeight = 16.sp,
                                                fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                                fontWeight = FontWeight(400),
                                                color = colors.secondary,
                                                letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                                modifier = Modifier

//                                .alpha(0.7f - swipeAlpha.value) // Плавное исчезновение
                                            )
                                    }
                                }
                            }
                                Spacer(modifier = Modifier.height(8.dp))

                            }





                        }
                    }



                    if (!isEdit) {
                        if (isOwner || isAdmin)
                            Text(
                                text = if (isOwner) stringResource(MokoRes.strings.owner) else stringResource(MokoRes.strings.admin),
                                fontSize = 16.sp,
                                lineHeight = 16.sp,
                                fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                fontWeight = FontWeight(400),
                                color = colors.secondary,
                                letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                modifier = Modifier
                                    .padding(bottom = 10.dp)
//                                .alpha(0.7f - swipeAlpha.value) // Плавное исчезновение
                            )
                    }

            }
        }
    }
    if (modalVisible.value) {
        ModalDialogWithoutText(
            onDismiss = { modalVisible.value = false },
            onConfirm = {
                
                groupViewModel.removeUserFromGroup(chat.chatId, groupUser.id)
                modalVisible.value = false
            },
            confirmText = stringResource(MokoRes.strings.delete),
            dismissText = stringResource(MokoRes.strings.cancel),
            title = "${stringResource(MokoRes.strings.remove_member)} ${groupUser.firstName + " " + groupUser.lastName}?"
        )
    }

}


@Composable
fun ChangeRolePopup(
    isVisible: Boolean,
    currentRole: GroupUserRole?,
    onDismissRequest: () -> Unit,
    onRoleChange: (Boolean) -> Unit,
    offset: IntOffset = IntOffset(0, 0)
) {
    val colors = MaterialTheme.colorScheme
    val isAdmin = currentRole == GroupUserRole.ADMIN

    if (isVisible) {
        Popup(
            alignment = Alignment.TopStart,
            offset = offset,
            onDismissRequest = onDismissRequest
        ) {
            Crossfade(targetState = isVisible, label = "ChangeRolePopup") { visible ->
                if (visible) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(colors.background)
                            .border(
                                width = 1.dp,
                                color = colors.onSecondary,
                                shape = RoundedCornerShape(size = 16.dp)
                            )
                            .padding(16.dp)
                            .width(200.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onRoleChange(!isAdmin)
                                    onDismissRequest()
                                }
                        ) {
                            Text(
                                text = stringResource(MokoRes.strings.admin),
                                fontSize = 16.sp,
                                fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                fontWeight = FontWeight(400),
                                color = colors.primary
                            )
                            CustomCheckbox(
                                checked = isAdmin,
                                onCheckedChange = {
                                    onRoleChange(it)
                                    onDismissRequest()
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}
