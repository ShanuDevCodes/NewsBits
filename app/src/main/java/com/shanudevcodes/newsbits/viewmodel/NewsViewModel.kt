package com.shanudevcodes.newsbits.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shanudevcodes.newsbits.data.NewsArticle
import com.shanudevcodes.newsbits.data.fetchAllNews
import com.shanudevcodes.newsbits.data.fetchTopNews
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() {

    private val _allNews = MutableStateFlow<List<NewsArticle>>(emptyList())
    val allNews: StateFlow<List<NewsArticle>> = _allNews

    private val _topNews = MutableStateFlow<List<NewsArticle>>(emptyList())
    val topNews: StateFlow<List<NewsArticle>> = _topNews

    fun loadAllNews() {
        viewModelScope.launch {
            _allNews.value = fetchAllNews()
        }
    }

    fun loadTopNews(){
        viewModelScope.launch {
            _topNews.value = fetchTopNews()
        }
    }
}