package com.example.msnews.data.repository

import com.example.msnews.data.model.ApiResponse
import com.example.msnews.data.model.Article
import com.example.msnews.data.model.Resource
import kotlinx.coroutines.flow.Flow

/**
 * This is what is called in the viewModal
 * Helps to maintain flexibility on what is used by the viewModal as it depends on what is passed
 * ViewModal relies on the interface/abstraction
 * eg. While testing, we can use different versions of it without using our real apis
 * */
interface NewsRepository {

    // get from Api
    suspend fun getTopHeadlinesFromApi(category: String, language: String): Resource<ApiResponse>
    suspend fun getSearchedNews(searchQuery: String, language: String): Resource<ApiResponse>

    // Local storage functions
    suspend fun insertNewsToDb(articles: List<Article>)
    fun getNewsFromDb(): Flow<List<Article>>

    suspend fun getNewsFromApiAndInsertIntoDb(category: String, language: String)
}
