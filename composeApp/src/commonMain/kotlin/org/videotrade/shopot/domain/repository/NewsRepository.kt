package org.videotrade.shopot.domain.repository

import org.videotrade.shopot.domain.model.NewsItem

interface NewsRepository {
    suspend fun getNewsByAppearance(appearance: String): List<NewsItem>?
    suspend fun markNewsAsViewed(newsId: String): Boolean
    fun clearData()
}
