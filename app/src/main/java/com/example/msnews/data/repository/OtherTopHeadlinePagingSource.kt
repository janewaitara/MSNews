package com.example.msnews.data.repository

import androidx.paging.PagingSource
import com.example.msnews.BuildConfig
import com.example.msnews.data.api.NewsApiService
import com.example.msnews.data.model.Article
import com.example.msnews.data.utils.Constants
import com.example.msnews.data.utils.Constants.NETWORK_SEARCH_PAGE_SIZE
import retrofit2.HttpException
import java.io.IOException

class OtherTopHeadlinePagingSource(
    private val newsApi: NewsApiService,
    private val category: String,
    private val language: String,
) : PagingSource<Int, Article>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {

        // Start paging with the STARTING_KEY if this is the first load
        val page: Int = params.key ?: Constants.NEWS_STARTING_PAGE_NUMBER

        return try {
            val apiResult = newsApi.getTopHeadlines(
                language = language,
                apiKey = BuildConfig.API_KEY,
                pageSize = NETWORK_SEARCH_PAGE_SIZE,
                page = page,
                category = category
            )

            // Added the empty list to stop the crashing when the api requests have been exceeded
            val articleListing = apiResult.body()?.articles ?: listOf()

            val nextKey = if (articleListing.isEmpty()) {
                null
            } else {
                // initial load size = 3 * NETWORK_SEARCH_PAGE_SIZE
                // ensure we're not requesting duplicating items, at the 2nd request
                // page + (params.loadSize / NETWORK_SEARCH_PAGE_SIZE)
                page + 1
            }

            LoadResult.Page(
                data = articleListing,
                prevKey = if (page == Constants.NEWS_STARTING_PAGE_NUMBER) null else (page - 1),
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }
}
