package com.example.msnews.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.msnews.BuildConfig
import com.example.msnews.data.api.NewsApiService
import com.example.msnews.data.model.ApiResponse
import com.example.msnews.data.model.Article
import com.example.msnews.data.model.Resource
import com.example.msnews.data.utils.Constants.NETWORK_SEARCH_PAGE_SIZE
import com.example.msnews.data.utils.Constants.NEWS_STARTING_PAGE_NUMBER
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class SearchNewsPagingSource(
    private val newsApi: NewsApiService,
    private val searchQuery: String,
    private val language: String
) : PagingSource<Int, Article>() {

    /**
     * called when the Paging library needs to reload items for the UI because the data
     * in its backing PagingSource has changed(situation is called invalidation)
     * */
    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1) ?: state.closestPageToPosition(
                position
            )?.nextKey?.minus(1)
        }
    }

    /**
     * called by the Paging library to asynchronously fetch more data
     * to be displayed as the user scrolls around.
     * */
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {

        // Start paging with the STARTING_KEY if this is the first load
        val page: Int = params.key ?: NEWS_STARTING_PAGE_NUMBER

        return try {
            val apiResult = newsApi.getSearchedNews(
                searchQuery = searchQuery,
                language = language,
                apiKey = BuildConfig.API_KEY,
                pageSize = NETWORK_SEARCH_PAGE_SIZE,
                page = page
            )

            /**
             * Would have used this:
             * val articleListing = apiResult.body()?.articles!!
             * if we didn't have our own Resource
             * */
            val apiResultAsResource = turnApiResultToResource(apiResult)

            // Added the empty list to stop the crashing when the api requests have been exceeded
            val searchedArticleListing = apiResultAsResource.data?.articles ?: listOf()

            val nextKey = if (searchedArticleListing.isEmpty()) {
                null
            } else {
                // initial load size = 3 * NETWORK_SEARCH_PAGE_SIZE
                // ensure we're not requesting duplicating items, at the 2nd request
                // page + (params.loadSize / NETWORK_SEARCH_PAGE_SIZE)
                page + 1
            }

            LoadResult.Page(
                data = searchedArticleListing,
                prevKey = if (page == NEWS_STARTING_PAGE_NUMBER) null else (page - 1),
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }
}

private fun turnApiResultToResource(apiResult: Response<ApiResponse>): Resource<ApiResponse> =
    when {
        apiResult.isSuccessful -> Resource.Success(apiResult.body()!!)
        else -> Resource.Error(apiResult.message())
    }
