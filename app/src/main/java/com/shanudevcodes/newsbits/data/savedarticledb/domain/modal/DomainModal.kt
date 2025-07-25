package com.shanudevcodes.newsbits.data.savedarticledb.domain.modal

data class Article(
    val article_id: String = "",
    val category: String = "",
    val content: String? = null,
    val country: String? = null,
    val createdAt: String? = null, // 🔥 FIXED HERE
    val creator: String? = null,
    val description: String? = "",
    val image_url: String? = null,
    val keywords: String? = null,
    val link: String = "",
    val pubDate: String = "",
    val source_icon: String? = null,
    val source_id: String = "",
    val source_name: String = "",
    val source_priority: Int? = null,
    val source_url: String = "",
    val title: String = "",
    val video_url: String? = null
)

// domain/model/SearchHistory.kt
data class SearchHistory(
    val id: Int = 0,
    val query: String
)