package org.videotrade.shopot.presentation.screens.contacts

import Avatar
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
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
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import com.dokar.sonner.ToastType
import com.dokar.sonner.ToasterDefaults
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.resources.Font
import org.koin.compose.koinInject
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.api.navigateToScreen
import org.videotrade.shopot.domain.model.ContactDTO
import org.videotrade.shopot.multiplatform.Platform
import org.videotrade.shopot.multiplatform.getPlatform
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.components.Common.getParticipantCountText
import org.videotrade.shopot.presentation.components.Contacts.CreateGroupChatHeader
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.main.MainViewModel
import org.videotrade.shopot.presentation.tabs.ChatsTab
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Res


class CreateGroupSecondScreen() : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: ContactsViewModel = koinInject()
        val commonViewModel: CommonViewModel = koinInject()
        val chatViewModel: ChatViewModel = koinInject()
        val profile = chatViewModel.profile.collectAsState().value
        val toasterViewModel: CommonViewModel = koinInject()
        val selectedContacts = viewModel.selectedContacts
        val isSearching = remember { mutableStateOf(false) }
        val searchQuery = remember { mutableStateOf("") }
        val groupName = remember { mutableStateOf("") }
        val fillInput = stringResource(MokoRes.strings.please_fill_in_the_group_name_input_field)
        val groupNameError = remember { mutableStateOf<String?>("") }
        val colors = MaterialTheme.colorScheme
        val tabNavigator = LocalTabNavigator.current
        val mainViewModel: MainViewModel = koinInject()
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
                .background(colors.background)
        ) {
            SafeArea(padding = if (getPlatform() == Platform.Android) 0.dp else 16.dp) {
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
                                viewModel.createGroupChat(groupName.value,
//                                    profile.id
                                )
                                navigateToScreen(navigator, CreateChatScreen())
                                tabNavigator.current = ChatsTab
//                                commonViewModel.restartApp()
                                mainViewModel.getChatsInBack()
                            }
                        }
                    )
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .background(color = colors.background),
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
                        val size = filteredContacts.size
                        
                        itemsIndexed(filteredContacts) { _, item ->
                            
                            ContactItem(item = item, viewModel, size)
                        }
                        item {
                            Spacer(modifier = Modifier.height(100.dp))
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun ContactItem(item: ContactDTO, sharedViewModel: ContactsViewModel, size: Int) {
    val colors = MaterialTheme.colorScheme
    
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(colors.background)
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
                            color = colors.primary,
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
                            color = colors.secondary,
                            letterSpacing = TextUnit(0F, TextUnitType.Sp),
                            modifier = Modifier
                        )
                        
                    }
                    
                }
                if (size > 1)
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(30.dp)
                            .clickable {
                                sharedViewModel.removeContact(item)
                            }
                            .padding(8.dp)
                    ) {
                        
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Gray
                        
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
    val nameValidate3 =
        stringResource(MokoRes.strings.group_name_can_contain_only_letters_and_numbers)
    val colors = MaterialTheme.colorScheme
    
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
                cursorBrush = SolidColor(colors.primary),
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                    fontWeight = FontWeight(400),
                    textAlign = TextAlign.Start,
                    letterSpacing = TextUnit(0F, TextUnitType.Sp),
                    color = colors.primary
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier
                    .fillMaxWidth(1f),
                value = groupName.value,
                onValueChange = { newText ->
                    groupName.value = newText
                    groupNameError.value = validateGroupName(
                        newText,
                        nameValidate1,
                        nameValidate2,
                        nameValidate3
                    ) // Валидация никнейма
                },
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                            .background(colors.background, shape = RoundedCornerShape(size = 16.dp))
                            .border(
                                width = 1.dp,
                                color = colors.secondaryContainer,
                                shape = RoundedCornerShape(size = 16.dp)
                            )
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
                                    color = colors.secondary
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
                color = colors.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp),
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
private fun ParticipantCountText(count: Int) {
    val colors = MaterialTheme.colorScheme
    Text(
        text = getParticipantCountText(count),
        fontSize = 16.sp,
        lineHeight = 16.sp,
        fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
        fontWeight = FontWeight(500),
        color = colors.primary,
        letterSpacing = TextUnit(0F, TextUnitType.Sp),
    )
}


private fun validateGroupName(
    name: String,
    nameValidate1: String,
    nameValidate2: String,
    nameValidate4: String
): String? {
    return when {
        name.isBlank() -> nameValidate1
        name.length > 40 -> nameValidate2
        !name.matches(Regex("^[a-zA-Zа-яА-Я0-9\\s]+$")) -> nameValidate4
        else -> null
    }
}