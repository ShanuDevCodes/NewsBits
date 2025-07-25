package com.shanudevcodes.newsbits.data.savedarticledb.data.mapper

import com.shanudevcodes.newsbits.data.savedarticledb.data.entity.History
import com.shanudevcodes.newsbits.data.savedarticledb.data.entity.SavedArticle
import com.shanudevcodes.newsbits.data.savedarticledb.domain.modal.Article
import com.shanudevcodes.newsbits.data.savedarticledb.domain.modal.SearchHistory

fun Article.toEntity(): SavedArticle = SavedArticle(
    article_id = article_id,
    title = title,
    description = description,
    content = content,
    category = category,
    country = country,
    creator = creator,
    createdAt = createdAt,
    pubDate = pubDate,
    image_url = image_url,
    video_url = video_url,
    link = link,
    source_id = source_id,
    source_name = source_name,
    source_icon = source_icon,
    source_url = source_url,
    source_priority = source_priority,
    keywords = keywords
)

fun SavedArticle.toDomain(): Article = Article(
    article_id = article_id,
    title = title,
    description = description,
    content = content,
    category = category,
    country = country,
    creator = creator,
    createdAt = createdAt,
    pubDate = pubDate,
    image_url = image_url,
    video_url = video_url,
    link = link,
    source_id = source_id,
    source_name = source_name,
    source_icon = source_icon,
    source_url = source_url,
    source_priority = source_priority,
    keywords = keywords
)

fun SearchHistory.toEntity(): History = History(id = id, query = query)
fun History.toDomain(): SearchHistory = SearchHistory( id = id, query = query)