package org.videotrade.shopot.domain.model

import com.shepeliev.webrtckmp.SessionDescriptionType
import kotlinx.serialization.Serializable

@Serializable
data class WebRTCMessage(
    val type: String,
    val calleeId: String? = null,
    val callerId: String? = null,
    val userId: String? = null,
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




@Serializable
data class SessionDescriptionDTO(
    val type: SessionDescriptionType,
    val sdp: String
)