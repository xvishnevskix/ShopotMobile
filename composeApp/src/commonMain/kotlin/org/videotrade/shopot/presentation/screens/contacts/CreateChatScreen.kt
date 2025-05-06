package org.videotrade.shopot.presentation.screens.contacts

import Avatar
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import org.koin.compose.koinInject
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.api.navigateToScreen
import org.videotrade.shopot.domain.model.ContactDTO
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.multiplatform.ContactsProviderFactory
import org.videotrade.shopot.multiplatform.Platform
import org.videotrade.shopot.multiplatform.getPlatform
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.components.Contacts.ContactsSearch
import org.videotrade.shopot.presentation.components.Contacts.InviteContacts
import org.videotrade.shopot.presentation.components.Contacts.MakeGroup
import org.videotrade.shopot.presentation.components.ProfileComponents.CreateChatHeader
import org.videotrade.shopot.presentation.screens.profile.ProfileChatScreen
import org.videotrade.shopot.presentation.screens.profile.ProfileViewModel
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Res

class CreateChatScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: ContactsViewModel = koinInject()
        val viewModelProfile: ProfileViewModel = koinInject()
        val contacts = viewModel.contacts.collectAsState(initial = listOf()).value
        val unregisteredContacts =
            viewModel.unregisteredContacts.collectAsState(initial = listOf()).value
        val isSearching = remember { mutableStateOf(false) }
        val searchQuery = remember { mutableStateOf("") }
        val colors = MaterialTheme.colorScheme
        
        
        LaunchedEffect(Unit) {
            viewModel.getContacts()
        }
        
        println("contacts@@##@##@ $contacts")
        
        val filteredContacts = contacts.filter { contact ->
            contact.phone != viewModelProfile.profile.value.phone &&
                    (searchQuery.value.isEmpty() ||
                            (contact.firstName?.contains(
                                searchQuery.value,
                                ignoreCase = true
                            ) == true ||
                                    contact.phone.contains(searchQuery.value)))
        }
        
        val sortedUnregisteredContacts = unregisteredContacts.sortedBy { it.firstName }
        
        
        val groupedContacts =
            filteredContacts.groupBy { it.firstName?.firstOrNull()?.uppercaseChar() ?: '#' }
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(colors.surface)
        ) {
            SafeArea(padding = if (getPlatform() == Platform.Android) 0.dp else 16.dp)
            
            {
                Column(
                    Modifier.background(colors.background)
                ) {
                    CreateChatHeader(
                        text = stringResource(MokoRes.strings.contacts),
                        isSearching = isSearching,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(
                        modifier = Modifier
                            .animateContentSize()
                    ) {
                        Crossfade(targetState = isSearching.value) { searching ->
                            if (searching) {
                                Column {
                                    ContactsSearch(searchQuery, isSearching)
                                }
                            } else {
                                Column(Modifier.animateContentSize()) {
                                    MakeGroup(contacts)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    InviteContacts(unregisteredContacts)
                                }
                                
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyColumn(
                        modifier = Modifier
                            .animateContentSize()
                            .fillMaxSize()
                            .background(color = colors.background)
                            .padding(bottom = 60.dp)
                    
                    ) {
                        groupedContacts.forEach { (initial, contacts) ->
                            item {
                            
                            }
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(colors.onBackground)
                                ) {
                                    Text(
                                        text = initial.toString(),
                                        textAlign = TextAlign.Start,
                                        fontSize = 16.sp,
                                        lineHeight = 16.sp,
                                        fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                        fontWeight = FontWeight(400),
                                        color = colors.secondary,
                                        letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                        modifier = Modifier.padding(
                                            horizontal = 16.dp,
                                            vertical = 4.dp
                                        )
                                    )
                                    
                                    
                                }
                            }
                            items(contacts) { contact ->
                                ContactItem(viewModel, item = contact)
                            }
                        }
//                        if(sortedUnregisteredContacts.isNotEmpty()) {
//                            item {
//                                Box(
//                                    modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
//                                        .fillMaxWidth()
//                                        .background(colors.onBackground)
//                                ) {
//                                    Text(
//                                        text = stringResource(MokoRes.strings.invite_to_shopot),
//                                        textAlign = TextAlign.Start,
//                                        fontSize = 16.sp,
//                                        lineHeight = 16.sp,
//                                        fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
//                                        fontWeight = FontWeight(500),
//                                        color = colors.secondary,
//                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
//                                    )
//
//                                }
//                            }
//                            items(sortedUnregisteredContacts) { contact ->
//                                ContactItem(viewModel, item = contact,true)
//                            }
//                        }
                        
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
private fun ContactItem(
    viewModel: ContactsViewModel, item: ContactDTO,
    onInviteNewUsers: Boolean? = null
) {
    val navigator = LocalNavigator.currentOrThrow
    val colors = MaterialTheme.colorScheme
    
    Box(
        modifier = Modifier.padding(horizontal = 16.dp)
            .clip(
                RoundedCornerShape(4.dp)
            )
            .background(colors.background)
            .fillMaxWidth()
            .clickable {
                println("item41421 $onInviteNewUsers")
                if (onInviteNewUsers == null && item.id !== null) {
                    
                    navigateToScreen(
                        navigator,
                        ProfileChatScreen(
                            ProfileDTO(
                                id = item.id,
                                firstName = item.firstName ?: "",
                                lastName = item.lastName ?: "",
                                icon = item.icon ?: "",
                                phone = item.phone,
                                login =  item.login,
                            ),
                            
                            true
                        )
                    )

//                    viewModel.createChat(item, tabNavigator)

//                    tabNavigator.current = ChatsTab
                } else {
                    ContactsProviderFactory.create().sendMessageInvite()
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
                            color = colors.secondary,
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