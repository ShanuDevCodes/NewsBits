package com.shanudevcodes.newsbits.data.savedarticledb

data class RoomStates(
    val savedArticles: List<SavedArticle> = emptyList(),
    val isArticleSaved: Boolean = false
)