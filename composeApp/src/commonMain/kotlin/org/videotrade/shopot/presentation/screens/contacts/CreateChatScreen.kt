package org.videotrade.shopot.presentation.screens.contacts

import Avatar
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.videotrade.shopot.domain.model.ContactDTO
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.components.ProfileComponents.CreateChatHeader
import org.videotrade.shopot.presentation.tabs.ChatsTab
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.arrowleft
import shopot.composeapp.generated.resources.create_group

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
                    .fillMaxSize()
                    .background(Color(255, 255, 255))
            ) {
                SafeArea {
                Column {
                    CreateChatHeader(
                        text = "Создать чат",
                        isSearching = isSearching,
                        searchQuery = searchQuery,
                    )
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = Color(255, 255, 255))
                    ) {
                        item {
                            makeA_group(contacts)
                        }
                        itemsIndexed(filteredContacts) { _, item ->
                            ChatItem(viewModel, item = item)
                        }
                    }
                }
                }
//                BottomBar(modifier = Modifier.align(Alignment.BottomCenter))
            }

    }
}


@Composable
private fun makeA_group(contacts: List<ContactDTO>) {
    val navigator = LocalNavigator.currentOrThrow
    Box(
        modifier = Modifier
            .background(Color(255, 255, 255))
            .fillMaxSize()
            .padding(top = 10.dp, bottom = 42.dp)
            .clip(
                RoundedCornerShape(4.dp)
            )
            .clickable {
                if (contacts.isNotEmpty())
                    navigator.push(CreateGroupFirstScreen())
            }
    
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Image(
                    painter = painterResource(Res.drawable.create_group),
                    contentDescription = "create group",
                    modifier = Modifier.size(56.dp)
                )
                
                Text(
                    text = "Создать группу",
                    fontSize = 20.sp,
                    fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
                    textAlign = TextAlign.Center,
                    letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                    lineHeight = 20.sp,
                    modifier = Modifier
                        .padding(start = 20.dp)
                
                )
            }
            Image(
                painter = painterResource(Res.drawable.arrowleft),
                contentDescription = "create group arrow",
                modifier = Modifier.size(18.dp)
            )
            
        }
    }
}


@Composable
private fun ChatItem(viewModel: ContactsViewModel, item: ContactDTO) {
    val navigator = LocalNavigator.currentOrThrow
//    val tabNavigator: TabNavigator = LocalTabNavigator.current
    val tabNavigator = LocalTabNavigator.current
    
    
    Box(
        modifier = Modifier
            .padding(top = 22.dp)
            .clip(
                RoundedCornerShape(4.dp)
            )
            .background(Color(255, 255, 255))
            .fillMaxWidth()
            .clickable {
                
                viewModel.createChat(item, tabNavigator)
                
                tabNavigator.current = ChatsTab
            }
    
    ) {
        Column(
        ) {
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
                    Avatar(item.icon, 80.dp)
                    Column(
                        modifier = Modifier.padding(start = 16.dp)
                    ) {
                        Text(
                            text = listOfNotNull(item.firstName, item.lastName)
                                .joinToString(" ")
                                .takeIf { it.isNotBlank() }
                                ?.let {
                                    if (it.length > 35) "${it.take(32)}..." else it
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
                Image(
                    painter = painterResource(Res.drawable.arrowleft),
                    contentDescription = "create group arrow",
                    modifier = Modifier.size(18.dp)
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