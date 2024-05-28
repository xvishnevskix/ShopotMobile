package org.videotrade.shopot.presentation.screens.call

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.koinInject
import org.videotrade.shopot.presentation.components.Call.aceptBtn


class IncomingCallScreen(private val userId: String) : Screen {
    
    @Composable
    override fun Content() {
//        val photo: DrawableResource = Res.drawable.maksimus
        val navigator = LocalNavigator.currentOrThrow
        
        val viewModel: CallViewModel = koinInject()
        val isConnectedWebrtc by viewModel.isConnectedWebrtc.collectAsState()
        
        LaunchedEffect(isConnectedWebrtc) {
            
            println("isConnectedWebrtc $isConnectedWebrtc")
            if (isConnectedWebrtc)
                navigator.push(
                    CallScreen(
                        userId,
                        "IncomingCall"
                    
                    )
                )
        }

//        val name = remember { "Максим Аркаев" }
//
//        Box(modifier = Modifier.fillMaxSize()) {
//            Image(
//                painter = painterResource(photo),
//                contentDescription = "background image",
//                contentScale = ContentScale.Crop,
//                modifier = Modifier
//                    .fillMaxSize()
//                    .blur(7.dp)
//            )
//
//            Column(
//                modifier = Modifier.fillMaxSize(),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Center
//            ) {
//                Box(
//                    modifier = Modifier
//                        .width(200.dp)
//                        .height(200.dp)
//                        .background(
//                            color = Color.White,
//                            shape = RoundedCornerShape(100.dp)
//                        )
//                        .clip(CircleShape)
//                ) {
//                    Image(
//                        painter = painterResource(photo),
//                        contentDescription = "profile image",
//                        contentScale = ContentScale.Crop,
//                        modifier = Modifier
//                            .align(Alignment.Center)
//                            .width(190.dp)
//                            .height(190.dp)
//                            .clip(CircleShape)
//                    )
//                }
//
//                Text(
//                    modifier = Modifier.padding(top = 25.dp),
//                    text = "Входящий звонок...",
//                    fontSize = 16.sp,
//                    color = Color.White
//                )
//
//                Text(
//                    modifier = Modifier.padding(top = 12.5.dp),
//                    text = name,
//                    fontSize = 24.sp,
//                    color = Color.White
//                )
//
//                Spacer(modifier = Modifier.height(30.dp))

//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.SpaceAround,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 50.dp)
//                ) {
//                    rejectBtn { /* Handle reject */ }
        aceptBtn {
            
            viewModel.initWebrtc()
            
            
        }
//                }
    }
}
