package org.videotrade.shopot.presentation.components.Main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import com.shepeliev.webrtckmp.MediaDevices
import com.shepeliev.webrtckmp.MediaStream
import com.shepeliev.webrtckmp.OfferAnswerOptions
import com.shepeliev.webrtckmp.PeerConnection
import com.shepeliev.webrtckmp.SessionDescription
import com.shepeliev.webrtckmp.SessionDescriptionType
import com.shepeliev.webrtckmp.VideoStreamTrack
import com.shepeliev.webrtckmp.videoTracks
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.websocket.Frame
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.presentation.components.Call.Video
import org.videotrade.shopot.presentation.screens.call.CallViewModel
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.person

class CallScreen(private val chat: ChatItem) : Screen {
    
    @Composable
    override fun Content() {
        val scope = rememberCoroutineScope()
        val viewModel: CallViewModel = koinInject()
        val peerConnections by viewModel.peerConnection.collectAsState()
        val inCommingCall by viewModel.inCommingCall.collectAsState()
        
        val (localStream, setLocalStream) = remember { mutableStateOf<MediaStream?>(null) }
        val (remoteVideoTrack, setRemoteVideoTrack) = remember {
            mutableStateOf<VideoStreamTrack?>(null)
        }
        
        LaunchedEffect(localStream == null) {
            val stream = MediaDevices.getUserMedia(audio = true, video = true)
            setLocalStream(stream)
        }
        
        LaunchedEffect(localStream, peerConnections) {
            if (peerConnections == null || localStream == null) return@LaunchedEffect
            
            viewModel.Call(
                viewModel.wsSession,
                viewModel.peerConnection.value,
                localStream,
                setRemoteVideoTrack,
            
            )
        }
        
        val localVideoTrack = localStream?.videoTracks?.firstOrNull()
        
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            localVideoTrack?.let { Video(track = it, modifier = Modifier.weight(1f)) }
                ?: Box(modifier = Modifier.weight(1f))
            
            remoteVideoTrack?.let { Video(track = it, modifier = Modifier.weight(1f)) }
                ?: Box(modifier = Modifier.weight(1f))
            
            if (inCommingCall) {
                Button(onClick = {
                    scope.launch {
                        val answer = peerConnections.createAnswer(
                            options = OfferAnswerOptions(
                                offerToReceiveVideo = true,
                                offerToReceiveAudio = true
                            )
                        )
                        peerConnections.setLocalDescription(answer)
                        if (viewModel.wsSession.value != null) {
                            answerCall(viewModel.wsSession.value, answer, viewModel.getOtherUserId())
                        }
                    }
                }, content = {
                    Text("inCommingCall")
                }, modifier = Modifier.weight(0.4f))
            }
            
            CallButton({
                scope.launch {
                    val offer = peerConnections.createOffer(
                        OfferAnswerOptions(
                            offerToReceiveVideo = true,
                            offerToReceiveAudio = true
                        )
                    )
                    peerConnections.setLocalDescription(offer)
                    if (viewModel.wsSession.value != null) {
                        makeCall(viewModel.wsSession.value, offer, viewModel.getOtherUserId())
                    }
                }
            })
        }
    }
}

@Composable
fun CallButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(onClick, modifier = modifier) {
        Text("Call")
    }
}

@Serializable
data class WebRTCMessage(
    val type: String,
    val calleeId: String? = null,
    val callerId: String? = null,
    val rtcMessage: SessionDescriptionDTO? = null,
    val sender: String? = null,
    val iceMessage: rtcMessageDTO? = null
)

@Serializable
data class rtcMessageDTO(
    val label: Int,
    val id: String,
    val candidate: String,
)

@OptIn(DelicateCoroutinesApi::class)
suspend fun makeCall(
    wsSessionSession: DefaultClientWebSocketSession?,
    offer: SessionDescription?,
    userId: String
) {
    if (offer == null || wsSessionSession == null || wsSessionSession.outgoing.isClosedForSend) {
        return
    }
    
    val newCallMessage = WebRTCMessage(
        type = "call",
        calleeId = userId,
        rtcMessage = SessionDescriptionDTO(offer.type, offer.sdp)
    )
    
    val jsonMessage = Json.encodeToString(WebRTCMessage.serializer(), newCallMessage)
    
    try {
        wsSessionSession.send(Frame.Text(jsonMessage))
        println("Message sent successfully")
    } catch (e: Exception) {
        println("Failed to send message: ${e.message}")
    }
}

@OptIn(DelicateCoroutinesApi::class)
suspend fun answerCall(
    wsSessionSession: DefaultClientWebSocketSession?, answer: SessionDescription?, otherUserId:
    String
) {
    if (answer == null || wsSessionSession == null || wsSessionSession.outgoing.isClosedForSend) {
        return
    }
    
    val answerCallMessage = WebRTCMessage(
        type = "answerCall",
        callerId = otherUserId,
        rtcMessage = SessionDescriptionDTO(answer.type, answer.sdp)
    )
    println("answerCallMessage $answerCallMessage")
    val jsonMessage = Json.encodeToString(WebRTCMessage.serializer(), answerCallMessage)
    
    try {
        wsSessionSession.send(Frame.Text(jsonMessage))
        println("Message sent successfully")
    } catch (e: Exception) {
        println("Failed to send message: ${e.message}")
    }
}

@Serializable
data class SessionDescriptionDTO(
    val type: SessionDescriptionType,
    val sdp: String
)
