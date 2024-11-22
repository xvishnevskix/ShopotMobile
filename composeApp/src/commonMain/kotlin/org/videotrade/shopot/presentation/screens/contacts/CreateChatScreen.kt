package org.videotrade.shopot.presentation.screens.contacts

import Avatar
import androidx.compose.animation.Crossfade
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
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
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.domain.model.ContactDTO
import org.videotrade.shopot.presentation.components.Common.CustomTextField
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.components.Common.validateFirstName
import org.videotrade.shopot.presentation.components.Contacts.ContactsSearch
import org.videotrade.shopot.presentation.components.Contacts.MakeGroup
import org.videotrade.shopot.presentation.components.ProfileComponents.CreateChatHeader
import org.videotrade.shopot.presentation.tabs.ChatsTab
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.arrow_left
import shopot.composeapp.generated.resources.arrowleft
import shopot.composeapp.generated.resources.create_group
import shopot.composeapp.generated.resources.group

class CreateChatScreen() : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: ContactsViewModel = koinInject()
        val contacts = viewModel.contacts.collectAsState(initial = listOf()).value
        val isSearching = remember { mutableStateOf(false) }
        val searchQuery = remember { mutableStateOf("") }
        
        
        
        LaunchedEffect(Unit) {
        viewModel.getContacts()
        
        }
        
        println("contacts@@##@##@ $contacts")
        
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

        val groupedContacts = filteredContacts.groupBy { it.firstName?.firstOrNull()?.uppercaseChar() ?: '#' }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color(0xFFF7F7F7))
        ) {
            SafeArea(padding = 0.dp) {
                Column {
                    CreateChatHeader(
                        text = stringResource(MokoRes.strings.contacts),
                        isSearching = isSearching,
                    )
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = Color(255, 255, 255))
                            .padding(bottom = 60.dp)
                    ) {
                        item {
                            Crossfade(targetState = isSearching.value) { searching ->
                                if (searching) {
                                    ContactsSearch(searchQuery, isSearching)
                                } else {
                                    MakeGroup(contacts)
                                }
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                        groupedContacts.forEach { (initial, contacts) ->
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFF7F7F7))
                                ) {
                                    Text(text = initial.toString(),
                                        textAlign = TextAlign.Start,
                                        fontSize = 16.sp,
                                        lineHeight = 16.sp,
                                        fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                        fontWeight = FontWeight(400),
                                        color = Color(0x80373533),
                                        letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
                                }
                            }
                            items(contacts) { contact ->
                                ContactItem(viewModel, item = contact)
                            }
                        }
                        item {
                            Box(modifier = Modifier.height(70.dp))
                        }
                    }
                }
            }
//                BottomBar(modifier = Modifier.align(Alignment.BottomCenter))
        }

    }
}


@Composable
private fun ContactItem(viewModel: ContactsViewModel, item: ContactDTO) {
    val tabNavigator = LocalTabNavigator.current
    
    
    Box(
        modifier = Modifier.padding(horizontal = 16.dp)
            .clip(
                RoundedCornerShape(4.dp)
            )
            .background(Color(255, 255, 255))
            .fillMaxWidth()
            .clickable {
                println("item41421 $item")
                viewModel.createChat(item, tabNavigator)
                
                tabNavigator.current = ChatsTab
            }
    
    ) {
        Column(
        ) {
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
            }
            Spacer(modifier = Modifier.height(9.dp))
        }
    }
}