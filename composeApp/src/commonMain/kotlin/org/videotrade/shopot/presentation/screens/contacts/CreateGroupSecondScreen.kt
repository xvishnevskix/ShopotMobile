package org.videotrade.shopot.presentation.screens.contacts

import Avatar
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.dokar.sonner.ToastType
import com.dokar.sonner.ToasterDefaults
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.domain.model.ContactDTO
import org.videotrade.shopot.presentation.components.Common.CustomButton
import org.videotrade.shopot.presentation.components.Common.CustomCheckbox
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.components.Contacts.CreateGroupChatHeader
import org.videotrade.shopot.presentation.components.ProfileComponents.CreateChatHeader
import org.videotrade.shopot.presentation.screens.chats.ChatsScreen
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.main.MainScreen
import org.videotrade.shopot.presentation.screens.main.MainViewModel
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.edit_group_name
import kotlin.math.abs


class CreateGroupSecondScreen() : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: ContactsViewModel = koinInject()
        val commonViewModel: CommonViewModel = koinInject()
        val toasterViewModel: CommonViewModel = koinInject()
        val selectedContacts = viewModel.selectedContacts
        val isSearching = remember { mutableStateOf(false) }
        val searchQuery = remember { mutableStateOf("") }
        val groupName = remember { mutableStateOf("") }
        val fillInput = stringResource(MokoRes.strings.please_fill_in_the_group_name_input_field)
        val groupNameError = remember { mutableStateOf<String?>("") }
        
        val filteredContacts = if (searchQuery.value.isEmpty()) {
            selectedContacts
        } else {
            selectedContacts.filter {
                
                if (it.firstName !== null) {
                    it.firstName.contains(
                        searchQuery.value,
                        ignoreCase = true
                    ) || it.phone.contains(
                        searchQuery.value
                    )
                } else {
                    false
                }
            }
        }



            Box(
                modifier = Modifier
                    //background
                    .fillMaxWidth()
                    .background(Color(255, 255, 255))
            ) {
                SafeArea(padding = 0.dp) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CreateGroupChatHeader(
                        stringResource(MokoRes.strings.create_group),
                        order = "2",
                        onClick = {
                            if (groupNameError.value != null) {
                                    toasterViewModel.toaster.show(
                                        fillInput,
                                        type = ToastType.Error,
                                        duration = ToasterDefaults.DurationDefault
                                    )
                                } else {
                                    viewModel.createGroupChat(groupName.value)
                                    commonViewModel.restartApp()
                                }
                        }
                    )
                    LazyColumn(
                        modifier = Modifier

                            .fillMaxWidth()
                            .fillMaxHeight(0.8F)
                            .background(color = Color(255, 255, 255)),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(46.dp))
                            CreateGroupInput(groupName, groupNameError)
                            Spacer(modifier = Modifier.height(24.dp))
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                                contentAlignment = Alignment.TopStart
                            ) {
                                ParticipantCountText(selectedContacts.size)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        itemsIndexed(filteredContacts) { _, item ->
                            ContactItem(item = item, viewModel)
                        }
                    }

//                    Box(
//                        modifier = Modifier.padding(top = 5.dp)
//                    ) {
//                        CustomButton(
//                            stringResource(MokoRes.strings.next),
//                            {
//
//                                if (groupNameError.value != null) {
//                                    toasterViewModel.toaster.show(
//                                        fillInput,
//                                        type = ToastType.Error,
//                                        duration = ToasterDefaults.DurationDefault
//                                    )
//                                } else {
//                                    viewModel.createGroupChat(groupName.value)
//                                    commonViewModel.restartApp()
//                                }
//
//                            })
//                    }
                }
                }
//                BottomBar(modifier = Modifier.align(Alignment.BottomCenter))
            }
    }
}


