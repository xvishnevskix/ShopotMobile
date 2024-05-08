package org.videotrade.shopot.domain.model


data class UserItem(
    val id: String,
    val isPersonal: Boolean,
    var icon: String,
    var firstName: String,
    var lastName: String,
    var phone: String,
    var unread: Int,
    var notificationToken: String,
    var lastMessage: String,
)
