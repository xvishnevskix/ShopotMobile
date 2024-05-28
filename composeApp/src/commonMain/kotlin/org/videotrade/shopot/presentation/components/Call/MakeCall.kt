import com.shepeliev.webrtckmp.SessionDescription
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.websocket.Frame
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.serialization.json.Json
import org.videotrade.shopot.domain.model.SessionDescriptionDTO
import org.videotrade.shopot.domain.model.WebRTCMessage


@OptIn(DelicateCoroutinesApi::class)
suspend fun MakeCall(
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