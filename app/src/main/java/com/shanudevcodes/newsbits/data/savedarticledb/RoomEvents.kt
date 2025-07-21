package com.shanudevcodes.newsbits.data.savedarticledb

import com.shanudevcodes.newsbits.data.NewsArticle

sealed interface RoomEvents {
    data class SaveArticle(val article: NewsArticle?): RoomEvents
    data class UpdateBookMarkedArticle(val article: SavedArticle): RoomEvents
    data class DeleteArticle(val article: NewsArticle?): RoomEvents
    data object GetArticles: RoomEvents
    data class CheckArticleSaved(val articleId: String): RoomEvents
    data class GetArticleById(val articleId: String): RoomEvents
    data class DeleteArticleById(val articleId: String): RoomEvents
    data class SetHistoryQuery(val query: String): RoomEvents
    data object GetHistory: RoomEvents
    data class DeleteHistory(val history: History): RoomEvents
    data object UpsertHistory: RoomEvents
    data class SaveHistory(val query: String) : RoomEvents
}