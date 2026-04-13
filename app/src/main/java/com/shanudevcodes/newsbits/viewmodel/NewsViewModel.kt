package com.shanudevcodes.newsbits.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.algolia.client.api.SearchClient
import com.algolia.client.model.search.SearchForHits
import com.algolia.client.model.search.SearchMethodParams
import com.algolia.client.model.search.SearchResponse
import com.algolia.client.model.search.SearchResult
import com.shanudevcodes.newsbits.BuildConfig
import com.shanudevcodes.newsbits.data.NewsArticle
import com.shanudevcodes.newsbits.data.NewsArticleSearch
import com.shanudevcodes.newsbits.data.SearchSuggestion
import com.shanudevcodes.newsbits.feature.news.domain.repository.NewsRepository
import com.shanudevcodes.newsbits.feature.news.domain.usecase.GetTopNewsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class NewsViewModel @Inject constructor(
    private val repository: NewsRepository,
    private val getTopNewsUseCase: GetTopNewsUseCase
) : ViewModel() {

    private val _currentLink = MutableStateFlow("")
    val currentLink: StateFlow<String> = _currentLink

    private val _searchResults = MutableStateFlow<List<NewsArticleSearch>>(emptyList())
    val searchResults: StateFlow<List<NewsArticleSearch>> = _searchResults

    private val _searchSuggestions = MutableStateFlow<List<SearchSuggestion>>(emptyList())
    val searchSuggestions: StateFlow<List<SearchSuggestion>> = _searchSuggestions

    private val _isSearchResultsLoaded = MutableStateFlow(false)
    val isSearchResultsLoaded: StateFlow<Boolean> = _isSearchResultsLoaded

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore

    private val _paginationFailed = MutableStateFlow(false)
    val paginationFailed: StateFlow<Boolean> = _paginationFailed

    private val _isNewsLoaded = MutableStateFlow(false)
    val isNewsLoaded: StateFlow<Boolean> = _isNewsLoaded

    private val _topNews = MutableStateFlow<List<NewsArticle>>(emptyList())
    val topNews: StateFlow<List<NewsArticle>> = _topNews

    private val _isTopNewsLoaded = MutableStateFlow(false)
    val isTopNewsLoaded: StateFlow<Boolean> = _isTopNewsLoaded

    private val _preference = MutableStateFlow<String?>(null)
    val preference: StateFlow<String?> = _preference

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    val allNewsPagingFlow = _selectedCategory
        .flatMapLatest { category -> repository.getNewsPagingFlow(category) }
        .cachedIn(viewModelScope)

    val forYouNewsPagingFlow = _preference
        .flatMapLatest { category -> repository.getNewsPagingFlow(category) }
        .cachedIn(viewModelScope)

    fun resetCurrentLink() { _currentLink.value = "" }
    fun setCurrentLink(link: String) { _currentLink.value = link }
    fun resetSearchResultsLoaded() { _isSearchResultsLoaded.value = false }
    fun resetSearchResults() { _searchResults.value = emptyList() }
    fun resetSearchSuggestions() { _searchSuggestions.value = emptyList() }
    fun setPreference(preference: String?) { _preference.value = preference }
    fun setCategory(category: String?) { _selectedCategory.value = category }
    fun newsLoaded() { _isNewsLoaded.value = true }

    fun loadTopNews() {
        viewModelScope.launch {
            _isTopNewsLoaded.value = false
            _topNews.value = getTopNewsUseCase()
            if (_topNews.value.isNotEmpty()) _isTopNewsLoaded.value = true
        }
    }

    fun searchSuggestionInAlgolia(query: String) {
        viewModelScope.launch {
            val client = SearchClient(BuildConfig.ALGOLIA_APP_ID, BuildConfig.ALGOLIA_SEARCH_KEY)
            try {
                val response = client.search(
                    SearchMethodParams(
                        requests = listOf(SearchForHits(indexName = "News_Suggestions_Record", query = query))
                    )
                )
                val json = Json { ignoreUnknownKeys = true }
                val suggestions = response.results.mapNotNull { result ->
                    when (result) {
                        is SearchResult.SearchResponseValue -> result.value.hits.mapNotNull { hit ->
                            hit.additionalProperties?.let { json.decodeFromJsonElement<SearchSuggestion>(JsonObject(it)) }
                        }
                        is SearchResponse -> result.hits.mapNotNull { hit ->
                            hit.additionalProperties?.let { json.decodeFromJsonElement<SearchSuggestion>(JsonObject(it)) }
                        }
                        else -> null
                    }
                }.flatten()
                _searchSuggestions.value = suggestions
            } catch (e: Exception) {
                Log.e("AlgoliaSearch", "Suggestion failed: ${e.message}", e)
                _searchSuggestions.value = emptyList()
            }
        }
    }

    fun searchNewsInAlgolia(query: String, page: Int = 0) {
        viewModelScope.launch {
            _isSearchResultsLoaded.value = false
            val client = SearchClient(BuildConfig.ALGOLIA_APP_ID, BuildConfig.ALGOLIA_SEARCH_KEY)
            try {
                val response = client.search(
                    SearchMethodParams(
                        requests = listOf(SearchForHits(indexName = BuildConfig.ALGOLIA_INDEX, query = query, page = page))
                    )
                )
                val json = Json { ignoreUnknownKeys = true }
                val articles = response.results.mapNotNull { result ->
                    when (result) {
                        is SearchResult.SearchResponseValue -> result.value.hits.mapNotNull { hit ->
                            hit.additionalProperties?.let { json.decodeFromJsonElement<NewsArticleSearch>(JsonObject(it)) }
                        }
                        is SearchResponse -> result.hits.mapNotNull { hit ->
                            hit.additionalProperties?.let { json.decodeFromJsonElement<NewsArticleSearch>(JsonObject(it)) }
                        }
                        else -> null
                    }
                }.flatten()
                _searchResults.value = articles
                _isSearchResultsLoaded.value = true
            } catch (e: Exception) {
                Log.e("AlgoliaSearch", "Search failed: ${e.message}", e)
                _searchResults.value = emptyList()
            }
        }
    }

    fun loadMoreNewsFromAlgolia(query: String, page: Int) {
        viewModelScope.launch {
            _isLoadingMore.value = true
            val client = SearchClient(BuildConfig.ALGOLIA_APP_ID, BuildConfig.ALGOLIA_SEARCH_KEY)
            try {
                val response = client.search(
                    SearchMethodParams(
                        requests = listOf(SearchForHits(indexName = BuildConfig.ALGOLIA_INDEX, query = query, page = page))
                    )
                )
                val json = Json { ignoreUnknownKeys = true }
                val articles = response.results.mapNotNull { result ->
                    when (result) {
                        is SearchResult.SearchResponseValue -> result.value.hits.mapNotNull { hit ->
                            hit.additionalProperties?.let { json.decodeFromJsonElement<NewsArticleSearch>(JsonObject(it)) }
                        }
                        is SearchResponse -> result.hits.mapNotNull { hit ->
                            hit.additionalProperties?.let { json.decodeFromJsonElement<NewsArticleSearch>(JsonObject(it)) }
                        }
                        else -> null
                    }
                }.flatten()
                _searchResults.value = _searchResults.value + articles
                _isLoadingMore.value = false
                _paginationFailed.value = false
            } catch (e: Exception) {
                _paginationFailed.value = true
                Log.e("AlgoliaSearch", "Load more failed: ${e.message}", e)
            }
        }
    }
}