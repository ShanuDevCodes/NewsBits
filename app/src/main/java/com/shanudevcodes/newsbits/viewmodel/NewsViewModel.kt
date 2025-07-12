package com.shanudevcodes.newsbits.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.algolia.client.api.SearchClient
import com.algolia.client.model.search.SearchForHits
import com.algolia.client.model.search.SearchMethodParams
import com.algolia.client.model.search.SearchResponse
import com.algolia.client.model.search.SearchResult
import com.shanudevcodes.newsbits.BuildConfig
import com.shanudevcodes.newsbits.data.NewsArticle
import com.shanudevcodes.newsbits.data.NewsArticleSearch
import com.shanudevcodes.newsbits.data.fetchTopNews
import com.shanudevcodes.newsbits.data.getNewsPagingFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement

class NewsViewModel : ViewModel() {

    private val _searchResults = MutableStateFlow<List<NewsArticleSearch>>(emptyList())
    val searchResults: StateFlow<List<NewsArticleSearch>> = _searchResults

    fun resetSearchResults(){
        _searchResults.value = emptyList()
    }

    fun searchNewsInAlgolia(query: String) {
        viewModelScope.launch {
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
                                query = query
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
                Log.d("AlgoliaSearchResponseArticle", "Parsed articles: $articles")

            } catch (e: Exception) {
                Log.e("AlgoliaSearch", "Search failed: ${e.message}", e)
                _searchResults.value = emptyList()
            }
        }
    }

    private val _isNewsLoaded = MutableStateFlow(false)
    val isNewsLoaded: StateFlow<Boolean> = _isNewsLoaded

    private val _topNews = MutableStateFlow<List<NewsArticle>>(emptyList())
    val topNews: StateFlow<List<NewsArticle>> = _topNews

    val allNewsPagingFlow: Flow<PagingData<NewsArticle>> = getNewsPagingFlow().cachedIn(viewModelScope)

    fun loadTopNews(){
        viewModelScope.launch {
            _topNews.value = fetchTopNews()
        }
    }

    fun newsLoaded(){
        _isNewsLoaded.value = true
    }
}