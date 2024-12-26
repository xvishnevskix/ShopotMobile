package org.videotrade.shopot.data.remote.repository

import kotlinx.coroutines.flow.MutableStateFlow
import org.videotrade.shopot.data.origin
import org.videotrade.shopot.domain.model.NewsItem
import org.videotrade.shopot.domain.model.NewsResponse
import org.videotrade.shopot.domain.repository.NewsRepository

class NewsRepositoryImpl : NewsRepository {
    private val newsFlow = MutableStateFlow<List<NewsItem>>(emptyList())

    override suspend fun getNewsByAppearance(appearance: String): List<NewsItem>? {
        val originInstance = origin()
        val url = "news/appearance/$appearance"

        return try {
            originInstance.get<NewsResponse>(url)?.news ?: run {
                println("Response is null")
                null
            }
        } catch (e: Exception) {
            println("Error fetching news: ${e.message}")
            null
        }
    }

    override suspend fun markNewsAsViewed(newsId: String): Boolean {
        return try {
            val originInstance = origin()
            val url = "news/viewed?newsId=$newsId"
            originInstance.post(url, null.toString()) != null
        } catch (e: Exception) {
            println("Error marking news as viewed: ${e.message}")
            false
        }
    }

    override fun clearData() {
        newsFlow.value = emptyList()
    }
}

