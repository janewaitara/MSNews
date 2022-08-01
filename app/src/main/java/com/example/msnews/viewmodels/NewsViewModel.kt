package com.example.msnews.viewmodels

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.msnews.NewsApplication
import com.example.msnews.data.model.Article
import com.example.msnews.data.model.Resource
import com.example.msnews.data.repository.NewsRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Used AndroidViewModel to use the application context(lives as long as the app does)
 * */

class NewsViewModel(
    app: NewsApplication,
    private val newsRepository: NewsRepository,
) : AndroidViewModel(app) {

    private val _isNetworkAvailable = MutableLiveData<Boolean>()
    private val _status = MutableLiveData<Resource<List<Article>>>()
    private val _listOfPagedSearchedArticles = MutableLiveData<PagingData<Article>>()
    private val _listOfTopHeadlineArticles = MutableLiveData<PagingData<Article>>()

    private val _article = MutableLiveData<Article>()
    private var _categoryFilter = MutableLiveData<String>()

    val listOfTopHeadlineArticles = _listOfTopHeadlineArticles
    val listOfPagedSearchedArticles: LiveData<PagingData<Article>> = _listOfPagedSearchedArticles
    val article: LiveData<Article> = _article
    val status: LiveData<Resource<List<Article>>> = _status
    val isNetworkAvailable: LiveData<Boolean> = _isNetworkAvailable
    val categoryFilter: LiveData<String> = _categoryFilter

    init {
        if (hasNoCategorySet()) {
            // setCategory(getString(R.string.general))
            setCategory("General")
        }

        _isNetworkAvailable.value = true
    }

    private fun getGeneralTopHeadlinesFromDB(category: String, language: String) {
        viewModelScope.launch {
            val generalNewsList =
                newsRepository.getGeneralTopHeadlinesFromDB(category, language)
                    .cachedIn(viewModelScope)
            generalNewsList.collectLatest { pagingData ->
                _listOfTopHeadlineArticles.value = pagingData
            }
        }
    }

    private fun getOtherTopHeadlinesFromApi(
        category: String,
        language: String,
    ) {
        viewModelScope.launch {
            if (hasInternetConnection()) {
                _isNetworkAvailable.value = hasInternetConnection()
                val otherCategorisedNewsList =
                    newsRepository.getOtherTopHeadlinesFromApi(category, language)
                        .cachedIn(viewModelScope)
                otherCategorisedNewsList.collectLatest { pagingData ->
                    _listOfTopHeadlineArticles.value = pagingData
                }
            } else {
                _isNetworkAvailable.value = false
            }
        }
    }

    fun getPagedSearchedNews(searchQuery: String, language: String) {
        viewModelScope.launch {
            if (hasInternetConnection()) {
                _isNetworkAvailable.value = hasInternetConnection()
                val apiResponse = newsRepository.getPagedSearchedNews(searchQuery, language)
                    // to maintain paging state through configuration or navigation changes,
                    // we use the cachedIn()
                    .cachedIn(viewModelScope)

                apiResponse.collectLatest { pagedArticles ->
                    Log.d("Paged Data fetched for $searchQuery", pagedArticles.toString())
                    _listOfPagedSearchedArticles.value = pagedArticles
                }
            } else {
                _isNetworkAvailable.value = false
            }
        }
    }

    /**
     * Dilemma: I'm fetching categorised data whose response does not have categorisation in it
     * hence on room, all categories are stored together. This means that fetching from the db will
     * return the same list regardless of the chip(category) selected.
     * To solve this: In the meantime, I'm only storing general data in room
     * while others I fetch from the network hence the when statement.
     * */
    fun setCategory(selectedCategory: String) {
        _categoryFilter.value = selectedCategory

        when (selectedCategory) {
            "General" -> {
                _isNetworkAvailable.value = true
                getGeneralTopHeadlinesFromDB(selectedCategory, "en")
            }
            else -> {
                getOtherTopHeadlinesFromApi(selectedCategory, "en")
            }
        }
    }

    private fun hasNoCategorySet(): Boolean {
        return _categoryFilter.value.isNullOrEmpty()
    }

    fun onArticleClicked(article: Article) {
        _article.value = article
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager: ConnectivityManager =
            getApplication<NewsApplication>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION") val activeNetworkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return activeNetworkInfo.isConnected
        }
    }
}