@Composable
private fun ContactItem(item: ContactDTO, sharedViewModel: ContactsViewModel) {


    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color(255, 255, 255))
            .fillMaxWidth()

    ) {
        Column {
            Spacer(modifier = Modifier.height(9.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding().fillMaxWidth()
            )
            {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding()
                ) {
                    Avatar(item.icon, 56.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        Text(
                            text = listOfNotNull(item.firstName, item.lastName)
                                .joinToString(" ")
                                .takeIf { it.isNotBlank() }
                                ?.let {
                                    if (it.length > 35) "${it.take(32)}..." else it
                                } ?: "",
                            fontSize = 16.sp,
                            lineHeight = 16.sp,
                            fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                            fontWeight = FontWeight(500),
                            color = Color(0xFF373533),
                            letterSpacing = TextUnit(0F, TextUnitType.Sp),
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = item.phone,
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp,
                            lineHeight = 16.sp,
                            fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                            fontWeight = FontWeight(400),
                            color = Color(0x80373533),
                            letterSpacing = TextUnit(0F, TextUnitType.Sp),
                            modifier = Modifier
                        )

                    }

                }

                Box(
                    contentAlignment = Alignment.Center
                ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF000000),
                            modifier = Modifier
                                .size(20.dp)
                                .padding()
                                .pointerInput(Unit) {
                                    sharedViewModel.removeContact(item)
                                }

                        )
                }



            }
            Spacer(modifier = Modifier.height(9.dp))

        }
    }
}


@Composable
fun CreateGroupInput(groupName: MutableState<String>, groupNameError: MutableState<String?>) {

    val nameValidate1 = stringResource(MokoRes.strings.group_name_is_required)
    val nameValidate2 = stringResource(MokoRes.strings.group_name_should_not_exceed_40_characters)
    val nameValidate3 = stringResource(MokoRes.strings.group_name_can_contain_only_letters_and_numbers)


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {


            BasicTextField(
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                    fontWeight = FontWeight(400),
                    textAlign = TextAlign.Start,
                    letterSpacing = TextUnit(0F, TextUnitType.Sp),
                    color = Color(0xFF373533)
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier
                    .fillMaxWidth(1f),
                value = groupName.value,
                onValueChange = {
                        newText -> groupName.value = newText
                    groupNameError.value = validateGroupName(newText, nameValidate1, nameValidate2, nameValidate3) // Валидация никнейма
                },
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                            .background(Color.White, shape = RoundedCornerShape(size = 16.dp))
                            .border(width = 1.dp, color = Color(0x33373533), shape = RoundedCornerShape(size = 16.dp))
                            .padding(
                                horizontal = 16.dp,
                                vertical = 16.dp
                            ),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (groupName.value.isEmpty()) {
                            Text(
                                text = stringResource(MokoRes.strings.enter_group_name),
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    lineHeight = 16.sp,
                                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                    fontWeight = FontWeight(400),
                                    textAlign = TextAlign.Start,
                                    letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                    color = Color(0x80373533)
                                )
                            )
                        }
                        innerTextField()
                    }
                },



                )
        }
        groupNameError.value?.let {
            Text(
                text = it,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp),
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
fun ParticipantCountText(count: Int) {
    Text(
        text = getParticipantCountText(count),
        fontSize = 16.sp,
        fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
        color = Color(0xFF000000),
        modifier = Modifier
    )
}

@Composable
fun getParticipantCountText(count: Int): String {
    val forms = arrayOf(
        stringResource(MokoRes.strings.participant),
        stringResource(MokoRes.strings.participants_1),
        stringResource(MokoRes.strings.participants_2)
    )
    return "$count ${getPluralForm(count, forms)}"
}

fun getPluralForm(number: Int, forms: Array<String>): String {
    val n = abs(number) % 100
    val n1 = n % 10
    return when {
        n in 11..19 -> forms[2]
        n1 == 1 -> forms[0]
        n1 in 2..4 -> forms[1]
        else -> forms[2]
    }
}

private fun validateGroupName(name: String, nameValidate1: String, nameValidate2: String, nameValidate4: String): String? {
    return when {
        name.isEmpty() -> nameValidate1
        name.length > 40 -> nameValidate2
        !name.matches(Regex("^[\\p{L}\\p{N}\\p{S}\\s]+$")) -> nameValidate4 // Добавлено поддержка эмодзи
        else -> null
    }
}