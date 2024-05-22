package org.videotrade.shopot.domain.model

import kotlinx.serialization.Serializable


//data class ChatItem(
//    val id: String,
//    val isPersonal: Boolean,
//    var icon: String,
//    var firstName: String,
//    var lastName: String,
//    var phone: String,
//    var unread: Int,
//    var notificationToken: String,
//    var lastMessage: String,
//    var chatId: String,
//)


@Serializable
data class ChatItem(
    val id: String,
    val firstUserId: String,
    val secondUserId: String,
    val createdAt: Long,
    
    )

