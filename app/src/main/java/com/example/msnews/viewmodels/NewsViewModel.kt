package com.example.msnews.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.example.msnews.data.model.Article
import com.example.msnews.data.model.Resource
import com.example.msnews.data.repository.NewsRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class NewsViewModel(
    private val newsRepository: NewsRepository
) : ViewModel() {

    private val _status = MutableLiveData<Resource<List<Article>>>()
    private val _listOfTopArticles = MutableLiveData<List<Article>>()
    private val _listOfSearchedArticles = MutableLiveData<List<Article>>()
    private val _article = MutableLiveData<Article>()
    private var _categoryFilter = MutableLiveData<String>()

    val listOfTopArticles: LiveData<List<Article>> = _listOfTopArticles
    val listOfSearchedArticles: LiveData<List<Article>> = _listOfSearchedArticles
    val article: LiveData<Article> = _article
    val status: LiveData<Resource<List<Article>>> = _status
    val categoryFilter: LiveData<String> = _categoryFilter

    init {
        if (hasNoCategorySet()) {
            // setCategory(getString(R.string.general))
            setCategory("General")
        }
    }

    fun getNewsFromApiAndInsertIntoDb(
        category: String,
        language: String
    ) {
        viewModelScope.launch {
            newsRepository.getNewsFromApiAndInsertIntoDb(category, language)
            Log.d("Data inserted", " Successful")
        }
    }

    fun getTopHeadlinesFromDb() {
        viewModelScope.launch {
            _status.value = Resource.Loading()
            try {
                val apiResponse = newsRepository.getNewsFromDb()
                apiResponse.collect { listOfArticles ->
                    Log.d("Data fetched", listOfArticles.size.toString())
                    _listOfTopArticles.value = listOfArticles
                    _status.value = Resource.Success(listOfArticles)
                }
            } catch (e: Exception) {
                _status.value = Resource.Error(e.localizedMessage)
                _listOfTopArticles.value = listOf()
            }
        }
    }

    fun getTopHeadlinesFromAPI(
        category: String,
        language: String
    ) {
        viewModelScope.launch {
            _status.value = Resource.Loading()
            try {
                val apiResponse = newsRepository.getTopHeadlinesFromApi(category, language).data!!
                Log.d("Data fetched for $category", apiResponse.articles.size.toString())
                _listOfTopArticles.value = apiResponse.articles
                _status.value = Resource.Success(apiResponse.articles!!)
            } catch (e: Exception) {
                _status.value = Resource.Error(e.localizedMessage)
                _listOfTopArticles.value = listOf()
            }
        }
    }

    fun getSearchedNews(searchQuery: String, language: String) {
        viewModelScope.launch {
            _status.value = Resource.Loading()
            try {
                val apiResponse = newsRepository.getSearchedNews(searchQuery, language).data!!
                Log.d("Data fetched for $searchQuery", apiResponse.articles.size.toString())
                _listOfSearchedArticles.value = apiResponse.articles
                _status.value = Resource.Success(apiResponse.articles)
            } catch (e: Exception) {
                _status.value = Resource.Error(e.localizedMessage)
                _listOfSearchedArticles.value = listOf()
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
                getNewsFromApiAndInsertIntoDb(selectedCategory, "en")
                getTopHeadlinesFromDb()
            }
            else -> {
                getTopHeadlinesFromAPI(selectedCategory, "en")
            }
        }
    }

    private fun hasNoCategorySet(): Boolean {
        return _categoryFilter.value.isNullOrEmpty()
    }

    fun onArticleClicked(article: Article) {
        _article.value = article
    }
}
