package org.videotrade.shopot.presentation.components.Main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import com.shepeliev.webrtckmp.MediaDevices
import com.shepeliev.webrtckmp.MediaStream
import com.shepeliev.webrtckmp.OfferAnswerOptions
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
import org.jetbrains.compose.resources.Font
import org.koin.compose.koinInject
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.presentation.components.Call.Video
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.screens.call.CallViewModel
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular

class CallScreen(private val chat: ChatItem) : Screen {
    
    @Composable
    override fun Content() {
        val scope = rememberCoroutineScope()
        val viewModel: CallViewModel = koinInject()
        val peerConnections by viewModel.peerConnection.collectAsState()
        val inCommingCall by viewModel.inCommingCall.collectAsState()
        
        var text by remember { mutableStateOf("") }
        
        
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
        
   SafeArea {
       Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
           
           Text(viewModel.getCallerId())
           
           
           BasicTextField(
               value = text,
               onValueChange = { text = it },
               textStyle = TextStyle(
                   color = Color.Black,
                   fontSize = 16.sp
               ), // Простой чёрный текст
               cursorBrush = SolidColor(Color.Black), // Чёрный цвет курсора
               visualTransformation = VisualTransformation.None, // Без визуальных преобразований
//                decorationBox = { innerTextField ->
//                    Box {
//                        if (text.isEmpty()) {
//                            Text(
//                                "Написать...",
//                                textAlign = TextAlign.Center,
//                                fontSize = 16.sp,
//                                fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
//                                letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
//                                lineHeight = 20.sp,
//                                color = Color(0xFF979797),
//                            )
//                            // Подсказка
//                        }
//                        innerTextField() // Основное текстовое поле
//                    }
//                }
           )
           
           
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
                           answerCall(
                               viewModel.wsSession.value, answer,
                               viewModel.getOtherUserId()
                           )
                       }
                   }
               }, content = {
                   Text("inCommingCall")
               }, modifier = Modifier.weight(0.4f))
           }
           
           CallButton({
               scope.launch {
                   viewModel.updateOtherUserId(text)
                   
                   val offer = peerConnections.createOffer(
                       OfferAnswerOptions(
                           offerToReceiveVideo = true,
                           offerToReceiveAudio = true
                       )
                   )
                   peerConnections.setLocalDescription(offer)
                   if (viewModel.wsSession.value != null) {
                       makeCall(
                           viewModel.wsSession.value, offer,
                           text
                       )
                   }
               }
           })
       }
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
