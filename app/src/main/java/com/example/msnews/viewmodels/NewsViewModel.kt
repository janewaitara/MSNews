package com.example.msnews.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.msnews.R
import com.example.msnews.data.model.ApiResponse
import com.example.msnews.data.model.Article
import com.example.msnews.data.model.Resource
import com.example.msnews.data.repository.NewsRepository
import kotlinx.coroutines.launch

class NewsViewModel(
    private val newsRepository: NewsRepository
) : ViewModel() {

    private val _status = MutableLiveData<Resource<ApiResponse>>()
    private val _listOfTopArticles = MutableLiveData<List<Article>>()
    private val _listOfSearchedArticles = MutableLiveData<List<Article>>()
    private val _article = MutableLiveData<Article>()
    private var _categoryFilter = MutableLiveData<String>()


    val listOfTopArticles: LiveData<List<Article>> = _listOfTopArticles
    val listOfSearchedArticles: LiveData<List<Article>> = _listOfSearchedArticles
    val article: LiveData<Article> = _article
    val status: LiveData<Resource<ApiResponse>> = _status
    val categoryFilter: LiveData<String> = _categoryFilter

    init {
        if (hasNoCategorySet()){
           // setCategory(getString(R.string.general))
            setCategory("General")
        }
    }

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

    fun setCategory(selectedCategory: String){
        _categoryFilter.value = selectedCategory
    }

    fun hasNoCategorySet(): Boolean {
        return _categoryFilter.value.isNullOrEmpty()
    }

    fun onArticleClicked(article: Article) {
        _article.value = article
    }
}
