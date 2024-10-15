package org.videotrade.shopot.presentation.screens.call

import Avatar
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.multiplatform.MusicPlayer
import org.videotrade.shopot.presentation.components.Call.aceptBtn
import org.videotrade.shopot.presentation.components.Call.rejectBtn
import org.videotrade.shopot.presentation.screens.main.MainScreen
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.person


class IncomingCallScreen(
    private val userId: String,
    private val userIcon: String? = null,
    private val userFirstName: String,
    private val userLastName: String,
    private val userPhone: String
) : Screen {
    
    @Composable
    override fun Content() {
        val photo: DrawableResource = Res.drawable.person
        val navigator = LocalNavigator.currentOrThrow
        
        val viewModel: CallViewModel = koinInject()
        val isConnectedWebrtc by viewModel.isConnectedWebrtc.collectAsState()
        val isCallBackground by viewModel.isCallBackground.collectAsState()
        
        val musicPlayer = remember { MusicPlayer() }
        
        // Используем состояние для отслеживания, играет ли музыка
        var isPlaying by remember { mutableStateOf(false) }

//        val imagePainter = if (user.icon.isNullOrBlank()) {
//            painterResource(Res.drawable.person)
//        } else {
//            rememberImagePainter("${serverUrl}file/plain/${user.icon}")
//        }
        
        val imagePainter = painterResource(Res.drawable.person)
        
        
        LaunchedEffect(Unit) {
            musicPlayer.play("callee")
            isPlaying = true
        }
        
        DisposableEffect(Unit) {
            onDispose {
                if (
                    isPlaying
                ) {
                    musicPlayer.stop()
                    isPlaying = false
                    
                }
                
            }
        }
        
//        LaunchedEffect(isConnectedWebrtc) {
//            if (isConnectedWebrtc)
//                navigator.push(
//                    CallScreen(
//                        userId,
//                        if (isCallBackground) "IncomingBackgroundCall" else "IncomingCall",
//                        userIcon,
//                        userFirstName,
//                        userLastName,
//                        userPhone,
//                    )
//                )
//        }
        
        val name = remember { "$userFirstName $userLastName" }
        
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = imagePainter,
                contentDescription = "image",
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
                        .align(Alignment.CenterHorizontally)
                        .width(190.dp)
                        .height(190.dp)
                        .background(
                            color = Color(255, 255, 255),
                            shape = RoundedCornerShape(100.dp)
                        )
                        .clip(CircleShape)
                ) {
                    Avatar(null, 190.dp)
                }
                
                Text(
                    modifier = Modifier.padding(top = 25.dp),
                    text = stringResource(MokoRes.strings.incoming_call),
                    fontSize = 16.sp,
                    color = Color.White
                )
                
                Text(
                    modifier = Modifier.padding(top = 12.5.dp),
                    text = name,
                    fontSize = 24.sp,
                    color = Color.White
                )
                
                
                Text(
                    modifier = Modifier.padding(top = 12.5.dp),
                    text = "+${userPhone}",
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
                    rejectBtn({
                            viewModel.rejectCall( userId)
                    })
                    aceptBtn {
                        
                        viewModel.initWebrtc()
                        
                        
                    }
                }
            }
        }
    }
}
