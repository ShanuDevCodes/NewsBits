package com.shanudevcodes.newsbits.data.savedarticledb.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.shanudevcodes.newsbits.data.savedarticledb.data.entity.History
import com.shanudevcodes.newsbits.data.savedarticledb.data.entity.SavedArticle
import kotlinx.coroutines.flow.Flow

@Dao
interface RoomDao {

    @Upsert
    suspend fun upsertArticle(article: SavedArticle)

    @Delete
    suspend fun deleteArticle(article: SavedArticle)

    @Query("DELETE FROM SavedArticle WHERE article_id = :article_Id")
    suspend fun deleteArticleById(article_Id: String)

    @Query("SELECT *, ROWID FROM SavedArticle ORDER BY ROWID DESC")
    fun getArticles(): Flow<List<SavedArticle>>

    @Query("SELECT * FROM SavedArticle WHERE article_id = :article_Id")
    fun getArticleById(article_Id: String): Flow<SavedArticle?>

    @Query("SELECT EXISTS(SELECT * FROM SavedArticle WHERE article_id = :article_Id)")
    suspend fun checkArticleSaved(article_Id: String): Boolean

    @Upsert
    suspend fun upsertHistory(history: History)

    @Delete
    suspend fun deleteHistory(history: History)

    @Query("SELECT * FROM History ORDER BY ROWID DESC")
    fun getHistory(): Flow<List<History>>

}