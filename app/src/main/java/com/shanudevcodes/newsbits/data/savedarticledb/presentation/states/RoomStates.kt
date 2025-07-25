package com.shanudevcodes.newsbits.data.savedarticledb.presentation.states

import com.shanudevcodes.newsbits.data.savedarticledb.domain.modal.Article
import com.shanudevcodes.newsbits.data.savedarticledb.domain.modal.SearchHistory

data class RoomStates(
    val savedArticles: List<Article> = emptyList(),
    val isArticleSaved: Boolean = false,
    val article: Article? = null,
    val historyQuery: String = "",
    val historyList: List<SearchHistory> = emptyList(),
)