package org.videotrade.shopot.api

import androidx.compose.runtime.MutableState
import co.touchlab.kermit.Logger
import com.shepeliev.webrtckmp.IceCandidate
import com.shepeliev.webrtckmp.PeerConnection
import com.shepeliev.webrtckmp.SessionDescription
import com.shepeliev.webrtckmp.SessionDescriptionType
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.http.HttpMethod
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

suspend fun handleWebRTCWebSocket(
    webSocketSession: MutableState<DefaultClientWebSocketSession?>,
    isConnected: MutableState<Boolean>,
  
) {
    val httpClient = HttpClient {
        install(WebSockets)
        
    }
    
    
//    if (!isConnected.value) {
//        try {
//            httpClient.webSocket(
//                method = HttpMethod.Get,
//                host = "192.168.31.223",
//                port = 3001,
//                path = "/message",
//                request = {
//                    callerId.value?.let { url.parameters.append("callerId", it) }
//                }
//            ) {
//                webSocketSession.value = this
//                isConnected.value = true
//
//
//                val callOutputRoutine = launch {
//
//                    for (frame in incoming) {
//                        if (frame is Frame.Text) {
//                            val text = frame.readText()
//                            val jsonElement = Json.parseToJsonElement(text)
//                            val type = jsonElement.jsonObject["type"]?.jsonPrimitive?.content
//                            val rtcMessage = jsonElement.jsonObject["rtcMessage"]?.jsonObject
//
//
//                            when (type) {
//                                "newCall" -> {
//
//                                    if (rtcMessage != null) {
//                                        val sdp =
//                                            rtcMessage.get("sdp")?.jsonPrimitive?.content
//                                                ?: return@launch
//
//
//                                        val callerId =
//                                            jsonElement.jsonObject["callerId"]?.jsonPrimitive?.content
//
//
//                                        otherUserId.value = callerId
//
//
//                                        val offer =
//                                            SessionDescription(SessionDescriptionType.Offer, sdp)
//
//
//                                        if (peerConnections != null) {
//
//
//                                            peerConnections.setRemoteDescription(offer)
//
//
//                                            inCommingCall.value = true
//                                        }
//
//                                    }
//                                }
//
//                                "callAnswered" -> {
//
//                                    if (rtcMessage == null || peerConnections == null) return@launch
//
//
//                                    val sdp =
//                                        rtcMessage.get("sdp")?.jsonPrimitive?.content
//                                            ?: return@launch
//
//
//                                    val answer =
//                                        SessionDescription(SessionDescriptionType.Answer, sdp)
//
//
//                                    Logger.d("rtcMessage31313 $answer")
//
//                                    peerConnections.setRemoteDescription(answer)
//                                }
//
//                                "ICEcandidate" -> {
//
//
//                                    if (rtcMessage == null || peerConnections == null) return@launch
//
//
//                                    Logger.d("ICEcandidate313131 $rtcMessage")
//
//
//                                    val jsonElement = Json.parseToJsonElement(rtcMessage.toString())
//
//                                    val label =
//                                        jsonElement.jsonObject["label"]?.jsonPrimitive?.int
//                                    val id =
//                                        jsonElement.jsonObject["id"]?.jsonPrimitive?.content
//                                    val candidate =
//                                        jsonElement.jsonObject["candidate"]?.jsonPrimitive?.content
//
//
////                                    val dto = Json.decodeFromString(SessionDescriptionDTO.serializer(), )
//
//
//                                    if (candidate == null || id == null || label == null) return@launch
//
//                                    println(
//                                        "iceCandidate222 $callerId ${
//                                            IceCandidate(
//                                                candidate = candidate,
//                                                sdpMid = id,
//                                                sdpMLineIndex = label,
//                                            )
//                                        }"
//                                    )
//
//                                    peerConnections.addIceCandidate(
//                                        IceCandidate(
//                                            candidate = candidate,
//                                            sdpMid = id,
//                                            sdpMLineIndex = label,
//                                        )
//                                    )
//                                }
//                            }
//                        }
//                    }
//
//                }
//
//                callOutputRoutine.join()
//            }
//        } catch (e: Exception) {
//            isConnected.value = false
//            println("Ошибка соединения: $e")
//        }
//    }
}