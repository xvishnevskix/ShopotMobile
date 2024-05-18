package org.videotrade.shopot.multiplatform

import kotlinx.serialization.Serializable

@Serializable
data class Contact(val name: String, val phoneNumber: String)
