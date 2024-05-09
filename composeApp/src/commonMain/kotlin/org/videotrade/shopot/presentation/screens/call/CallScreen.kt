package org.videotrade.shopot.presentation.screens.call

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import co.touchlab.kermit.Logger
import com.shepeliev.webrtckmp.MediaDevices
import com.shepeliev.webrtckmp.MediaStream
import com.shepeliev.webrtckmp.PeerConnection
import com.shepeliev.webrtckmp.VideoStreamTrack
import com.shepeliev.webrtckmp.videoTracks
import org.videotrade.shopot.presentation.components.Call.Video
import org.videotrade.shopot.presentation.components.Common.SafeArea

class CallScreen : Screen {
    
    @Composable
    override fun Content() {
        val scope = rememberCoroutineScope()
        val (localStream, setLocalStream) = remember { mutableStateOf<MediaStream?>(null) }
        val (remoteVideoTrack, setRemoteVideoTrack) = remember {
            mutableStateOf<VideoStreamTrack?>(
                null
            )
        }
        val (peerConnections, setPeerConnections) = remember {
            mutableStateOf<Pair<PeerConnection, PeerConnection>?>(null)
        }
        
        LaunchedEffect(localStream, peerConnections) {
            
            val stream = MediaDevices.getUserMedia(audio = true, video = true)
            
            println("privet $stream")
            setLocalStream(stream)
        }
        SafeArea {
            
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                val localVideoTrack = localStream?.videoTracks?.firstOrNull()
                
                localVideoTrack?.let {
                    Video(
                        track = it,
                        modifier = Modifier.weight(1f)
                    )
                }
                    ?: Box(modifier = Modifier.weight(1f))
                
                remoteVideoTrack?.let {
                    Video(
                        track = it,
                        modifier = Modifier.weight(1f)
                    )
                }
                    ?: Box(modifier = Modifier.weight(1f))
                
            }
        }
        
    }
}