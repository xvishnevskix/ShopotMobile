package org.videotrade.shopot.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class MessageItem(
    val id: String = "",
    val fromUser: String = "",
    val content: String? ,
    val forwardMessage: String? = "",
    var answerMessage: String? = "",
    var replaces: Int? = null,
    var created: List<Int> = emptyList(),
    var isDeleted: Boolean = false,
    var chatId: String,
    var anotherRead: Boolean,
    var iread: Boolean,
    var attachments: List<String>?,
)