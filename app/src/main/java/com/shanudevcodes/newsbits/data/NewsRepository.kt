package com.shanudevcodes.newsbits.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

suspend fun fetchLatestNews(): List<NewsArticle> {
    val db = FirebaseFirestore.getInstance()
    val doc = db.collection("news").document("latest").get().await()
    val articlesList = doc.get("articles") as? List<Map<String, Any>> ?: emptyList()
    return articlesList.mapNotNull { map ->
        try {
            NewsArticle(
                ai_org = map["ai_org"] as? String,
                ai_region = map["ai_region"] as? String,
                ai_tag = map["ai_tag"] as? String,
                article_id = map["article_id"] as? String ?: "",
                category = map["category"] as? List<String> ?: emptyList(),
                content = map["content"] as? String,
                country = map["country"] as? List<String> ?: emptyList(),
                creator = map["creator"] as? List<String> ?: emptyList(),
                description = map["description"] as? String ?: "",
                duplicate = map["duplicate"] as? Boolean ?: false,
                image_url = map["image_url"] as? String,
                keywords = map["keywords"] as? List<String> ?: emptyList(),
                language = map["language"] as? String ?: "",
                link = map["link"] as? String ?: "",
                pubDate = map["pubDate"] as? String ?: "",
                pubDateTZ = map["pubDateTZ"] as? String ?: "",
                sentiment = map["sentiment"] as? String,
                sentiment_stats = map["sentiment_stats"] as? String,
                source_icon = map["source_icon"] as? String,
                source_id = map["source_id"] as? String ?: "",
                source_name = map["source_name"] as? String ?: "",
                source_priority = (map["source_priority"] as? Long)?.toInt() ?: 0,
                source_url = map["source_url"] as? String ?: "",
                title = map["title"] as? String ?: "",
                video_url = map["video_url"] as? String
            )
        } catch (e: Exception) {
            null
        }
    }
}
suspend fun fetchAllNews(limit: Long = 20): List<NewsArticle> {
    val db = FirebaseFirestore.getInstance()
    val snapshot = db.collection("news_all")
        .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
        .limit(limit)
        .get()
        .await()
    return snapshot.documents.mapNotNull { doc ->
        doc.toObject(NewsArticle::class.java)
    }
}
suspend fun getArticleByArticleId(articleId: String): NewsArticle? {
    return try {
        val snapshot = FirebaseFirestore.getInstance()
            .collection("newsArticles")
            .whereEqualTo("article_id", articleId)
            .limit(1)
            .get()
            .await()

        if (!snapshot.isEmpty) {
            snapshot.documents[0].toObject(NewsArticle::class.java)
        } else {
            null
        }
    } catch (e: Exception) {
        Log.e("Firestore", "Failed to fetch article by ID", e)
        null
    }
}