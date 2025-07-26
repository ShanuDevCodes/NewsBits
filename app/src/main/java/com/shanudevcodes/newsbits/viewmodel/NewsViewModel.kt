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
import com.shanudevcodes.newsbits.data.fetchTopNews
import com.shanudevcodes.newsbits.data.getNewsPagingFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement

@OptIn(ExperimentalCoroutinesApi::class)
class NewsViewModel : ViewModel() {

    private val _searchResults = MutableStateFlow<List<NewsArticleSearch>>(emptyList())
    val searchResults: StateFlow<List<NewsArticleSearch>> = _searchResults

    private val _searchSuggestions = MutableStateFlow<List<SearchSuggestion>>(emptyList())
    val searchSuggestions: StateFlow<List<SearchSuggestion>> = _searchSuggestions

    private val _isSearchResultsLoaded = MutableStateFlow(false)
    val isSearchResultsLoaded: StateFlow<Boolean> = _isSearchResultsLoaded

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore

    private var _paginationFailed = MutableStateFlow(false)
    val paginationFailed: StateFlow<Boolean> = _paginationFailed

    fun resetSearchResultsLoaded(){
        _isSearchResultsLoaded.value = false
    }

    fun resetSearchResults(){
        _searchResults.value = emptyList()
    }

    fun resetSearchSuggestions(){
        _searchSuggestions.value = emptyList()
    }

    fun searchSuggestionInAlgolia(query: String){
        viewModelScope.launch {
            val appID = BuildConfig.ALGOLIA_APP_ID
            val apiKey = BuildConfig.ALGOLIA_SEARCH_KEY
            val indexName = "News_Suggestions_Record"

            val client = SearchClient(appID, apiKey)

            try {
                val response = client.search(
                    SearchMethodParams(
                        requests = listOf(
                            SearchForHits(
                                indexName = indexName,
                                query = query
                            )
                        )
                    )
                )

                val json = Json { ignoreUnknownKeys = true }

                val suggestions = response.results.mapNotNull { result ->
                    when (result) {
                        is SearchResult.SearchResponseValue -> {
                            // Old behavior: wrapped
                            result.value.hits.mapNotNull { hit ->
                                hit.additionalProperties?.let {
                                    val jsonObject = JsonObject(it)
                                    json.decodeFromJsonElement<SearchSuggestion>(jsonObject)
                                }
                            }
                        }

                        is SearchResponse -> {
                            // Newer SDK behavior: direct SearchResponse
                            result.hits.mapNotNull { hit ->
                                hit.additionalProperties?.let {
                                    val jsonObject = JsonObject(it)
                                    json.decodeFromJsonElement<SearchSuggestion>(jsonObject)
                                }
                            }
                        }

                        else -> null
                    }
                }.flatten()

                _searchSuggestions.value = suggestions

            }catch (e: Exception){
                Log.e("AlgoliaSearch", "Search Suggestion failed: ${e.message}", e)
                _searchSuggestions.value = emptyList()
            }
        }
    }

    fun searchNewsInAlgolia(query: String, page: Int = 0) {
        viewModelScope.launch {
            _isSearchResultsLoaded.value = false
            val appID = BuildConfig.ALGOLIA_APP_ID
            val apiKey = BuildConfig.ALGOLIA_SEARCH_KEY
            val indexName = BuildConfig.ALGOLIA_INDEX

            val client = SearchClient(appID, apiKey)

            try {
                val response = client.search(
                    SearchMethodParams(
                        requests = listOf(
                            SearchForHits(
                                indexName = indexName,
                                query = query,
                                page = page
                            )
                        )
                    )
                )

                Log.d("AlgoliaSearchResponse", "Search result: ${response.results.filterIsInstance<SearchResult.SearchResponseValue>()}")

                val json = Json { ignoreUnknownKeys = true }


                val articles = response.results.mapNotNull { result ->
                    when (result) {
                        is SearchResult.SearchResponseValue -> {
                            // Old behavior: wrapped
                            result.value.hits.mapNotNull { hit ->
                                hit.additionalProperties?.let {
                                    val jsonObject = JsonObject(it)
                                    json.decodeFromJsonElement<NewsArticleSearch>(jsonObject)
                                }
                            }
                        }

                        is SearchResponse -> {
                            // Newer SDK behavior: direct SearchResponse
                            result.hits.mapNotNull { hit ->
                                hit.additionalProperties?.let {
                                    val jsonObject = JsonObject(it)
                                    json.decodeFromJsonElement<NewsArticleSearch>(jsonObject)
                                }
                            }
                        }

                        else -> null
                    }
                }.flatten()

                _searchResults.value = articles
                _isSearchResultsLoaded.value = true
                Log.d("AlgoliaSearchResponseArticle", "Parsed articles: $articles")

            } catch (e: Exception) {
                Log.e("AlgoliaSearch", "Search failed: ${e.message}", e)
                _searchResults.value = emptyList()
            }
        }
    }

