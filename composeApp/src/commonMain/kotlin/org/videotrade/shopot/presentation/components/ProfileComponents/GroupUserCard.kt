import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.resources.Font
import org.koin.compose.koinInject
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.model.GroupUserDTO
import org.videotrade.shopot.presentation.components.Common.ModalDialogWithoutText
import org.videotrade.shopot.presentation.components.Common.SwipeToDeleteContainer
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import org.videotrade.shopot.presentation.screens.contacts.ContactsViewModel
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Res


@Composable
fun GroupUserCard(
    groupUser: GroupUserDTO,
    viewModel: ChatViewModel,
    isEdit: Boolean,
    chat: ChatItem,
) {
    val colors = MaterialTheme.colorScheme
    val inContact = groupUser.phone.let {
      val findContact = viewModel.findContactByPhone(it)
      findContact != null
    }
    val modalVisible = remember { mutableStateOf(false) }
    val contactsViewModel: ContactsViewModel = koinInject()


            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                SwipeToDeleteContainer(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min),
                    isVisible = isEdit,
                    onSwipeDelete = {
                        modalVisible.value = true
                    }
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
                            verticalArrangement = Arrangement.Top
                        ) {

                            Spacer(modifier = Modifier.height(4.dp))


                            if (inContact) {
                                Text(
                                    "${groupUser.firstName} ${groupUser.lastName}",
                                    fontSize = 16.sp,
                                    lineHeight = 16.sp,
                                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                                    fontWeight = FontWeight(500),
                                    color = colors.primary,
                                    letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            Text(
                                text = "+${groupUser.phone}",
                                fontSize = 16.sp,
                                lineHeight = 16.sp,
                                fontFamily = if (inContact) FontFamily(Font(Res.font.ArsonPro_Regular)) else FontFamily(Font(Res.font.ArsonPro_Medium)),
                                fontWeight = FontWeight(400),
                                color = if (inContact) colors.secondary else colors.primary,
                                letterSpacing = TextUnit(0F, TextUnitType.Sp),
                            )
                        }
                    }

//                    Text(
//                        text = stringResource(MokoRes.strings.owner),
//                        fontSize = 16.sp,
//                        lineHeight = 16.sp,
//                        fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)) ,
//                        fontWeight = FontWeight(400),
//                        color = colors.secondary,
//                        letterSpacing = TextUnit(0F, TextUnitType.Sp),
//                        modifier = Modifier.padding()
//                    )
                }
            }
        }
    if (modalVisible.value) {
        ModalDialogWithoutText(
            onDismiss = { modalVisible.value = false },
            onConfirm = {
                
                contactsViewModel.removeUserFromGroup(chat.chatId, "groupUser")
                modalVisible.value = false
            },
            confirmText = stringResource(MokoRes.strings.delete),
            dismissText = stringResource(MokoRes.strings.cancel),
            title = "${stringResource(MokoRes.strings.remove_member)} ${groupUser.firstName + " " + groupUser.lastName}?"
        )
    }
}


