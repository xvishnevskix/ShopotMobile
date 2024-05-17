package org.videotrade.shopot.presentation.screens.call

import Call
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import co.touchlab.kermit.Logger
import com.shepeliev.webrtckmp.IceCandidate
import com.shepeliev.webrtckmp.IceServer
import com.shepeliev.webrtckmp.MediaDevices
import com.shepeliev.webrtckmp.MediaStream
import com.shepeliev.webrtckmp.OfferAnswerOptions
import com.shepeliev.webrtckmp.PeerConnection
import com.shepeliev.webrtckmp.RtcConfiguration
import com.shepeliev.webrtckmp.SessionDescription
import com.shepeliev.webrtckmp.SessionDescriptionType
import com.shepeliev.webrtckmp.VideoStreamTrack
import com.shepeliev.webrtckmp.videoTracks
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.http.HttpMethod
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.videotrade.shopot.multiplatform.DeviceIdProviderFactory
import org.videotrade.shopot.presentation.components.Call.Video


fun showDeviceId(): String? {
    val deviceIdProvider = DeviceIdProviderFactory.create()
    
    
    return deviceIdProvider.getDeviceId()
}

class CallScreenCopy(private val userId: String = "82c208513187dc01") : Screen {
    
    
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
            mutableStateOf<PeerConnection?>(null)
        }
        val webSocketSession = remember { mutableStateOf<DefaultClientWebSocketSession?>(null) }
        val isConnected = remember { mutableStateOf(false) }
        
        val httpClient = HttpClient {
            install(WebSockets)
            
        }
        
        
        val inCommingCall = remember { mutableStateOf(false) }
        
        val otherUserId = remember { mutableStateOf<String?>(userId) }
        
        val callerId = remember { mutableStateOf<String?>(showDeviceId()) }
        
        
        
        LaunchedEffect(localStream == null) {
            val stream = MediaDevices.getUserMedia(audio = true, video = true)
            
            setLocalStream(stream)
        }
        
        
        
        
        
        
        DisposableEffect(Unit) {
            val job = scope.launch {
                
                val iceServers = listOf(
                    "stun:stun.l.google.com:19302",
                    "stun:stun1.l.google.com:19302",
                    "stun:stun2.l.google.com:19302",
                )
                
                val turnServers = listOf(
                    "turn:89.221.60.156:3478",
                )
                // Создание конфигурации для PeerConnection
                val rtcConfiguration = RtcConfiguration(
                    iceServers = listOf(
                        IceServer(iceServers),
                        IceServer(
                            urls = turnServers,// URL TURN сервера
                            username = "andrew", // Имя пользователя
                            password = "kapustin" // Пароль
                        )
                    )
                )
                
                // Создание PeerConnection с данной конфигурацией
                val peerConnection = PeerConnection(rtcConfiguration)
                
                setPeerConnections(peerConnection)
                
                handleWebRTCWebSocket(
                    peerConnection,
                    httpClient,
                    webSocketSession,
                    isConnected,
                    inCommingCall,
                    otherUserId,
                    callerId
                )
                
                
            }
            
            onDispose {
                job.cancel()
                scope.launch {
                    webSocketSession.value?.close()
                }
            }
        }
        
        
        LaunchedEffect(localStream, peerConnections) {
            
            
            if (peerConnections == null || localStream == null) return@LaunchedEffect
            
            println("mylog111 $peerConnections ///// ${localStream.tracks}")
            
            
            Call(
                webSocketSession,
                peerConnections,
                localStream,
                setRemoteVideoTrack,
                otherUserId,
                callerId
            )
            
        }
        
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            val localVideoTrack = localStream?.videoTracks?.firstOrNull()
            
            localVideoTrack?.let { Video(track = it, modifier = Modifier.weight(1f)) }
                ?: Box(modifier = Modifier.weight(1f))
            
            remoteVideoTrack?.let { Video(track = it, modifier = Modifier.weight(1f)) }
                ?: Box(modifier = Modifier.weight(1f))
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                
                if (
                    inCommingCall.value
                ) {
                    Button(onClick = {
                        scope.launch {
                            
                            if (peerConnections == null) return@launch
                            
                            
                            val answer =
                                peerConnections.createAnswer(
                                    options = OfferAnswerOptions(
                                        offerToReceiveVideo = true,
                                        offerToReceiveAudio = true
                                    )
                                )
                            
                            peerConnections.setLocalDescription(answer)
                            
                            if (webSocketSession.value != null) {
                                answerCall(webSocketSession.value, answer, otherUserId)
                            }
                            
                            
                        }
                    }, content = {
                        Text("inCommingCall")
                    }, modifier = Modifier.weight(0.4f))
                }
                
                if (localStream == null) {
                    Button(onClick = {
                        scope.launch {
                            
                        
                        }
                    }, content = {
                        Text("ssssss")
                    })
                    
                    return
                }
                
                
                
                StopButton(
                    onClick = {
                        hangup(peerConnections, setPeerConnections, setRemoteVideoTrack)
                        localStream.release()
                        setLocalStream(null)
                    }
                )
                
                SwitchCameraButton(
                    onClick = {
                        scope.launch { localStream.videoTracks.firstOrNull()?.switchCamera() }
                    }
                )
                
                CallButton(
                    onClick = {
                        
                        scope.launch {
                            
                            
                            if (peerConnections !== null) {
                                val offer = peerConnections.createOffer(
                                    OfferAnswerOptions(
                                        offerToReceiveVideo = true,
                                        offerToReceiveAudio = true
                                    )
                                )
                                
                                
                                peerConnections.setLocalDescription(offer)
                                
                                if (webSocketSession.value != null) {
                                    makeCall(webSocketSession.value, offer, userId)
                                }
                            }
                            
                            
                        }
                        
                        
                    },
                )
            }
        }
        
        
    }
    
}

