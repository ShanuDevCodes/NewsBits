package com.shanudevcodes.newsbits.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shanudevcodes.newsbits.data.NewsArticle
import com.shanudevcodes.newsbits.feature.news.domain.usecase.GetNewsByLinkUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchResultDetailScreenViewModel @Inject constructor(
    private val getNewsByLinkUseCase: GetNewsByLinkUseCase
) : ViewModel() {

    private val _news = MutableStateFlow<NewsArticle?>(null)
    val news: MutableStateFlow<NewsArticle?> = _news

    private val _isNewsFetched = MutableStateFlow(false)
    val isNewsFetched = _isNewsFetched

    private var lastFetchedLink = ""

    fun fetchNews(link: String) {
        if (link == lastFetchedLink) return
        lastFetchedLink = link
        viewModelScope.launch {
            _isNewsFetched.value = false
            val result = getNewsByLinkUseCase(link)
            _news.value = result
            _isNewsFetched.value = true
        }
    }
}