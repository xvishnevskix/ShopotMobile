package org.videotrade.shopot.presentation.components.Main.News

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.videotrade.shopot.domain.model.NewsItem
import org.videotrade.shopot.domain.usecase.NewsUseCase

class NewsViewModel : ViewModel(), KoinComponent {
    private val newsUseCase = NewsUseCase()

    private val _news = MutableStateFlow<List<NewsItem>>(emptyList()) // Для actual
    val news: StateFlow<List<NewsItem>> get() = _news

    private val _onceNews = MutableStateFlow<List<NewsItem>>(emptyList()) // Для once
    val onceNews: StateFlow<List<NewsItem>> get() = _onceNews

    private val _isLoadingActualNews = MutableStateFlow(false)
    val isLoadingActualNews: StateFlow<Boolean> get() = _isLoadingActualNews

    private val _isLoadingOnceNews = MutableStateFlow(false)
    val isLoadingOnceNews: StateFlow<Boolean> get() = _isLoadingOnceNews

    fun getNewsByAppearance(appearance: String) {
        val isLoadingState = when (appearance) {
            "actual" -> _isLoadingActualNews
            "once" -> _isLoadingOnceNews
            else -> return
        }

        if (isLoadingState.value) return // Если уже идет загрузка, выходим

        isLoadingState.value = true
        viewModelScope.launch {
            try {
                val fetchedNews = withContext(Dispatchers.IO) {
                    newsUseCase.getNewsByAppearance(appearance)
                }
                when (appearance) {
                    "actual" -> _news.value = fetchedNews ?: emptyList()
                    "once" -> _onceNews.value = fetchedNews ?: emptyList()
                }
            } catch (e: Exception) {
                println("Error fetching $appearance news: ${e.message}")
            } finally {
                isLoadingState.value = false
            }
        }
    }

    fun markNewsAsViewed(newsId: String) {
        viewModelScope.launch {
            val success = newsUseCase.markNewsAsViewed(newsId)
            if (success) {
                _news.value = _news.value.map {
                    if (it.id == newsId) it.copy(viewed = true) else it
                }
                _onceNews.value = _onceNews.value.map {
                    if (it.id == newsId) it.copy(viewed = true) else it
                }
            }
        }
    }
}

