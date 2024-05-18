package org.videotrade.shopot.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ContactDTO(val name: String, val phone: String)
