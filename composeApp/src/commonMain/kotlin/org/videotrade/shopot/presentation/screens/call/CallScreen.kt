package org.videotrade.shopot.presentation.screens.call

import Avatar
import androidx.compose.foundation.Image
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.seiko.imageloader.rememberImagePainter
import com.shepeliev.webrtckmp.PeerConnectionState
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.api.EnvironmentConfig.serverUrl
import org.videotrade.shopot.multiplatform.CallProviderFactory
import org.videotrade.shopot.multiplatform.MusicPlayer
import org.videotrade.shopot.presentation.components.Call.microfonBtn
import org.videotrade.shopot.presentation.components.Call.rejectBtn
import org.videotrade.shopot.presentation.components.Call.speakerBtn
import org.videotrade.shopot.presentation.screens.main.MainScreen
import shopot.composeapp.generated.resources.Montserrat_Regular
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.person

class CallScreen(
    private val userId: String,
    private val callCase: String,
    private val userIcon: String? = null,
    private val userFirstName: String,
    private val userLastName: String,
    private val userPhone: String,
    
    ) : Screen {
    
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        
        
        var secondsElapsed by remember { mutableStateOf(0) }
        var isRunning by remember { mutableStateOf(false) }
        
        val viewModel: CallViewModel = koinInject()
        val callStateView by viewModel.callState.collectAsState()
        val isConnectedWs by viewModel.isConnectedWs.collectAsState()
        val localStream by viewModel.localStreamm.collectAsState()
        val isCallBackground by viewModel.isCallBackground.collectAsState()
        
        
        val hasExecuted = remember { mutableStateOf(false) }
        
        val callState = remember { mutableStateOf("") }
        
        val isSwitchToSpeaker = remember { mutableStateOf(true) }
        val isSwitchToMicrophone = remember { mutableStateOf(true) }
        
        val musicPlayer = remember { MusicPlayer() }
        
        var isPlaying by remember { mutableStateOf(false) }
        
        
        val imagePainter = if (userIcon.isNullOrBlank()) {
            painterResource(Res.drawable.person)
        } else {
            rememberImagePainter("${serverUrl}file/plain/${userIcon}")
        }
        
        
        
        LaunchedEffect(Unit) {
            
            when (callCase) {
                "Call" -> {
                    musicPlayer.play("caller")
                    isPlaying = true
                }
            }
            
        }
        
        LaunchedEffect(isRunning) {
            if (isRunning) {
                musicPlayer.stop()
                isPlaying = false
            }
        }
        
        DisposableEffect(Unit) {
            onDispose {
                
                when (callCase) {
                    "Call" -> {
                        if (
                            isPlaying
                        ) {
                            musicPlayer.stop()
                            isPlaying = false
                        }
                    }
                }
                
                
            }
        }
        
        LaunchedEffect(isConnectedWs) {
            println("isConnectedWs $isConnectedWs")
            
            if(isConnectedWs) {
                viewModel.initCall(callCase, userId)
            }
        }
        
        
        
        LaunchedEffect(isRunning) {
            while (isRunning) {
                delay(1000L)
                secondsElapsed++
            }
        }
        
        val callIncoming: String = stringResource(MokoRes.strings.call_incoming)
        val connectionEstablishmentInProgress: String =
            stringResource(MokoRes.strings.connection_establishment_in_progress)
        val connectionEstablished: String = stringResource(MokoRes.strings.connection_established)
        val connectionWasBroken: String = stringResource(MokoRes.strings.connection_was_broken)
        val errorOccurredWhileEstablishingConnection: String =
            stringResource(MokoRes.strings.error_occurred_while_establishing_connection)
        val connectionWasClosed: String = stringResource(MokoRes.strings.connection_was_closed)
        
        
        LaunchedEffect(callStateView) {
            when (callStateView) {
                PeerConnectionState.New -> callState.value = callIncoming
                PeerConnectionState.Connecting -> callState.value =
                    connectionEstablishmentInProgress
                
                PeerConnectionState.Connected -> {
                    callState.value = connectionEstablished
                    delay(500)
                    isRunning = true
                }
                
                PeerConnectionState.Disconnected -> callState.value = connectionWasBroken
                PeerConnectionState.Failed -> callState.value =
                    errorOccurredWhileEstablishingConnection
                
                PeerConnectionState.Closed -> callState.value = connectionWasClosed
            }
        }
        
        Image(
            painter = imagePainter,
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
            )
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
                Avatar(userIcon, 190.dp)
            }
            
            
            
            
            Text(
                text = if (isRunning) {
                    val hours = secondsElapsed / 3600
                    val minutes = (secondsElapsed % 3600) / 60
                    val seconds = secondsElapsed % 60
                    if (hours > 0) {
                        "${hours.toString().padStart(2, '0')}:${
                            minutes.toString().padStart(2, '0')
                        }:${seconds.toString().padStart(2, '0')}"
                    } else {
                        "${minutes.toString().padStart(2, '0')}:${
                            seconds.toString().padStart(2, '0')
                        }"
                    }
                } else {
                    callState.value
                },
                modifier = Modifier
                    .padding(top = 25.dp)
                    .align(Alignment.CenterHorizontally),
                fontSize = 16.sp,
                color = Color(255, 255, 255),
                textAlign = TextAlign.Center,
                fontFamily = FontFamily(Font(Res.font.Montserrat_Regular)),
                letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                lineHeight = 20.sp,
            )
            
            
            Text(
                modifier = Modifier
                    .padding(top = 12.5.dp)
                    .align(Alignment.CenterHorizontally),
                text = "$userFirstName $userLastName",
                fontSize = 26.sp,
                color = Color(255, 255, 255),
                textAlign = TextAlign.Center,
                fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
                letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                lineHeight = 20.sp,
            )
            
            Text(
                modifier = Modifier
                    .padding(top = 12.5.dp)
                    .align(Alignment.CenterHorizontally),
                text = "+${userPhone}",
                fontSize = 20.sp,
                color = Color(255, 255, 255),
                textAlign = TextAlign.Center,
                fontFamily = FontFamily(Font(Res.font.Montserrat_Regular)),
                letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                lineHeight = 20.sp,
            )
            
            Spacer(modifier = Modifier.fillMaxHeight(0.2F))

//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.SpaceAround,
//                modifier = Modifier.fillMaxSize()
//            ) {
//                speakerBtn { }
//                rejectBtn({
//
//                    println("rejectBtn")
//                    viewModel.rejectCall(navigator, userId)
//
//
//                }, "Завершить")
//                microfonBtn {}
//            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                microfonBtn(isSwitchToMicrophone.value) {
                    viewModel.setMicro()
                    isSwitchToMicrophone.value = !isSwitchToMicrophone.value
                }
//                videoBtn { }
                
                speakerBtn(isSwitchToSpeaker.value) {
                    CallProviderFactory.create().switchToSpeaker(isSwitchToSpeaker.value)
                    
                    
                    isSwitchToSpeaker.value = !isSwitchToSpeaker.value
                }
            }
            Spacer(modifier = Modifier.height(56.dp))
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                rejectBtn({
                    
                    println("rejectBtn")
                    if (!isCallBackground) {
                        viewModel.rejectCall(navigator, userId)
                        
                        navigator.push(MainScreen())
                        
                    } else {
                        viewModel.rejectCallBackground(userId)
                    }
                    
                    
                }, stringResource(MokoRes.strings.end_call))
            }
        }
    }
}
