package com.shanudevcodes.newsbits.feature.news.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.firestore.FirebaseFirestore
import com.shanudevcodes.newsbits.data.FirestorePagingSource
import com.shanudevcodes.newsbits.data.NewsArticle
import com.shanudevcodes.newsbits.feature.news.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.net.URLEncoder
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : NewsRepository {

    override fun getNewsPagingFlow(category: String?): Flow<PagingData<NewsArticle>> {
        val collection = firestore.collection("news_all")
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 5,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                FirestorePagingSource(collection, categoryFilter = category)
            }
        ).flow
    }

    override suspend fun fetchTopNews(limit: Long): List<NewsArticle> {
        return try {
            val snapshot = firestore.collection("news_top")
                .orderBy("pubDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .await()
            snapshot.documents.mapNotNull { it.toObject(NewsArticle::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun fetchNewsByLink(link: String): NewsArticle? {
        return try {
            val encodedLink = URLEncoder.encode(link, "UTF-8")
            val snapshot = firestore.collection("news_all")
                .document(encodedLink)
                .get()
                .await()
            if (snapshot.exists()) snapshot.toObject(NewsArticle::class.java) else null
        } catch (e: Exception) {
            null
        }
    }
}
