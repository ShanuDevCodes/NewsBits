package com.shanudevcodes.newsbits.feature.news.domain.usecase

import com.shanudevcodes.newsbits.data.NewsArticle
import com.shanudevcodes.newsbits.feature.news.domain.repository.NewsRepository
import javax.inject.Inject

class GetTopNewsUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    suspend operator fun invoke(limit: Long = 10): List<NewsArticle> =
        repository.fetchTopNews(limit)
}

class GetNewsByLinkUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    suspend operator fun invoke(link: String): NewsArticle? =
        repository.fetchNewsByLink(link)
}
