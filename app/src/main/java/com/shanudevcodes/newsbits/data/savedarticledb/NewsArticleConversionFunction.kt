package com.shanudevcodes.newsbits.data.savedarticledb

import com.google.firebase.Timestamp
import com.shanudevcodes.newsbits.data.NewsArticle
import java.text.SimpleDateFormat
import java.util.*

fun NewsArticle.toSavedArticle(): SavedArticle {
    return SavedArticle(
        article_id = article_id,
        category = category?.joinToString(",") ?: "",
        content = content,
        country = country?.joinToString(",") ?: "",
        createdAt = createdAt.toFormattedLocalString(),
        creator = creator?.joinToString(",") ?: "",
        description = description,
        image_url = image_url,
        keywords = keywords?.joinToString(",") ?: "",
        link = link,
        pubDate = pubDate,
        source_icon = source_icon,
        source_id = source_id,
        source_name = source_name,
        source_priority = source_priority,
        source_url = source_url,
        title = title,
        video_url = video_url
    )
}

// Converts Firebase Timestamp to device-local time formatted as String
fun Timestamp?.toFormattedLocalString(): String? {
    return this?.let {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        sdf.timeZone = TimeZone.getDefault()
        sdf.format(it.toDate())
    }
}