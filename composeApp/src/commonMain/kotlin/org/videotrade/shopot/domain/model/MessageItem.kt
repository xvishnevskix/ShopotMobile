package org.videotrade.shopot.domain.model

import androidx.compose.ui.geometry.Size
import kotlinx.serialization.Serializable

@Serializable
data class MessageItem(
    val id: String = "",
    val fromUser: String = "",
    val content: String?,
    val forwardMessage: Boolean? = false,
    var answerMessage: MessageItem? = null,
    var replaces: Int? = null,
    var created: List<Int> = emptyList(),
    var isDeleted: Boolean = false,
    var chatId: String,
    var anotherRead: Boolean,
    var iread: Boolean,
    var attachments: List<Attachment>?,
    var phone: String? = null,
    var upload: Boolean? = null,
    var uploadId: String? = null,
)


@Serializable
data class Attachment(
    val id: String = "",
    val messageId: String = "",
    val userId: String,
    val fileId: String = "",
    var type: String,
    var name: String,
    var originalFileDir: String? = null,
    var photoPath: String? = null,
    var photoName: String? = null,
    var photoByteArray: ByteArray? = null,
    var size: Long? = null,

)





