package com.shanudevcodes.newsbits.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.shanudevcodes.newsbits.data.NewsArticle
import com.shanudevcodes.newsbits.data.fetchTopNews
import com.shanudevcodes.newsbits.data.getNewsPagingFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() {

    private val _isNewsLoaded = MutableStateFlow(false)
    val isNewsLoaded: StateFlow<Boolean> = _isNewsLoaded

    private val _topNews = MutableStateFlow<List<NewsArticle>>(emptyList())
    val topNews: StateFlow<List<NewsArticle>> = _topNews

    val allNewsPagingFlow: Flow<PagingData<NewsArticle>> = getNewsPagingFlow().cachedIn(viewModelScope)

    fun loadTopNews(){
        viewModelScope.launch {
            _topNews.value = fetchTopNews()
        }
    }

    fun newsLoaded(){
        _isNewsLoaded.value = true
    }
}