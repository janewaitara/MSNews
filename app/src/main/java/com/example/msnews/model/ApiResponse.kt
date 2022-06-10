package com.example.msnews.model

/**
 * This data class defines an api response which includes the status, total results and articles.
 * The property names of this data class are used by Moshi to match the names of values in JSON.
 */
data class ApiResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<Article>
)
