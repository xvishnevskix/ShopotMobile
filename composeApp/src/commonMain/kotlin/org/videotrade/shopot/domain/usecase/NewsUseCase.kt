package org.videotrade.shopot.domain.usecase

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.videotrade.shopot.domain.model.NewsItem
import org.videotrade.shopot.domain.repository.NewsRepository

class NewsUseCase : KoinComponent {
    private val repository: NewsRepository by inject()

    suspend fun getNewsByAppearance(appearance: String): List<NewsItem>? {
        return repository.getNewsByAppearance(appearance)
    }

    suspend fun markNewsAsViewed(newsId: String): Boolean {
        return repository.markNewsAsViewed(newsId)
    }
}

