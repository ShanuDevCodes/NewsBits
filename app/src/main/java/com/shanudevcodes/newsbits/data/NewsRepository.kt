package com.shanudevcodes.newsbits.data


import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Query.Direction.DESCENDING
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

fun getNewsPagingFlow(): Flow<PagingData<NewsArticle>> {
    val collection = FirebaseFirestore.getInstance()
        .collection("news_all")

    return Pager(
        config = PagingConfig(
            pageSize = 20,
            prefetchDistance = 5,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            FirestorePagingSource(collection, pageSize = 20)
        }
    ).flow
}

suspend fun fetchTopNews(limit: Long = 10): List<NewsArticle> {
    val db = FirebaseFirestore.getInstance()
    val snapshot = db.collection("news_top")
        .orderBy("pubDate", DESCENDING)
        .limit(limit)
        .get()
        .await()
    return snapshot.documents.mapNotNull {doc->
        doc.toObject(NewsArticle::class.java)
    }
}

class FirestorePagingSource(
    private val query: CollectionReference,
    private val pageSize: Long = 20
) : PagingSource<DocumentSnapshot, NewsArticle>() {

    override suspend fun load(params: LoadParams<DocumentSnapshot>): LoadResult<DocumentSnapshot, NewsArticle> {
        return try {
            val currentKey = params.key

            // Initial page (no cursor)
            val currentQuery = if (currentKey == null) {
                query.orderBy("pubDate", Query.Direction.DESCENDING)
                    .limit(pageSize)
            } else {
                query.orderBy("pubDate", Query.Direction.DESCENDING)
                    .startAfter(currentKey)
                    .limit(pageSize)
            }

            val snapshot = currentQuery.get().await()
            val documents = snapshot.documents

            val items = documents.mapNotNull { it.toObject(NewsArticle::class.java) }
            val nextKey = documents.lastOrNull() // Cursor for next page

            LoadResult.Page(
                data = items,
                prevKey = null,     // Paging backward is not supported here
                nextKey = nextKey   // Cursor for next load
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<DocumentSnapshot, NewsArticle>): DocumentSnapshot? {
        return null // You can improve this with smarter logic later
    }
}