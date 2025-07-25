package com.shanudevcodes.newsbits.data.savedarticledb.domain.usecase

import com.shanudevcodes.newsbits.data.savedarticledb.domain.modal.Article
import com.shanudevcodes.newsbits.data.savedarticledb.domain.modal.SearchHistory
import com.shanudevcodes.newsbits.data.savedarticledb.domain.repository.ArticleRepository
import kotlinx.coroutines.flow.Flow

class SaveArticleUseCase(private val repository: ArticleRepository) {
    suspend operator fun invoke(article: Article) = repository.saveArticle(article)
}

class GetArticlesUseCase(private val repository: ArticleRepository) {
    operator fun invoke(): Flow<List<Article>> = repository.getArticles()
}

class DeleteArticleUseCase(private val repository: ArticleRepository) {
    suspend operator fun invoke(article: Article) = repository.deleteArticle(article)
}

class DeleteArticleByIdUseCase(private val repository: ArticleRepository) {
    suspend operator fun invoke(id: String) = repository.deleteArticleById(id)
}

class GetArticleByIdUseCase(private val repository: ArticleRepository) {
    operator fun invoke(id: String): Flow<Article?> = repository.getArticleById(id)
}

class IsArticleSavedUseCase(private val repository: ArticleRepository) {
    suspend operator fun invoke(id: String): Boolean = repository.isArticleSaved(id)
}

class SaveHistoryUseCase(private val repository: ArticleRepository) {
    suspend operator fun invoke(query: String) = repository.saveHistory(query)
}

class GetHistoryUseCase(private val repository: ArticleRepository) {
    operator fun invoke(): Flow<List<SearchHistory>> = repository.getHistory()
}

class DeleteHistoryUseCase(private val repository: ArticleRepository) {
    suspend operator fun invoke(history: SearchHistory) = repository.deleteHistory(history)
}