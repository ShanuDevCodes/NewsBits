package com.shanudevcodes.newsbits.feature.news.domain.repository

import androidx.paging.PagingData
import com.shanudevcodes.newsbits.data.NewsArticle
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    fun getNewsPagingFlow(category: String?): Flow<PagingData<NewsArticle>>
    suspend fun fetchTopNews(limit: Long = 10): List<NewsArticle>
    suspend fun fetchNewsByLink(link: String): NewsArticle?
}
