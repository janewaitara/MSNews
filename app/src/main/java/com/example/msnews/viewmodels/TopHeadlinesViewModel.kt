package com.example.msnews.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.msnews.data.model.ApiResponse
import com.example.msnews.data.model.Article
import com.example.msnews.data.model.Resource
import com.example.msnews.data.repository.NewsRepository
import kotlinx.coroutines.launch

class TopHeadlinesViewModel(
    private val newsRepository: NewsRepository
) : ViewModel() {

    private val _status = MutableLiveData<Resource<ApiResponse>>()
    private val _listOfTopArticles = MutableLiveData<List<Article>>()
    private val _listOfSearchedArticles = MutableLiveData<List<Article>>()
    private val _article = MutableLiveData<Article>()

    private val topArticles: LiveData<List<Article>> = _listOfTopArticles
    private val searchedArticles: LiveData<List<Article>> = _listOfSearchedArticles
    private val article: LiveData<Article> = _article
    val status: LiveData<Resource<ApiResponse>> = _status

    fun getTopHeadlinesFromApi(
        category: String,
        language: String
    ) {
        viewModelScope.launch {
            _status.value = Resource.Loading()
            try {
                val apiResponse = newsRepository.getTopHeadlinesFromApi(category, language).data!!
                _listOfTopArticles.value = apiResponse.articles
                _status.value = Resource.Success(apiResponse)
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
                _listOfSearchedArticles.value = apiResponse.articles
                _status.value = Resource.Success(apiResponse)
            } catch (e: Exception) {
                _status.value = Resource.Error(e.localizedMessage)
                _listOfSearchedArticles.value = listOf()
            }
        }
    }

    fun onArticleClicked(article: Article) {
        _article.value = article
    }
}
