package com.shanudevcodes.newsbits.data

import android.util.Log
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
import java.net.URLEncoder

fun getNewsPagingFlow(category: String? = null): Flow<PagingData<NewsArticle>> {
    val collection = FirebaseFirestore.getInstance()
        .collection("news_all")

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

suspend fun fetchNewsByLink(link: String): NewsArticle? {
    val encodedLink = URLEncoder.encode(link, "UTF-8")
    val db = FirebaseFirestore.getInstance()

    return try {
        val snapshot = db.collection("news_all")
            .document(encodedLink)
            .get()
            .await()

        if (snapshot.exists()) {
            snapshot.toObject(NewsArticle::class.java)
        } else {
            null
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
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
    private val collection: CollectionReference,
    private val categoryFilter: String? = null,
    private val countryFilter: String? = null, // ✅ NEW
    private val pageSize: Long = 20
) : PagingSource<DocumentSnapshot, NewsArticle>() {

    override suspend fun load(params: LoadParams<DocumentSnapshot>): LoadResult<DocumentSnapshot, NewsArticle> {
        return try {
            val currentKey = params.key
            var baseQuery: Query = collection

            Log.d("FirestorePagingSource", "Loading page. Category: $categoryFilter, Country: $countryFilter, Key: $currentKey")

            // ✅ Apply category filter
            if (!categoryFilter.isNullOrEmpty()) {
                Log.d("FirestorePagingSource", "Applying category filter: $categoryFilter")
                baseQuery = baseQuery.whereArrayContains("category", categoryFilter)
            }

            // ✅ Apply country filter
            if (!countryFilter.isNullOrEmpty()) {
                Log.d("FirestorePagingSource", "Applying country filter: $countryFilter")
                baseQuery = baseQuery.whereArrayContains("country", countryFilter)
            }

            // ✅ Order by pubDate
            baseQuery = baseQuery.orderBy("pubDate", Query.Direction.DESCENDING)

            // ✅ Pagination logic
            val finalQuery = if (currentKey == null) {
                baseQuery.limit(pageSize)
            } else {
                baseQuery.startAfter(currentKey).limit(pageSize)
            }

            val snapshot = finalQuery.get().await()
            val documents = snapshot.documents
            val items = documents.mapNotNull { it.toObject(NewsArticle::class.java) }

            val nextKey = if (documents.size < pageSize) null else documents.lastOrNull()

            Log.d("FirestorePagingSource", "Loaded ${items.size} items. NextKey: $nextKey")

            LoadResult.Page(
                data = items,
                prevKey = null,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            Log.e("FirestorePagingSource", "Error loading page", e)
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<DocumentSnapshot, NewsArticle>): DocumentSnapshot? {
        return null
    }
}