package org.videotrade.shopot.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatItem(
    val id: String,
    val personal: Boolean,
    var icon: String?,
    var firstName: String,
    var lastName: String,
    var phone: String,
    var unread: Int,
    var notificationToken: String?,
    var lastMessage: MessageItem?,
    var chatId: String,
    var userId: String,
)



//@Serializable
//data class ChatItem(
//    val id: String,
//    val firstUserId: String,,...
//    val secondUserId: String,
//    val createdAt: Long,
//
//    )

