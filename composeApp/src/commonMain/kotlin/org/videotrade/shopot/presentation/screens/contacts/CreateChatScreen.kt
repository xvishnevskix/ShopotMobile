package org.videotrade.shopot.presentation.screens.contacts

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.arrowleft
import shopot.composeapp.generated.resources.person
import shopot.composeapp.generated.resources.search_main


class CreateChatScreen() : Screen {
    @Composable
    override fun Content() {
        
        val viewModel: ContactsViewModel = koinInject()
        val contacts = viewModel.contacts.collectAsState(initial = listOf()).value
        
        
        viewModel.fetchContacts()
        
        
        Box(
            modifier = Modifier
                //background
                .fillMaxSize()
                .background(Color(255, 255, 255))
        ) {
            Column {
                Box(modifier = Modifier.height(40.dp).fillMaxWidth())
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(255, 255, 255))
                        .height(70.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                
                
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp),
                        
                        )
                    {
                        Image(painter = painterResource(Res.drawable.arrowleft),
                            contentDescription = "image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(24.dp).clickable {}
                        
                        )
                    }
                    Text(
                        text = "Создать чат",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily.SansSerif
                    
                    )
                    Box(
                        modifier = Modifier
                            .clickable {},
                        
                        )
                    {
                        Image(
                            painter = painterResource(Res.drawable.search_main),
                            contentDescription = "image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(24.dp)
                        
                        )
                    }
                    
                }
                
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color(255, 255, 255))
                ) {
                    item {
                        makeA_group()
                    }

//                    var usersInfo = arrayListOf(
//                        UserItemInfo("Василий", "+7 (000) 000-00-00"),
//                        UserItemInfo("Андрей", "+7 (000) 000-00-00"),
//                        UserItemInfo("Алексей", "+7 (000) 000-00-00"),
//
//                        )
//                    usersInfo.add(UserItemInfo("Павел", "+7 (000) 000-00-00"))
                    itemsIndexed(
                        contacts
                    
                    ) { _, item ->
                        UserItem(item = item)
                        
                    }
                    
                }
                
                
            }
            
        }
        
        
    }
    
    
}


@Composable
private fun makeA_group() {
    Box(
        modifier = Modifier
            .background(Color(255, 255, 255))
            .fillMaxSize()
            .height(80.dp)
            .padding(top = 10.dp)
            .border(0.5.dp, color = Color(127, 127, 127))
            .clickable { }
    
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(start = 25.dp)
                .height(64.dp),
        ) {
            
            Image(
                painter = painterResource(Res.drawable.person),
                contentDescription = "image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(68.dp)
                    .clip(RoundedCornerShape(100.dp))
            )
            
            
            Text(
                text = "Создать группу",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier
                    .padding(start = 26.dp)
            
            )
            
            
        }
        
        
    }
    
}


