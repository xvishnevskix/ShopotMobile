package org.videotrade.shopot.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class MessageItem(
    val id: String,
    val fromUser: String,
    val content: String,
    val forwardMessage: String?,
    var answerMessage: String?,
    var replaces: Int,
    var created:  List<Int>?,
    var isDeleted: Boolean,
    var chatId: String,
    
    
    )