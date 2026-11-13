package com.luna.ai.feature.headlines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HeadlinesViewModel @Inject constructor(
    private val newsService: NewsService
) : ViewModel() {

    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    val articles: StateFlow<List<Article>> = _articles

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        fetchNews()
    }

    fun fetchNews() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = newsService.getTopHeadlines()
                _articles.value = response.articles
            } catch (e: Exception) {
                _error.value = "খবর আনতে সমস্যা হয়েছে: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
