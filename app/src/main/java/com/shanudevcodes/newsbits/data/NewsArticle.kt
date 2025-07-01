package com.shanudevcodes.newsbits.data

import androidx.annotation.Keep
import com.google.firebase.Timestamp

@Keep
data class NewsArticle(
    val ai_org: String? = null,
    val ai_region: String? = null,
    val ai_tag: String? = null,
    val article_id: String = "",
    val category: List<String> = emptyList(),
    val content: String? = null,
    val country: List<String> = emptyList(),
    val createdAt: Timestamp? = null, // 🔥 FIXED HERE
    val creator: List<String> = emptyList(),
    val description: String? = "",
    val duplicate: Boolean = false,
    val image_url: String? = null,
    val keywords: List<String> = emptyList(),
    val language: String = "",
    val link: String = "",
    val pubDate: String = "",
    val pubDateTZ: String = "",
    val sentiment: String? = null,
    val sentiment_stats: String? = null,
    val source_icon: String? = null,
    val source_id: String = "",
    val source_name: String = "",
    val source_priority: Int? = null,
    val source_url: String = "",
    val title: String = "",
    val video_url: String? = null
)
