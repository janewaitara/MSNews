package com.example.msnews.data.api

import com.example.msnews.data.model.ApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("category") category: String,
        @Query("language") language: String,
        @Query("apiKey") apiKey: String,
        @Query("pageSize") pageSize: Int = 20,
        @Query("page") page: Int
    ): Response<ApiResponse>

    @GET("v2/everything")
    suspend fun getSearchedNews(
        @Query("q") searchQuery: String,
        @Query("language") language: String,
        @Query("apiKey") apiKey: String,
        @Query("pageSize") pageSize: Int,
        @Query("page") page: Int
    ): Response<ApiResponse>
}
