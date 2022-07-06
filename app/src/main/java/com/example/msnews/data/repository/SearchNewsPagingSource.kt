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

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1) ?: state.closestPageToPosition(
                position
            )?.nextKey?.minus(1)
        }
    }
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {

        // Start paging with the STARTING_KEY if this is the first load
        val page: Int = params.key ?: NEWS_STARTING_PAGE_NUMBER

        return try {
            val apiResult = newsApi.getSearchedNews(
                searchQuery = searchQuery, language = language,
                apiKey = BuildConfig.API_KEY, page
            )

            /**
             * Would have used this:
             * val articleListing = apiResult.body()?.articles!!
             * if we didn't have our own Resource
             * */
            val apiResultAsResource = turnApiResultToResource(apiResult)
            val searchedArticleListing = apiResultAsResource.data!!.articles

            val nextKey = if (searchedArticleListing.isEmpty()) {
                null
            } else {
                page + (params.loadSize / NETWORK_SEARCH_PAGE_SIZE)
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
