package org.videotrade.shopot.presentation.screens.call

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import com.shepeliev.webrtckmp.videoTracks
import org.koin.compose.koinInject
import org.videotrade.shopot.presentation.components.Call.Video
import org.videotrade.shopot.presentation.components.Common.SafeArea

class CallScreen(
//    private val chat: ChatItem,
    private val userId: String,
    private val callCase: String,
    
    ) : Screen {
    
    @Composable
    override fun Content() {
        
        
        val viewModel: CallViewModel = koinInject()
        val wsSession by viewModel.wsSession.collectAsState()
        val localStream by viewModel.localStream.collectAsState()
//        val remoteVideoTrack by viewModel.remoteVideoTrack.collectAsState()
//
//        val localVideoTrack = localStream?.videoTracks?.firstOrNull()
        
        val hasExecuted = remember { mutableStateOf(false) }
        
        
        LaunchedEffect(wsSession) {
            
            
            if (!hasExecuted.value && wsSession != null) {
                when (callCase) {
                    "Call" -> {
                        viewModel.initWebrtc()
                        
                        viewModel.updateOtherUserId(userId)
                        viewModel.makeCall(userId)
                    }
                    
                    "IncomingCall" -> viewModel.answerCall()
                    
                }
                
                hasExecuted.value = true
            }
        }
        
        
        
        
        
        SafeArea {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                
                
//                localVideoTrack?.let { Video(track = it, modifier = Modifier.weight(0.4f)) }
//                    ?: Box(modifier = Modifier.weight(0.5f))
//
//                remoteVideoTrack?.let { Video(track = it, modifier = Modifier.weight(0.4f)) }
//                    ?: Box(modifier = Modifier.weight(0.5f))
                
                
            }
        }
    }
}




