package org.videotrade.shopot.presentation.components.Main

import Avatar
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.api.formatTimestamp
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.presentation.screens.chat.ChatScreen
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.double_message_check
import shopot.composeapp.generated.resources.person
import shopot.composeapp.generated.resources.randomUser

@Composable
fun UserComponentItem(chat: ChatItem) {
    val navigator = LocalNavigator.currentOrThrow
    
    
    Row(
        modifier = Modifier.padding(bottom = 12.dp).fillMaxWidth().clickable {
            navigator.push(ChatScreen(chat))
        },
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            
            Avatar(
                drawableRes = Res.drawable.person,
                size = 60.dp
            )
            
            Column(
                modifier = Modifier.padding(start = 10.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "${chat.firstName} ${chat.lastName}",
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
                    letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                    lineHeight = 20.sp,
                    color = Color(0xFF000000)
                
                )
                
                
                val messageContent = chat.lastMessage?.content ?: "Начните переписку"
                
                
                    Text(
                        messageContent,
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                        letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                        lineHeight = 20.sp,
                        color = Color(0xFF979797),
                        modifier = Modifier.padding(top = 5.dp)
                    
                    )
                    
               
                }
            
        }
        
        Row {
            Column(
                modifier = Modifier.padding(top = 12.dp, end = 5.dp)
            ) {
//                Image(
//                    modifier = Modifier.size(14.dp),
//                    painter = painterResource(Res.drawable.double_message_check),
//                    contentDescription = null,
//                )
//                Image(
//                    modifier = Modifier.size(14.dp),
//                    painter = painterResource(Res.drawable.single_message_check),
//                    contentDescription = null,
//                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 9.dp)
            ) {
                chat.lastMessage?.let {
                    
                    Text(
                        formatTimestamp(it.created),
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                        letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                        lineHeight = 20.sp,
                        color = Color(0xFF979797),
                        
                        )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
//                        .background(if (boxText.isEmpty()) Color.Transparent else Color(0xFF2A293C))
                        .background(Color(0xFF2A293C))
                ) {
//                    Text(
//                        "3",
//                        modifier = Modifier
//                            .padding(start = 6.dp, end = 6.dp, top = 0.dp, bottom = 0.dp),
//                        textAlign = TextAlign.Center,
//                        fontSize = 10.sp,
//                        fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
//                        letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
//                        lineHeight = 20.sp,
//                        color = Color(0xFFFFFFFF),
//
//                        )
                    
                }
            }
            
        }
        
        
    }
    
}