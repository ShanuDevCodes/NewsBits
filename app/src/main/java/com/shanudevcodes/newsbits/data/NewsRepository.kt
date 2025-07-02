package com.shanudevcodes.newsbits.data


import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query.Direction.DESCENDING
import kotlinx.coroutines.tasks.await

suspend fun fetchAllNews(limit: Long = 50): List<NewsArticle> {
    val db = FirebaseFirestore.getInstance()
    val snapshot = db.collection("news_all")
        .orderBy("pubDate", DESCENDING)
        .limit(limit)
        .get()
        .await()
    return snapshot.documents.mapNotNull { doc ->
        doc.toObject(NewsArticle::class.java)
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