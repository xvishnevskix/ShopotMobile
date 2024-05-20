package org.videotrade.shopot.presentation.components.Main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.presentation.components.Call.aceptBtn
import org.videotrade.shopot.presentation.components.Call.rejectBtn
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.maksimus


class IncomingCallScreen() : Screen {
    
    
    @Composable
    override fun Content() {
        var Photo: DrawableResource
        Photo = Res.drawable.maksimus
        
        Image(
            painter = painterResource(Photo),
            contentDescription = "image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .blur(7.dp)
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                //.background(Color(0, 0, 0)),
            )
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(200.dp)
                    .height(200.dp)
                    .background(
                        color = Color(255, 255, 255),
                        shape = RoundedCornerShape(100.dp)
                    )
                    .clip(CircleShape)
            ) {
                Image(
                    painter = painterResource(Photo),
                    contentDescription = "image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .width(190.dp)
                        .height(190.dp)
                        .clip(RoundedCornerShape(100.dp))
                )
                
            }
            
            Text(
                modifier = Modifier
                    .padding(top = 25.dp)
                    .align(Alignment.CenterHorizontally),
                text = "Входящий звонок...",
                fontSize = 16.sp,
                color = Color(255, 255, 255)
            
            )
            var name: String
            name = "Максим Аркаев"
            Text(
                modifier = Modifier
                    .padding(top = 12.5.dp)
                    .align(Alignment.CenterHorizontally),
                text = "$name",
                fontSize = 24.sp,
                color = Color(255, 255, 255)
            
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(330.dp)
                //.background(Color(0, 0, 0)),
            )
            
            
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxSize()
            ) {
                rejectBtn { }
                aceptBtn { }
            }
            
            
        }
    }
}