    fun loadMoreNewsFromAlgolia(query: String, page: Int){
        viewModelScope.launch {

            _isLoadingMore.value = true

            val appID = BuildConfig.ALGOLIA_APP_ID
            val apiKey = BuildConfig.ALGOLIA_SEARCH_KEY
            val indexName = BuildConfig.ALGOLIA_INDEX

            val client = SearchClient(appID, apiKey)

            try {
                val response = client.search(
                    SearchMethodParams(
                        requests = listOf(
                            SearchForHits(
                                indexName = indexName,
                                query = query,
                                page = page
                            )
                        )
                    )
                )

                Log.d("AlgoliaSearchResponse", "Search result: ${response.results.filterIsInstance<SearchResult.SearchResponseValue>()}")

                val json = Json { ignoreUnknownKeys = true }


                val articles = response.results.mapNotNull { result ->
                    when (result) {
                        is SearchResult.SearchResponseValue -> {
                            // Old behavior: wrapped
                            result.value.hits.mapNotNull { hit ->
                                hit.additionalProperties?.let {
                                    val jsonObject = JsonObject(it)
                                    json.decodeFromJsonElement<NewsArticleSearch>(jsonObject)
                                }
                            }
                        }

                        is SearchResponse -> {
                            // Newer SDK behavior: direct SearchResponse
                            result.hits.mapNotNull { hit ->
                                hit.additionalProperties?.let {
                                    val jsonObject = JsonObject(it)
                                    json.decodeFromJsonElement<NewsArticleSearch>(jsonObject)
                                }
                            }
                        }

                        else -> null
                    }
                }.flatten()

                _searchResults.value = _searchResults.value + articles
                Log.d("AlgoliaSearchResponseArticle", "Parsed articles: $articles")
                _isLoadingMore.value = false
                _paginationFailed.value = false

            } catch (e: Exception) {
                _paginationFailed.value = true
                Log.e("AlgoliaSearch", "Search failed: ${e.message}", e)
            }

        }
    }

    private val _isNewsLoaded = MutableStateFlow(false)
    val isNewsLoaded: StateFlow<Boolean> = _isNewsLoaded

    private val _topNews = MutableStateFlow<List<NewsArticle>>(emptyList())
    val topNews: StateFlow<List<NewsArticle>> = _topNews

    private val _isTopNewsLoaded = MutableStateFlow(false)
    val isTopNewsLoaded: StateFlow<Boolean> = _isTopNewsLoaded

    private val _selectedCategory = MutableStateFlow<String?>(null)

    val selectedCategory = _selectedCategory.asStateFlow()

    val allNewsPagingFlow = selectedCategory
        .flatMapLatest { category ->
            getNewsPagingFlow(category)
        }
        .cachedIn(viewModelScope)

    fun setCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun loadTopNews(){
        viewModelScope.launch {
            _isTopNewsLoaded.value = false
            _topNews.value = fetchTopNews()
            if (!_topNews.value.isEmpty()) {
                _isTopNewsLoaded.value = true
            }
        }
    }

    fun newsLoaded(){
        _isNewsLoaded.value = true
    }
}