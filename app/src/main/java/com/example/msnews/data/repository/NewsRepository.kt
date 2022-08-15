package com.example.msnews.data.repository

import androidx.paging.PagingData
import com.example.msnews.data.model.Article
import kotlinx.coroutines.flow.Flow

/**
 * This is what is called in the viewModal
 * Helps to maintain flexibility on what is used by the viewModal as it depends on what is passed
 * ViewModal relies on the interface/abstraction
 * eg. While testing, we can use different versions of it without using our real apis
 * */
interface NewsRepository {

    // get from Api
    suspend fun getPagedSearchedNews(searchQuery: String, language: String): Flow<PagingData<Article>>
    fun getOtherTopHeadlinesFromApi(category: String, language: String): Flow<PagingData<Article>>

    // Local storage functions
    fun getGeneralTopHeadlinesFromDB(category: String, language: String): Flow<PagingData<Article>>
}
