package com.shanudevcodes.newsbits.data.savedarticledb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface RoomDao {

    @Upsert
    suspend fun upsertArticle(article: SavedArticle)

    @Delete
    suspend fun deleteArticle(article: SavedArticle)

    @Query("SELECT * FROM SavedArticle")
    fun getArticles(): Flow<List<SavedArticle>>

    @Query("SELECT EXISTS(SELECT * FROM SavedArticle WHERE article_id = :article_Id)")
    suspend fun checkArticleSaved(article_Id: String): Boolean
}