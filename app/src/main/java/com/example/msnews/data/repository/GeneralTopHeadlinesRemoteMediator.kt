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
    private val language: String
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
            LoadType.REFRESH -> {
                // TODO
                0
            }
            LoadType.PREPEND -> {
                // TODO
                0
            }
            LoadType.APPEND -> {
                // TODO
                0
            }
        }

        try {
            val apiResult = newsApiService.getTopHeadlines(
                category = category,
                language = language,
                apiKey = BuildConfig.API_KEY,
                page = 1
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
}
