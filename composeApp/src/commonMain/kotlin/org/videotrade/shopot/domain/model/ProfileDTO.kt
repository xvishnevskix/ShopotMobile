package org.videotrade.shopot.domain.model


data class ProfileDTO(
    val id: String,
    var icon: String,
    var firstName: String,
    var lastName: String,
    var phone: String,
)
