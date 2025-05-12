package org.videotrade.shopot.presentation.screens.contacts

import Avatar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.InternalResourceApi
import org.koin.compose.koinInject
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.api.navigateToScreen
import org.videotrade.shopot.domain.model.ContactDTO
import org.videotrade.shopot.multiplatform.Platform
import org.videotrade.shopot.multiplatform.getPlatform
import org.videotrade.shopot.presentation.components.Common.CustomCheckbox
import org.videotrade.shopot.parkingProj.presentation.components.SafeArea
import org.videotrade.shopot.presentation.components.Contacts.ContactsSearch
import org.videotrade.shopot.presentation.components.Contacts.CreateGroupChatHeader
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.profile.ProfileViewModel
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Res


class CreateGroupFirstScreen : Screen {
    @OptIn(InternalResourceApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: ContactsViewModel = koinInject()
        val viewModelProfile: ProfileViewModel = koinInject()
        val contacts = viewModel.contacts.collectAsState(initial = listOf()).value
        val selectedContacts = viewModel.selectedContacts
        val isSearching = remember { mutableStateOf(false) }
        val searchQuery = remember { mutableStateOf("") }
        val toasterViewModel: CommonViewModel = koinInject()
        val colors = MaterialTheme.colorScheme

        val selectParticipants = stringResource(MokoRes.strings.select_participants)

        viewModel.fetchContacts()


        val filteredContacts = contacts.filter { contact ->
            contact.phone != viewModelProfile.profile.value.phone &&
                    (searchQuery.value.isEmpty() ||
                            (contact.firstName?.contains(searchQuery.value, ignoreCase = true) == true ||
                                    contact.phone.contains(searchQuery.value)))
        }

        val groupedContacts = filteredContacts.groupBy { it.firstName?.firstOrNull()?.uppercaseChar() ?: '#' }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.background)
        ) {
            SafeArea(padding = if (getPlatform() == Platform.Android) 0.dp else 16.dp) {
                Column(
                    modifier = Modifier.background(colors.background)
                ) {
                    CreateGroupChatHeader(
                        stringResource(MokoRes.strings.create_group),
                        order = "1",
                        viewModel = viewModel,
                        onClick = {
                            if (selectedContacts.isEmpty()) {
//                                toasterViewModel.toaster.show(
//                                    message = selectParticipants,
//                                    type = ToastType.Error,
//                                    duration = ToasterDefaults.DurationDefault
//                                )
                            } else {
                                navigateToScreen(navigator,CreateGroupSecondScreen())
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    ContactsSearch(searchQuery, isSearching)
                    Spacer(modifier = Modifier.height(24.dp))

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .background(color = colors.background)
                    ) {
                        item {

                        }

//                        itemsIndexed(filteredContacts) { _, item ->
//                            ChatItem(item = item, sharedViewModel = viewModel)
//                        }
                        groupedContacts.forEach { (initial, contacts) ->
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(colors.onBackground)
                                ) {
                                    Text(text = initial.toString(),
                                        textAlign = TextAlign.Start,
                                        fontSize = 16.sp,
                                        lineHeight = 16.sp,
                                        fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                        fontWeight = FontWeight(400),
                                        color = colors.secondary ,
                                        letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
                                }
                            }
                            items(contacts) { contact ->
                                ContactItem(sharedViewModel = viewModel, item = contact)
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(100.dp))
                        }
                    }
//                    Box(modifier = Modifier.padding(top = 5.dp)) {
//                        CustomButton(
//                            stringResource(MokoRes.strings.next),
//                            {
//                                if (selectedContacts.isEmpty()) {
////                                    Toast.makeText(context, "Выберите участников", Toast.LENGTH_SHORT).show()
//                                } else {
//                                    navigateToScreen(navigator,CreateGroupSecondScreen())
//                                }
//                            }
//                        )
//                    }
                }
            }
        }
    }
}

@Composable
private fun ContactItem(item: ContactDTO, sharedViewModel: ContactsViewModel) {
    val isChecked = remember { derivedStateOf { sharedViewModel.isContactSelected(item) } }
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(colors.background)
            .fillMaxWidth()
            .clickable {
                if (!isChecked.value) {
                    sharedViewModel.addContact(item)
                } else {
                    sharedViewModel.removeContact(item)
                }
            }
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
                            color = colors.secondary ,
                            letterSpacing = TextUnit(0F, TextUnitType.Sp),
                            modifier = Modifier
                        )

                    }

                }
                CustomCheckbox(
                    checked = isChecked.value,
                    onCheckedChange = {
                        if (it) {
                            sharedViewModel.addContact(item)
                        } else {
                            sharedViewModel.removeContact(item)
                        }
                    },
                    backgroundColor = Color(0xFF2A293C),
                    checkmarkColor = Color.White
                )
            }
            Spacer(modifier = Modifier.height(9.dp))

        }
    }
}