suspend fun handleWebRTCWebSocket(
    peerConnections: PeerConnection?,
    httpClient: HttpClient,
    webSocketSession: MutableState<DefaultClientWebSocketSession?>,
    isConnected: MutableState<Boolean>,
    inCommingCall: MutableState<Boolean>,
    otherUserId: MutableState<String?>,
    callerId: MutableState<String?>,
) {
    if (!isConnected.value) {
        try {
            httpClient.webSocket(
                method = HttpMethod.Get,
                host = "192.168.31.223",
                port = 3001,
                path = "/message",
                request = {
                    callerId.value?.let { url.parameters.append("callerId", it) }
                }
            ) {
                webSocketSession.value = this
                isConnected.value = true
                
                
                val callOutputRoutine = launch {
                    
                    for (frame in incoming) {
                        if (frame is Frame.Text) {
                            val text = frame.readText()
                            val jsonElement = Json.parseToJsonElement(text)
                            val type = jsonElement.jsonObject["type"]?.jsonPrimitive?.content
                            val rtcMessage = jsonElement.jsonObject["rtcMessage"]?.jsonObject
                            
                            
                            when (type) {
                                "newCall" -> {
                                    
                                    if (rtcMessage != null) {
                                        val sdp =
                                            rtcMessage.get("sdp")?.jsonPrimitive?.content
                                                ?: return@launch
                                        
                                        
                                        val callerId =
                                            jsonElement.jsonObject["callerId"]?.jsonPrimitive?.content
                                        
                                        
                                        otherUserId.value = callerId
                                        
                                        
                                        val offer =
                                            SessionDescription(SessionDescriptionType.Offer, sdp)
                                        
                                        
                                        if (peerConnections != null) {
                                            
                                            
                                            peerConnections.setRemoteDescription(offer)
                                            
                                            
                                            inCommingCall.value = true
                                        }
                                        
                                    }
                                }
                                
                                "callAnswered" -> {
                                    
                                    if (rtcMessage == null || peerConnections == null) return@launch
                                    
                                    
                                    val sdp =
                                        rtcMessage.get("sdp")?.jsonPrimitive?.content
                                            ?: return@launch
                                    
                                    
                                    val answer =
                                        SessionDescription(SessionDescriptionType.Answer, sdp)
                                    
                                    
                                    Logger.d("rtcMessage31313 $answer")
                                    
                                    peerConnections.setRemoteDescription(answer)
                                }
                                
                                "ICEcandidate" -> {
                                    
                                    
                                    if (rtcMessage == null || peerConnections == null) return@launch
                                    
                                    
                                    Logger.d("ICEcandidate313131 $rtcMessage")
                                    
                                    
                                    val jsonElement = Json.parseToJsonElement(rtcMessage.toString())
                                    
                                    val label =
                                        jsonElement.jsonObject["label"]?.jsonPrimitive?.int
                                    val id =
                                        jsonElement.jsonObject["id"]?.jsonPrimitive?.content
                                    val candidate =
                                        jsonElement.jsonObject["candidate"]?.jsonPrimitive?.content


//                                    val dto = Json.decodeFromString(SessionDescriptionDTO.serializer(), )
                                    
                                    
                                    if (candidate == null || id == null || label == null) return@launch
                                    
                                    println(
                                        "iceCandidate222 $callerId ${
                                            IceCandidate(
                                                candidate = candidate,
                                                sdpMid = id,
                                                sdpMLineIndex = label,
                                            )
                                        }"
                                    )
                                    
                                    peerConnections.addIceCandidate(
                                        IceCandidate(
                                            candidate = candidate,
                                            sdpMid = id,
                                            sdpMLineIndex = label,
                                        )
                                    )
                                }
                            }
                        }
                    }
                    
                }
                
                callOutputRoutine.join()
            }
        } catch (e: Exception) {
            isConnected.value = false
            println("Ошибка соединения: $e")
        }
    }
}

