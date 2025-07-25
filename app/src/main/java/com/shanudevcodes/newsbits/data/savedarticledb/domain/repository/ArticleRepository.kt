package com.shanudevcodes.newsbits.data.savedarticledb.domain.repository

import com.shanudevcodes.newsbits.data.savedarticledb.domain.modal.Article
import com.shanudevcodes.newsbits.data.savedarticledb.domain.modal.SearchHistory
import kotlinx.coroutines.flow.Flow

interface ArticleRepository {
    suspend fun saveArticle(article: Article)
    suspend fun deleteArticle(article: Article)
    suspend fun deleteArticleById(id: String)
    fun getArticles(): Flow<List<Article>>
    fun getArticleById(id: String): Flow<Article?>
    suspend fun isArticleSaved(id: String): Boolean

    fun getHistory(): Flow<List<SearchHistory>>
    suspend fun saveHistory(query: String)
    suspend fun deleteHistory(history: SearchHistory)
}