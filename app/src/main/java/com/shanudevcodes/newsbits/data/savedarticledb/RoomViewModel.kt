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
                        isArticleSaved = dao.checkArticleSaved(event.article.article_id)
                    )
                }
            }
        }
    }
}