@Composable
private fun CallButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(onClick, modifier = modifier) {
        Text("Call")
    }
}

@Composable
private fun HangupButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(onClick, modifier = modifier) {
        Text("Hangup")
    }
}

@Composable
private fun SwitchCameraButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(onClick = onClick, modifier = modifier) {
        Text("Switch Camera")
    }
}

@Composable
private fun StopButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(onClick = onClick, modifier = modifier) {
        Text("Stop")
    }
}


fun hangup(
    peerConnections: PeerConnection?,
    setPeerConnections: (PeerConnection?) -> Unit,
    setRemoteVideoTrack: (VideoStreamTrack?) -> Unit
) {
    peerConnections?.getTransceivers()?.forEach { peerConnections.removeTrack(it.sender) }
    peerConnections?.close()
    peerConnections?.close()
    setPeerConnections(null)
    setRemoteVideoTrack(null)
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
    wsSession: DefaultClientWebSocketSession?,
    offer: SessionDescription?,
    userId: String
) {
    
    
    println("offerofferoffer")
    
    if (offer == null || wsSession == null || wsSession.outgoing.isClosedForSend) {
        return
    }
    
    
    val newCallMessage = WebRTCMessage(
        type = "call",
        calleeId = userId,
        rtcMessage = SessionDescriptionDTO(offer.type, offer.sdp)
    )
    
    val jsonMessage = Json.encodeToString(WebRTCMessage.serializer(), newCallMessage)
    
    
    try {
        wsSession.send(Frame.Text(jsonMessage))
        println("Message sent successfully")
    } catch (e: Exception) {
        println("Failed to send message: ${e.message}")
    }
}

@OptIn(DelicateCoroutinesApi::class)
suspend fun answerCall(
    wsSession: DefaultClientWebSocketSession?, answer: SessionDescription?, otherUserId:
    MutableState<String?>
) {
    
    
    if (otherUserId.value == null || answer == null || wsSession == null || wsSession.outgoing.isClosedForSend) {
        return
    }
    
    
    val answerCallMessage = WebRTCMessage(
        type = "answerCall",
        callerId = otherUserId.value,
        rtcMessage = SessionDescriptionDTO(answer.type, answer.sdp)
    )
    
    val jsonMessage = Json.encodeToString(WebRTCMessage.serializer(), answerCallMessage)
    
    
    try {
        wsSession.send(Frame.Text(jsonMessage))
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


//fun serializeSessionDescription(sessionDescription: SessionDescription): String {
//
//    val dto =
//        SessionDescriptionDTO(type = sessionDescription.type.name, sdp = sessionDescription.sdp)
//    return Json.encodeToString(SessionDescriptionDTO.serializer(), dto)
//}
//
//fun deserializeSessionDescription(jsonString: String): SessionDescription {
//    val dto = Json.decodeFromString(SessionDescriptionDTO.serializer(), jsonString)
//    val type = SessionDescriptionType.valueOf(dto.type)
//    return SessionDescription(type, dto.sdp)
//}

