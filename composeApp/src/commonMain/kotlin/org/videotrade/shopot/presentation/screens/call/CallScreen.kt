package org.videotrade.shopot.presentation.screens.call

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import com.shepeliev.webrtckmp.MediaDevices
import com.shepeliev.webrtckmp.MediaStream
import com.shepeliev.webrtckmp.VideoStreamTrack
import com.shepeliev.webrtckmp.videoTracks
import org.koin.compose.koinInject
import org.videotrade.shopot.presentation.components.Call.Video
import org.videotrade.shopot.presentation.components.Common.SafeArea

class CallScreen(
//    private val chat: ChatItem,
    private val userId: String,
    private val callCase: String
) : Screen {
    
    @Composable
    override fun Content() {


//        val scope = rememberCoroutineScope()
        val viewModel: CallViewModel = koinInject()
//        val inCommingCall by viewModel.inCommingCall.collectAsState()
        val wsSession by viewModel.wsSession.collectAsState()
        
        val (localStream, setLocalStream) = remember { mutableStateOf<MediaStream?>(null) }
        val (remoteVideoTrack, setRemoteVideoTrack) = remember {
            mutableStateOf<VideoStreamTrack?>(null)
        }
        val hasExecuted = remember { mutableStateOf(false) }
        
        
        
        LaunchedEffect(wsSession) {
            
            
            if (!hasExecuted.value && wsSession != null) {
                
                
                when (callCase) {
                    "Call" -> {
                        viewModel.updateOtherUserId(userId)
                        viewModel.makeCall(userId)
                    }
                    
                    "IncomingCall" -> viewModel.answerCall()
                    
                }
                
                hasExecuted.value = true
            }
        }


//        }
        
        LaunchedEffect(localStream == null) {
            val stream = MediaDevices.getUserMedia(audio = true, video = true)
            setLocalStream(stream)
        }
        
        LaunchedEffect(localStream) {
            if (localStream == null) return@LaunchedEffect
            
            
            
            println("pspspsp ${viewModel.wsSession} /// ${viewModel.peerConnection.value} /// $localStream //// $setRemoteVideoTrack")
            
            
            viewModel.initWebrtc(
                viewModel.wsSession,
                viewModel.peerConnection.value,
                localStream,
                setRemoteVideoTrack,
            )
        }

        val localVideoTrack = localStream?.videoTracks?.firstOrNull()
//
        SafeArea {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                
//
//
                localVideoTrack?.let { Video(track = it, modifier = Modifier.weight(0.4f)) }
                    ?: Box(modifier = Modifier.weight(1f))
//
                remoteVideoTrack?.let { Video(track = it, modifier = Modifier.weight(0.4f)) }
                    ?: Box(modifier = Modifier.weight(1f))
//
//
//
//
            }
        }
    }
}




