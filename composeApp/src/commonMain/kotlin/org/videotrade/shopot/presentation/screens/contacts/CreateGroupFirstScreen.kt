package org.videotrade.shopot.presentation.screens.contacts

import Avatar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.jetbrains.compose.resources.Font
import org.koin.compose.koinInject
import org.videotrade.shopot.domain.model.ContactDTO
import org.videotrade.shopot.presentation.components.Common.CustomButton
import org.videotrade.shopot.presentation.components.Common.CustomCheckbox
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.components.Main.BottomBar
import org.videotrade.shopot.presentation.components.ProfileComponents.CreateChatHeader
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.randomUser


class CreateGroupFirstScreen() : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: ContactsViewModel = koinInject()
        val contacts = viewModel.contacts.collectAsState(initial = listOf()).value
        val isSearching = remember { mutableStateOf(false) }
        val searchQuery = remember { mutableStateOf("") }

        viewModel.fetchContacts()

        val filteredContacts = if (searchQuery.value.isEmpty()) {
            contacts
        } else {
            contacts.filter {
                
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
                SafeArea {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            //background
                            .fillMaxSize()
                    ) {
                        CreateChatHeader(
                            "Создать группу",
                            isSearching = isSearching,
                            searchQuery = searchQuery,
                        )
                        LazyColumn(
                            modifier = Modifier

                                .fillMaxWidth()
                                .fillMaxHeight(0.8F)
                                .background(color = Color(255, 255, 255))
                        ) {

                            itemsIndexed(

                                filteredContacts

                            ) { _, item ->
                                ChatItem(item = item, sharedViewModel = viewModel)


                            }
                        }



                        Box(
                            modifier = Modifier.padding(top = 85.dp)
                        ) {
                            CustomButton(
                                "Далее",
                                {
                                    navigator.push(
                                        CreateGroupSecondScreen()
                                    )

                                })
                        }

                    }
                }
                BottomBar(modifier = Modifier.align(Alignment.BottomCenter))
            }
        }
}



@Composable
private fun ChatItem(item: ContactDTO, sharedViewModel: ContactsViewModel) {
    val isChecked = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .padding(top = 22.dp)
            .background(Color(255, 255, 255))
            .fillMaxWidth()
            .clickable {
                isChecked.value = !isChecked.value
                if (isChecked.value) {
                    sharedViewModel.addContact(item)
                } else {
                    sharedViewModel.removeContact(item)
                }
            }
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding().fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding()) {
                    Avatar(item.icon, 80.dp)
                    Column(modifier = Modifier.padding(start = 16.dp)) {
                        Text(
                            text = listOfNotNull(item.firstName, item.lastName)
                                .joinToString(" ")
                                .takeIf { it.isNotBlank() }
                                ?.let {
                                    if (it.length > 32) "${it.take(29)}..." else it
                                } ?: "",
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
                            textAlign = TextAlign.Center,
                            letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                            lineHeight = 20.sp,
                            color = Color(0xFF000000)
                        )
                        Text(
                            text = item.phone,
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                            textAlign = TextAlign.Center,
                            letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                            lineHeight = 20.sp,
                            color = Color(0xFF979797),
                            modifier = Modifier.padding(top = 13.dp)
                        )
                    }
                }
                CustomCheckbox(
                    checked = isChecked.value,
                    onCheckedChange = {
                        isChecked.value = it
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
            Divider(
                color = Color(0xFFD9D9D9).copy(alpha = 0.43f),
                thickness = 1.dp,
                modifier = Modifier.padding(top = 22.dp)
            )
        }
    }
}


