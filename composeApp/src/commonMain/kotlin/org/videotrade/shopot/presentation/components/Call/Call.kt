import androidx.compose.runtime.MutableState
import co.touchlab.kermit.Logger
import com.shepeliev.webrtckmp.IceCandidate
import com.shepeliev.webrtckmp.MediaStream
import com.shepeliev.webrtckmp.MediaStreamTrackKind
import com.shepeliev.webrtckmp.PeerConnection
import com.shepeliev.webrtckmp.SignalingState
import com.shepeliev.webrtckmp.VideoStreamTrack
import com.shepeliev.webrtckmp.onConnectionStateChange
import com.shepeliev.webrtckmp.onIceCandidate
import com.shepeliev.webrtckmp.onIceConnectionStateChange
import com.shepeliev.webrtckmp.onSignalingStateChange
import com.shepeliev.webrtckmp.onTrack
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.websocket.Frame
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.json.Json
import org.videotrade.shopot.presentation.screens.call.WebRTCMessage
import org.videotrade.shopot.presentation.screens.call.rtcMessageDTO

suspend fun Call(
    webSocketSession: MutableState<DefaultClientWebSocketSession?>,
    peerConnection: PeerConnection,
    localStream: MediaStream,
    setRemoteVideoTrack: (VideoStreamTrack?) -> Unit,
    otherUserId: MutableState<String?>,
    callerId: MutableState<String?>,
    
    
    ): Nothing = coroutineScope {
    localStream.tracks.forEach { track ->
        peerConnection.addTrack(track)
    }
    
//    val pсIceCandidates = mutableListOf<IceCandidate>()
    
    
    // Обработка кандидатов ICE
    peerConnection.onIceCandidate
        .onEach { candidate ->
            Logger.d { "onIceCandidate22: ${candidate.candidate}" }
            
//            val rtcMessage = Json.encodeToString(
//                rtcMessageDTO.serializer(), rtcMessageDTO(
//                    label = candidate.sdpMLineIndex,
//                    id = candidate.sdpMid,
//                    candidate = candidate.candidate,
//                )
//            )
            
//
//            val iceCandidateMessage = WebRTCMessage(
//                type = "ICEcandidate",
//                calleeId = otherUserId.value,
//                rtcMessage = rtcMessageDTO(
//                    label = candidate.sdpMLineIndex,
//                    id = candidate.sdpMid,
//                    candidate = candidate.candidate,
//                ),
//            )
//
//            val jsonMessage = Json.encodeToString(WebRTCMessage.serializer(), iceCandidateMessage)
//
//
//            try {
//                webSocketSession.value?.send(Frame.Text(jsonMessage))
//                println("Message sent successfully")
//            } catch (e: Exception) {
//                println("Failed to send message: ${e.message}")
//            }
            
            peerConnection.addIceCandidate(candidate)
            
            
        }
        .launchIn(this)
    
    // Следим за изменениями состояния сигнализации
    peerConnection.onSignalingStateChange
        .onEach { signalingState ->
            Logger.d { "onSignalingStateChange: $signalingState" }
        }
        .launchIn(this)
    
    // Следим за изменениями состояния соединения ICE
    peerConnection.onIceConnectionStateChange
        .onEach { state ->
            Logger.d { "onIceConnectionStateChange: $state" }
        }
        .launchIn(this)
    
    // Следим за изменениями общего состояния соединения
    peerConnection.onConnectionStateChange
        .onEach { state ->
            Logger.d { "onConnectionStateChange: $state" }
        }
        .launchIn(this)
    
    // Обработка треков, получаемых от удалённого пира
    peerConnection.onTrack
        .onEach { event ->
            Logger.d { "onTrack: ${event.track}" }
            if (event.track?.kind == MediaStreamTrackKind.Video) {
                setRemoteVideoTrack(event.track as VideoStreamTrack)
            }
        }
        .launchIn(this)
    
    // Создаем предложение и устанавливаем его как локальное описание
//    val offer = peerConnection.createOffer(OfferAnswerOptions(offerToReceiveVideo = true, offerToReceiveAudio = true))
//    peerConnection.setLocalDescription(offer)
    
    // Отправляем описание на сервер или другому пиру, который должен ответить
    // Здесь нужен код для отправки описания через сигнализационный сервер или канал
    
    // В реальной ситуации, здесь нужно обрабатывать ответ от удалённого пира, когда он приходит
    // peerConnection.setRemoteDescription(receivedAnswer)
    
    awaitCancellation()  // Поддерживаем корутину активной
}
