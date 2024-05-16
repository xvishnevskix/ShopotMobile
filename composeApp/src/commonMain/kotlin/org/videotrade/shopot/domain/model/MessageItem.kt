package org.videotrade.shopot.domain.model


data class MessageItem(
    var id: String,
    val text: String,
    val isPersonal: Boolean,
    var icon: String,
    var firstName: String,
    var lastName: String,
    var phone: String,
    var notificationToken: String,
)
