package org.videotrade.shopot.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewsItem(
    val id: String,
    val imageIds: List<String>,
    val actionUrl: String,
    val appearance: String,
    val viewed: Boolean,
    val version: String,
    val duration: Long,
    val buttonText: String
)

@Serializable
data class NewsResponse(
    @SerialName("news")
    val news: List<NewsItem>
)