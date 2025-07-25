package com.shanudevcodes.newsbits.data.savedarticledb.data.repository

import com.shanudevcodes.newsbits.data.savedarticledb.data.dao.RoomDao
import com.shanudevcodes.newsbits.data.savedarticledb.data.entity.History
import com.shanudevcodes.newsbits.data.savedarticledb.data.mapper.toDomain
import com.shanudevcodes.newsbits.data.savedarticledb.data.mapper.toEntity
import com.shanudevcodes.newsbits.data.savedarticledb.domain.modal.Article
import com.shanudevcodes.newsbits.data.savedarticledb.domain.modal.SearchHistory
import com.shanudevcodes.newsbits.data.savedarticledb.domain.repository.ArticleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// data/repository/ArticleRepositoryImpl.kt
class ArticleRepositoryImpl(
    private val dao: RoomDao
): ArticleRepository {
    override suspend fun saveArticle(article: Article) {
        dao.upsertArticle(article.toEntity())
    }

    override suspend fun deleteArticle(article: Article) {
        dao.deleteArticle(article.toEntity())
    }

    override suspend fun deleteArticleById(id: String) {
        dao.deleteArticleById(id)
    }

    override fun getArticles(): Flow<List<Article>> {
        return dao.getArticles().map { list -> list.map { it.toDomain() } }
    }

    override fun getArticleById(id: String): Flow<Article?> {
        return dao.getArticleById(id).map { it?.toDomain() }
    }

    override suspend fun isArticleSaved(id: String): Boolean {
        return dao.checkArticleSaved(id)
    }

    override fun getHistory(): Flow<List<SearchHistory>> {
        return dao.getHistory().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun saveHistory(query: String) {
        dao.upsertHistory(History(query = query))
    }

    override suspend fun deleteHistory(history: SearchHistory) {
        dao.deleteHistory(history.toEntity())
    }
}
