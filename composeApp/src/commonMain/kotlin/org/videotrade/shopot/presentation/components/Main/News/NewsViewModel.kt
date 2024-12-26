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

    private val _updateNews = MutableStateFlow<List<NewsItem>>(emptyList()) // Для update
    val updateNews: StateFlow<List<NewsItem>> get() = _updateNews

    private val _isLoadingActualNews = MutableStateFlow(false)
    val isLoadingActualNews: StateFlow<Boolean> get() = _isLoadingActualNews

    private val _isLoadingUpdateNews = MutableStateFlow(false)
    val isLoadingUpdateNews: StateFlow<Boolean> get() = _isLoadingUpdateNews

    fun getNewsByAppearance(appearance: String) {
        val isLoadingState = when (appearance) {
            "actual" -> _isLoadingActualNews
            "update" -> _isLoadingUpdateNews
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
                    "update" -> _updateNews.value = fetchedNews ?: emptyList()
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
                _updateNews.value = _updateNews.value.map {
                    if (it.id == newsId) it.copy(viewed = true) else it
                }
            }
        }
    }
}

