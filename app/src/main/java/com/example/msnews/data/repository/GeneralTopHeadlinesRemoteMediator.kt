package com.example.msnews.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.msnews.BuildConfig
import com.example.msnews.data.api.NewsApiService
import com.example.msnews.data.db.ArticlesDatabase
import com.example.msnews.data.db.ArticlesKeys
import com.example.msnews.data.model.Article
import com.example.msnews.data.utils.Constants.NEWS_STARTING_PAGE_NUMBER
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class GeneralTopHeadlinesRemoteMediator(
    private val newsApiService: NewsApiService,
    private val articlesDatabase: ArticlesDatabase,
    private val category: String,
    private val language: String,
) : RemoteMediator<Int, Article>() {

    /**
     * PagingState - this gives us information about the pages that were loaded before, the most
     * recently accessed index in the list, and the PagingConfig we defined when initializing the
     * paging stream.
     * LoadType - this tells us whether we need to load data at the end (LoadType.APPEND) or at the
     * beginning of the data (LoadType.PREPEND) that we previously loaded,
     * or if this the first time we're loading data (LoadType.REFRESH).
     * */
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Article>,
    ): MediatorResult {

        // the page we need to load from the network, based on the LoadType.
        val page = when (loadType) {
            // when it's the first time we're loading data, or when PagingDataAdapter.refresh() is called
            LoadType.REFRESH -> {
                val articlesKeys = getRemoteKeyClosestToCurrentPosition(state)
                articlesKeys?.nextKey?.minus(1) ?: NEWS_STARTING_PAGE_NUMBER
            }
            // when we need to load data at the beginning of the currently loaded data set
            LoadType.PREPEND -> {
                val articlesKeys = getRemoteKeyForFirstItem(state)
                val prevKey = articlesKeys?.prevKey
                if (prevKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = articlesKeys != null)
                }
                prevKey
            }
            // When we need to load data at the end of the currently loaded data set, the load parameter is LoadType.APPEND
            LoadType.APPEND -> {
                val articlesKeys = getRemoteKeyForLastItem(state)
                val nextKey = articlesKeys?.nextKey
                if (nextKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = articlesKeys != null)
                }
                nextKey
            }
        }

        try {
            val apiResult = newsApiService.getTopHeadlines(
                category = category,
                language = language,
                apiKey = BuildConfig.API_KEY,
                page = page
            )

            val articles = apiResult.body()?.articles
            val endOfPagination = articles.isNullOrEmpty()

            if (articles != null) {
                articlesDatabase.withTransaction {
                    // clear all tables in the database
                    if (loadType == LoadType.REFRESH) {
                        articlesDatabase.articlesKeysDao().clearArticlesKeys()
                        articlesDatabase.articlesDao().clearNews()
                    }

                    val prevKey = if (page == NEWS_STARTING_PAGE_NUMBER) null else page - 1
                    val nextKey = if (endOfPagination) null else page - 1

                    val keys = articles.map {
                        ArticlesKeys(it.url, prevKey, nextKey)
                    }

                    //  Save the articles and keys in the database.
                    articlesDatabase.articlesKeysDao().insertAllKeys(keys)
                    articlesDatabase.articlesDao().insertNews(articles)
                }
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPagination)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Article>): ArticlesKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { article ->
                // Get the remote keys of the last item retrieved
                articlesDatabase.articlesKeysDao().getArticlesKeysByUrl(article.url)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Article>): ArticlesKeys? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { article ->
                // Get the remote keys of the first items retrieved
                articlesDatabase.articlesKeysDao().getArticlesKeysByUrl(article.url)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, Article>
    ): ArticlesKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.url?.let { articleUrl ->
                articlesDatabase.articlesKeysDao().getArticlesKeysByUrl(articleUrl)
            }
        }
    }
}
