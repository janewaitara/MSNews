package com.example.msnews.data.repository

import com.example.msnews.BuildConfig
import com.example.msnews.data.api.NewsApiService
import com.example.msnews.data.model.ApiResponse
import com.example.msnews.data.model.Resource
import retrofit2.Response
import java.io.IOException

class NewsRepositoryImpl(private val newsApi: NewsApiService) : NewsRepository {

    override suspend fun getTopHeadlinesFromApi(
        category: String,
        language: String
    ): Resource<ApiResponse> = try {

        val apiResult = newsApi.getTopHeadlines(category, language, BuildConfig.API_KEY)

        turnApiResultToResource(apiResult)
    } catch (error: Throwable) {
        Resource.Error(error.localizedMessage)
    }

    override suspend fun getSearchedNews(
        searchQuery: String,
        language: String
    ): Resource<ApiResponse> = try {
        val apiResult = newsApi.getSearchedNews(searchQuery, language, BuildConfig.API_KEY)

        turnApiResultToResource(apiResult)
    } catch (error: IOException) {
        Resource.Error(error.localizedMessage)
    }

    private fun turnApiResultToResource(apiResult: Response<ApiResponse>): Resource<ApiResponse> =
        when {
            apiResult.isSuccessful -> Resource.Success(apiResult.body()!!)
            else -> Resource.Error(apiResult.message())
        }
}
