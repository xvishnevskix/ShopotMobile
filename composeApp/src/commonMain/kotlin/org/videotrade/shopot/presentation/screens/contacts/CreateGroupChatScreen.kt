package org.videotrade.shopot.presentation.screens.contacts

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.arrowleft
import shopot.composeapp.generated.resources.person
import shopot.composeapp.generated.resources.search_main


class CreateGroupChatScreen() : Screen {
    
    
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
                
                
//                    var usersInfo = arrayListOf(
//                        UserItemInfo("Василий", "+7 (000) 000-00-00"),
//                        UserItemInfo("Андрей", "+7 (000) 000-00-00"),
//                        UserItemInfo("Алексей", "+7 (000) 000-00-00"),
//
//                        )
//                    usersInfo.add(UserItemInfo("Павел", "+7 (000) 000-00-00"))
                    val usersCount = contacts.count().toString()
                    
                    item { CreateGroupInput() }
                    item { participantCountText(usersCount) }
                    itemsIndexed(
                        contacts
                    
                    ) { _, item ->
                        UserItemCrGroup(item = item)
                        
                    }
                    item { NextBtn() }
                    
                }
                
                
            }
            
        }
        
    }
}

@Composable
fun CreateGroupInput() {
    
    val message = remember { mutableStateOf("") }
    Row(
        modifier = Modifier
            .padding(start = 25.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Image(
            painter = painterResource(Res.drawable.person),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(64.dp)
                .align(Alignment.CenterVertically)
                .clip(RoundedCornerShape(100.dp))
        
        )
        
        TextField(
            modifier = Modifier
                .width(260.dp)
                .padding(bottom = 15.dp, start = 25.dp)
                .background(Color(255, 255, 255)),
            label = { Text("Введите имя человека") },
            value = message.value,
            singleLine = true,
            textStyle = TextStyle(fontSize = 20.sp),
            onValueChange = { newText -> message.value = newText },
            colors = TextFieldDefaults.colors(
                disabledLabelColor = Color(0xffc5c7c6),
                focusedLabelColor = Color(0xffc5c7c6),
                focusedContainerColor = Color(255, 255, 255),
                disabledContainerColor = Color(255, 255, 255),
                unfocusedContainerColor = Color(255, 255, 255),
                focusedIndicatorColor = Color(0, 0, 255),
                unfocusedIndicatorColor = Color(0xffc5c7c6),
                disabledIndicatorColor = Color(0xffc5c7c6)
            ),
            
            )
    }
}


@Composable
fun participantCountText(counter: String) {
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(255, 255, 255))
            .height(70.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    
    ) {
        if (counter.toInt() < 5) {
            Text(
                text = "$counter участника",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.SansSerif
            
            )
        } else {
            Text(
                text = "$counter участников",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.SansSerif
            
            )
        }
    }
    
}


@Composable
private fun NextBtn() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(top = 30.dp, bottom = 10.dp),
        horizontalArrangement = Arrangement.Center
    
    ) {
        
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(41, 48, 60),
                contentColor = Color(255, 255, 255)
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .height(60.dp)
                .width(350.dp)
        
        ) {
            
            Text(
                text = "Далее",
                fontSize = 16.sp,
                color = Color(255, 255, 255),
                textAlign = TextAlign.Center,
                modifier = Modifier,
            )
            
        }
    }
    
    
}