package com.shanudevcodes.newsbits.data.savedarticledb

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RoomViewModel(
    private val dao: RoomDao
): ViewModel() {
    private val _state = MutableStateFlow(RoomStates())
    val state = _state

    fun onEvent(event: RoomEvents) {
        when(event) {
            is RoomEvents.DeleteArticle -> {
                viewModelScope.launch {
                    dao.deleteArticle(event.article.toSavedArticle())
                    _state.value = _state.value.copy(
                        isArticleSaved = false
                    )
                }
            }
            RoomEvents.GetArticles -> {
                viewModelScope.launch {
                    dao.getArticles()
                        .collect { articles ->
                            _state.value = _state.value.copy(
                                savedArticles = articles
                            )
                        }
                }
            }
            is RoomEvents.SaveArticle -> {
                viewModelScope.launch {
                    dao.upsertArticle(event.article.toSavedArticle())
                    _state.value = _state.value.copy(
                        isArticleSaved = true
                    )
                }
            }

            is RoomEvents.CheckArticleSaved -> {
                viewModelScope.launch {
                    _state.value = _state.value.copy(
                        isArticleSaved = dao.checkArticleSaved(event.articleId)
                    )
                }
            }

            is RoomEvents.GetArticleById -> {
                viewModelScope.launch {
                    dao.getArticleById(event.articleId)
                        .collect { article ->
                            _state.value = _state.value.copy(
                                article = article
                            )
                        }
                }
            }

            is RoomEvents.DeleteArticleById -> {
                viewModelScope.launch {
                    dao.deleteArticleById(event.articleId)
                    _state.value = _state.value.copy(
                        isArticleSaved = false
                    )
                }
            }

            is RoomEvents.UpdateBookMarkedArticle -> {
                viewModelScope.launch {
                    dao.upsertArticle(event.article)
                    _state.value = _state.value.copy(
                        isArticleSaved = true
                    )
                }
            }

            RoomEvents.GetHistory -> {
                viewModelScope.launch {
                    dao.getHistory()
                        .collect { history ->
                            _state.value = _state.value.copy(
                                historyList = history
                            )
                        }
                }
            }
            is RoomEvents.SetHistoryQuery -> {
                viewModelScope.launch {
                    _state.value = _state.value.copy(
                        historyQuery = event.query
                    )
                }
            }
            is RoomEvents.DeleteHistory -> {
                viewModelScope.launch {
                    dao.deleteHistory(event.history)
                    _state.value = _state.value.copy(
                        historyList = emptyList()
                    )
                }
            }

            is RoomEvents.UpsertHistory -> {
                viewModelScope.launch {
                    dao.upsertHistory(History(query = _state.value.historyQuery))
                }
            }
        }
    }
}