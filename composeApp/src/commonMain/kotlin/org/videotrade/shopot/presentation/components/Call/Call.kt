import androidx.compose.runtime.MutableState
import co.touchlab.kermit.Logger
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
import org.videotrade.shopot.presentation.components.Main.WebRTCMessage
import org.videotrade.shopot.presentation.components.Main.rtcMessageDTO


suspend fun Call(
    webSocketSession: MutableState<DefaultClientWebSocketSession?>,
    peerConnection: PeerConnection,
    localStream: MediaStream,
    setRemoteVideoTrack: (VideoStreamTrack?) -> Unit,
    otherUserId: MutableState<String?>,
    callerId: MutableState<String?>,
    
    
    ): Nothing = coroutineScope {
    localStream.tracks.forEach { track ->
        
        
        peerConnection.addTrack(track, localStream)
    }

//    val pсIceCandidates = mutableListOf<IceCandidate>()
    
    
    // Обработка кандидатов ICE
    peerConnection.onIceCandidate
        .onEach { candidate ->
//            Logger.d { "PC1 onIceCandidate22:${callerId.value} ${otherUserId.value}" }
            
            val iceCandidateMessage = WebRTCMessage(
                type = "ICEcandidate",
                calleeId = otherUserId.value,
                iceMessage = rtcMessageDTO(
                    label = candidate.sdpMLineIndex,
                    id = candidate.sdpMid,
                    candidate = candidate.candidate,
                ),
            )
            
            val jsonMessage = Json.encodeToString(WebRTCMessage.serializer(), iceCandidateMessage)
            
            
            try {
                webSocketSession.value?.send(Frame.Text(jsonMessage))
                println("Message sent successfully")
            } catch (e: Exception) {
                println("Failed to send message: ${e.message}")
            }
            
            peerConnection.addIceCandidate(candidate)
            
            
        }
        .launchIn(this)
    
    // Следим за изменениями состояния сигнализации
    peerConnection.onSignalingStateChange
        .onEach { signalingState ->
            Logger.d { "PC1 onSignalingStateChange: $signalingState" }
            
            if (signalingState == SignalingState.HaveRemoteOffer) {
                Logger.d { " peer2 signalingState: $signalingState" }
                
            }
        }
        .launchIn(this)
    
    // Следим за изменениями состояния соединения ICE
    peerConnection.onIceConnectionStateChange
        .onEach { state ->
            Logger.d { "PC1 onIceConnectionStateChange: $state" }
            
            
        }
        .launchIn(this)
    
    // Следим за изменениями общего состояния соединения
    peerConnection.onConnectionStateChange
        .onEach { state ->
            Logger.d { "PC1 onConnectionStateChange: $state" }
        }
        .launchIn(this)
    
    // Обработка треков, получаемых от удалённого пира
    peerConnection.onTrack
        .onEach { event ->
            Logger.d { "onTrack: $  ${event.track} ${event.streams} ${event.receiver} ${event.transceiver}" }
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
