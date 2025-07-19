package com.shanudevcodes.newsbits.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shanudevcodes.newsbits.data.NewsArticle
import com.shanudevcodes.newsbits.data.fetchNewsByLink
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SearchResultDetailScreenViewModel: ViewModel() {
    private val _news = MutableStateFlow<NewsArticle?>(null)
    val news: MutableStateFlow<NewsArticle?> = _news
    private val _isNewsFetched = MutableStateFlow(false)
    val isNewsFetched = _isNewsFetched

    fun fetchNews(link: String){
        viewModelScope.launch {
            val result = fetchNewsByLink(link)
            _news.value = result
            _isNewsFetched.value = true
        }
    }
}