package org.videotrade.shopot.presentation.screens.call

import Avatar
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.input.pointer.pointerInput
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
import org.videotrade.shopot.api.getValueInStorage
import org.videotrade.shopot.multiplatform.AudioFactory
import org.videotrade.shopot.multiplatform.CallProviderFactory
import org.videotrade.shopot.multiplatform.isCallActiveNatific
import org.videotrade.shopot.multiplatform.onResumeCallActivity
import org.videotrade.shopot.multiplatform.setScreenLockFlags
import org.videotrade.shopot.presentation.components.Call.aceptBtn
import org.videotrade.shopot.presentation.components.Call.microfonBtn
import org.videotrade.shopot.presentation.components.Call.rejectBtn
import org.videotrade.shopot.presentation.components.Call.speakerBtn
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.main.MainViewModel
import shopot.composeapp.generated.resources.Montserrat_Regular
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.person
import shopot.composeapp.generated.resources.reductionArrows

class CallScreen(
    private val userId: String,
    private val userIcon: String? = null,
    private val userFirstName: String,
    private val userLastName: String,
    private val userPhone: String,
    private val sendCall: Boolean? = null,
) : Screen {
    
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        
        val viewModel: CallViewModel = koinInject()
        val mainViewModel: MainViewModel = koinInject()
        val commonViewModel: CommonViewModel = koinInject()
        val callStateView by viewModel.callState.collectAsState()
        val isCallActive by viewModel.isCallActive.collectAsState()
        val isConnectedWs by viewModel.isConnectedWs.collectAsState()
        val localStream by viewModel.localStreamm.collectAsState()
        val isCallBackground by viewModel.isCallBackground.collectAsState()
        val isIncomingCall by viewModel.isIncomingCall.collectAsState()
        val timerValue = viewModel.timer.collectAsState()
        val isConnectedWebrtc by viewModel.isConnectedWebrtc.collectAsState()
        
        val hasExecuted = remember { mutableStateOf(false) }
        
        val callState = remember { mutableStateOf("") }
        
        LaunchedEffect(Unit) {
            println("isIncomingCall4142 ${isIncomingCall}")
            println("isCallBackground4142 ${isCallBackground}")
            
            setScreenLockFlags(true)
        }
        
        
        val isSwitchToSpeaker = remember { mutableStateOf(true) }
        val isSwitchToMicrophone = remember { mutableStateOf(true) }
        
        val musicPlayer = remember { AudioFactory.createMusicPlayer()  }
        
        var isPlaying by remember { mutableStateOf(false) }
        
        
        val imagePainter = if (userIcon.isNullOrBlank()) {
            painterResource(Res.drawable.person)
        } else {
            rememberImagePainter("${serverUrl}file/plain/${userIcon}")
        }
        
        LaunchedEffect(Unit) {
            onResumeCallActivity(navigator)
        }
        
        LaunchedEffect(Unit) {
            if (isIncomingCall) {
                musicPlayer.play("callee", true)
                isPlaying = true
            } else {
                musicPlayer.play("caller", true)
                isPlaying = true
            }
            
        }
        
        LaunchedEffect(isCallActive) {
            if (isCallActive) {
                musicPlayer.stop()
                isPlaying = false
            }
        }
        
        DisposableEffect(Unit) {
            setScreenLockFlags(false)
            
            onDispose {
                if (
                    isPlaying
                ) {
                    musicPlayer.stop()
                    isPlaying = false
                }
                
                
            }
        }
        
        
        if (isIncomingCall) {
            println("isIncomingCallCase")
            
            LaunchedEffect(isConnectedWebrtc) {
                println("isConnectedWebrtc $isConnectedWebrtc")
                if (isConnectedWebrtc) {
                    viewModel.setIsIncomingCall(false)
                    viewModel.answerCall()
                }
            }
        } else if (isCallBackground) {
            println("isCallBackgroundCase")
            
            val profileId = getValueInStorage("profileId")
            
            LaunchedEffect(Unit) {
                if (profileId != null) {
                    commonViewModel.mainNavigator.value = navigator
                    viewModel.checkUserShared(profileId, navigator)
                    
                }
            }
            
            LaunchedEffect(isConnectedWs) {
                
                println("isConnectedWs $isConnectedWs")
                if (isConnectedWs) {
//                    viewModel.setIsCallBackground(false)
                    viewModel.answerCallBackground()
                }
            }
        } else {
            LaunchedEffect(isConnectedWs) {
                
                println("Call")
                
                if (sendCall == true) {
                    if (!isCallActive)
                        if (isConnectedWs) {
                            viewModel.initCall(userId)
                        }
                }
                
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
                    
                    if (!isCallActive && isConnectedWebrtc) {
                        
                        println("AASDASDAS")
                        viewModel.startTimer(userIcon)
                        viewModel.setIsCallActive(true)
                        isCallActiveNatific()
                    }
                    
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
        
        if (isCallActive && mainViewModel.chats.value.isNotEmpty()) {
            Image(
                painter = painterResource(Res.drawable.reductionArrows),
                contentDescription = "Call",
                modifier = Modifier.padding(start = 30.dp, top = 40.dp).size(20.dp)
                    .pointerInput(Unit) {
                        viewModel.replacePopCall(navigator)
                    }
            )
        }
        
        Box(modifier = Modifier.fillMaxSize().safeContentPadding()) {
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
                    text = if (isIncomingCall) stringResource(MokoRes.strings.incoming_call) else if (isCallActive) {
                        timerValue.value
//                    val hours = secondsElapsed / 3600
//                    val minutes = (secondsElapsed % 3600) / 60
//                    val seconds = secondsElapsed % 60
//                    if (hours > 0) {
//                        "${hours.toString().padStart(2, '0')}:${
//                            minutes.toString().padStart(2, '0')
//                        }:${seconds.toString().padStart(2, '0')}"
//                    } else {
//                        "${minutes.toString().padStart(2, '0')}:${
//                            seconds.toString().padStart(2, '0')
//                        }"
//                    }
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
                
                if (isIncomingCall) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 50.dp)
                    ) {
                        rejectBtn({
                            viewModel.rejectCall(userId)
                        })
                        aceptBtn {
                            viewModel.initWebrtc()
                            
                        }
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        microfonBtn(isSwitchToMicrophone.value) {
                            viewModel.setMicro()
                            isSwitchToMicrophone.value = !isSwitchToMicrophone.value
//                            viewModel.setIsCallActive(true)
                            viewModel.startTimer(userIcon)
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
                            viewModel.rejectCall(userId)
                        }, stringResource(MokoRes.strings.end_call))
                    }
                }
                
            }
        }
    }
}
