package org.videotrade.shopot.presentation.screens.call

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.videotrade.shopot.presentation.components.Call.aceptBtn
import org.videotrade.shopot.presentation.components.Call.rejectBtn
import org.videotrade.shopot.presentation.screens.main.MainScreen
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.maksimus
import shopot.composeapp.generated.resources.person


class IncomingCallScreen(private val userId: String) : Screen {
    
    @Composable
    override fun Content() {
        val photo: DrawableResource = Res.drawable.person
        val navigator = LocalNavigator.currentOrThrow
        
        val viewModel: CallViewModel = koinInject()
        val isConnectedWebrtc by viewModel.isConnectedWebrtc.collectAsState()
        
        LaunchedEffect(isConnectedWebrtc) {
            
            if (isConnectedWebrtc)
                navigator.push(
                    CallScreen(
                        userId,
                        "IncomingCall"
                    )
                )
        }
        
        val name = remember { "Максим Аркаев" }
        
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(photo),
                contentDescription = "background image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(7.dp)
            )
            
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(200.dp)
                        .height(200.dp)
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(100.dp)
                        )
                        .clip(CircleShape)
                ) {
                    Image(
                        painter = painterResource(photo),
                        contentDescription = "profile image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .width(190.dp)
                            .height(190.dp)
                            .clip(CircleShape)
                    )
                }
                
                Text(
                    modifier = Modifier.padding(top = 25.dp),
                    text = "Входящий звонок...",
                    fontSize = 16.sp,
                    color = Color.White
                )
                
                Text(
                    modifier = Modifier.padding(top = 12.5.dp),
                    text = name,
                    fontSize = 24.sp,
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(30.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 50.dp)
                ) {
                    rejectBtn ({ navigator.push(MainScreen()) })
                    aceptBtn {
                        
                        viewModel.initWebrtc()
                        
                        
                    }
                }
            }
        }
    }
}
