package com.shanudevcodes.newsbits.data.savedarticledb

import com.shanudevcodes.newsbits.data.NewsArticle

sealed interface RoomEvents {
    data class SaveArticle(val article: NewsArticle): RoomEvents
    data class DeleteArticle(val article: NewsArticle): RoomEvents
    data object GetArticles: RoomEvents
    data class CheckArticleSaved(val article: NewsArticle): RoomEvents
}