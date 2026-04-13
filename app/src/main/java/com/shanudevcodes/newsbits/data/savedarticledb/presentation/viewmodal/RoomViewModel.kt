package com.shanudevcodes.newsbits.data.savedarticledb.presentation.viewmodal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shanudevcodes.newsbits.data.savedarticledb.data.dao.RoomDao
import com.shanudevcodes.newsbits.data.savedarticledb.data.mapper.toDomain
import com.shanudevcodes.newsbits.data.savedarticledb.data.mapper.toSavedArticle
import com.shanudevcodes.newsbits.data.savedarticledb.domain.repository.ArticleRepository
import com.shanudevcodes.newsbits.data.savedarticledb.domain.usecase.DeleteArticleByIdUseCase
import com.shanudevcodes.newsbits.data.savedarticledb.domain.usecase.DeleteArticleUseCase
import com.shanudevcodes.newsbits.data.savedarticledb.domain.usecase.DeleteHistoryUseCase
import com.shanudevcodes.newsbits.data.savedarticledb.domain.usecase.GetArticleByIdUseCase
import com.shanudevcodes.newsbits.data.savedarticledb.domain.usecase.GetArticlesUseCase
import com.shanudevcodes.newsbits.data.savedarticledb.domain.usecase.GetHistoryUseCase
import com.shanudevcodes.newsbits.data.savedarticledb.domain.usecase.IsArticleSavedUseCase
import com.shanudevcodes.newsbits.data.savedarticledb.domain.usecase.SaveArticleUseCase
import com.shanudevcodes.newsbits.data.savedarticledb.domain.usecase.SaveHistoryUseCase
import com.shanudevcodes.newsbits.data.savedarticledb.presentation.events.RoomEvents
import com.shanudevcodes.newsbits.data.savedarticledb.presentation.states.RoomStates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoomViewModel @Inject constructor(
    private val repository: ArticleRepository,
    private val dao: RoomDao
) : ViewModel() {

    private val saveArticleUseCase = SaveArticleUseCase(repository)
    private val deleteArticleUseCase = DeleteArticleUseCase(repository)
    private val deleteArticleByIdUseCase = DeleteArticleByIdUseCase(repository)
    private val getArticlesUseCase = GetArticlesUseCase(repository)
    private val getArticleByIdUseCase = GetArticleByIdUseCase(repository)
    private val checkArticleSavedUseCase = IsArticleSavedUseCase(repository)
    private val getHistoryUseCase = GetHistoryUseCase(repository)
    private val saveHistoryUseCase = SaveHistoryUseCase(repository)
    private val deleteHistoryUseCase = DeleteHistoryUseCase(repository)

    private val _state = MutableStateFlow(RoomStates())
    val state = _state.asStateFlow()

    fun onEvent(event: RoomEvents) {
        when (event) {
            is RoomEvents.SaveArticle -> {
                viewModelScope.launch {
                    event.article?.let {
                        saveArticleUseCase(it.toSavedArticle().toDomain())
                        _state.value = _state.value.copy(isArticleSaved = true)
                    }
                }
            }
            is RoomEvents.DeleteArticle -> {
                viewModelScope.launch {
                    event.article?.let {
                        deleteArticleUseCase(it.toSavedArticle().toDomain())
                        _state.value = _state.value.copy(isArticleSaved = false)
                    }
                }
            }
            is RoomEvents.DeleteArticleById -> {
                viewModelScope.launch {
                    deleteArticleByIdUseCase(event.articleId)
                    _state.value = _state.value.copy(isArticleSaved = false)
                }
            }
            is RoomEvents.CheckArticleSaved -> {
                viewModelScope.launch {
                    val exists = checkArticleSavedUseCase(event.articleId)
                    _state.value = _state.value.copy(isArticleSaved = exists)
                }
            }
            is RoomEvents.GetArticles -> {
                viewModelScope.launch {
                    getArticlesUseCase().distinctUntilChanged().collect { articles ->
                        _state.value = _state.value.copy(savedArticles = articles)
                    }
                }
            }
            is RoomEvents.GetArticleById -> {
                viewModelScope.launch {
                    getArticleByIdUseCase(event.articleId).collect { article ->
                        _state.value = _state.value.copy(article = article)
                    }
                }
            }
            is RoomEvents.SaveHistory -> {
                viewModelScope.launch { saveHistoryUseCase(query = event.query) }
            }
            is RoomEvents.DeleteHistory -> {
                viewModelScope.launch { deleteHistoryUseCase(event.history.toDomain()) }
            }
            is RoomEvents.GetHistory -> {
                viewModelScope.launch {
                    getHistoryUseCase().collect { history ->
                        _state.value = _state.value.copy(historyList = history)
                    }
                }
            }
            is RoomEvents.SetHistoryQuery -> {
                _state.value = _state.value.copy(historyQuery = event.query)
            }
            is RoomEvents.UpdateBookMarkedArticle -> {
                viewModelScope.launch {
                    dao.upsertArticle(event.article)
                    _state.value = _state.value.copy(isArticleSaved = true)
                }
            }
            RoomEvents.UpsertHistory -> {
                viewModelScope.launch {
                    saveHistoryUseCase(_state.value.historyQuery)
                }
            }
            is RoomEvents.CheckEachArticleSaved -> {
                viewModelScope.launch {
                    val exists = checkArticleSavedUseCase(event.articleId)
                    event.onResult(exists)
                }
            }
        }
    }
}