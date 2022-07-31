package com.example.msnews.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.msnews.BuildConfig
import com.example.msnews.data.api.NewsApiService
import com.example.msnews.data.db.ArticlesDao
import com.example.msnews.data.db.ArticlesDatabase
import com.example.msnews.data.model.ApiResponse
import com.example.msnews.data.model.Article
import com.example.msnews.data.model.Resource
import com.example.msnews.data.utils.Constants.NETWORK_SEARCH_PAGE_SIZE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException

class NewsRepositoryImpl(
    private val newsApi: NewsApiService,
    private val articlesDao: ArticlesDao,
    private val articlesDatabase: ArticlesDatabase,
) : NewsRepository {

    override suspend fun getNewsFromApiAndInsertIntoDb(
        category: String,
        language: String,
    ) {
        withContext(Dispatchers.IO) {
            val apiResponse = getTopHeadlinesFromApi(category, language).data
            // Added the empty list to stop the crashing when the api requests have been exceeded
            insertNewsToDb(apiResponse?.articles ?: listOf())
        }
    }

    override suspend fun getTopHeadlinesFromApi(
        category: String,
        language: String,
    ): Resource<ApiResponse> = try {

        val apiResult = newsApi.getTopHeadlines(category, language, BuildConfig.API_KEY)

        turnApiResultToResource(apiResult)
    } catch (error: Throwable) {
        Resource.Error(error.localizedMessage)
    }

    override fun getGeneralTopHeadlinesFromDB(
        category: String,
        language: String,
    ): Flow<PagingData<Article>> {

        val pagingSource = { articlesDatabase.articlesDao().getNews() }

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_SEARCH_PAGE_SIZE,
                enablePlaceholders = false
            ),
            remoteMediator = GeneralTopHeadlinesRemoteMediator(
                newsApiService = newsApi,
                articlesDatabase = articlesDatabase,
                category = category,
                language = language
            ),
            pagingSourceFactory = pagingSource
        ).flow
    }

    override fun getOtherTopHeadlinesFromApi(
        category: String,
        language: String,
    ): Flow<PagingData<Article>> = Pager(
        config = PagingConfig(
            pageSize = NETWORK_SEARCH_PAGE_SIZE,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { OtherTopHeadlinePagingSource(newsApi, category, language) }
    ).flow

    // Used without Paging
    override suspend fun getSearchedNews(
        searchQuery: String,
        language: String,
    ): Resource<ApiResponse> = try {
        val apiResult = newsApi.getSearchedNews(searchQuery, language, BuildConfig.API_KEY, 10, 1)

        turnApiResultToResource(apiResult)
    } catch (error: IOException) {
        Resource.Error(error.localizedMessage)
    }

    // Used With Paging
    /**
     * pagingSourceFactory provides an instance of the SearchedNewsPagingSource and its lambda
     * should always return a brand new PagingSource when invoked as PagingSource
     * instances are not reusable.
     * */
    override suspend fun getPagedSearchedNews(
        searchQuery: String,
        language: String,
    ): Flow<PagingData<Article>> = Pager(
        config = PagingConfig(
            pageSize = NETWORK_SEARCH_PAGE_SIZE,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { SearchNewsPagingSource(newsApi, searchQuery, language) }
    ).flow

    private fun turnApiResultToResource(apiResult: Response<ApiResponse>): Resource<ApiResponse> =
        when {
            apiResult.isSuccessful -> Resource.Success(apiResult.body()!!)
            else -> Resource.Error(apiResult.message())
        }

    override suspend fun insertNewsToDb(articles: List<Article>) {
        if (articles.isNotEmpty()) {
            articlesDao.insertNews(articles)
        }
    }

    override fun getNewsFromDb(): Flow<List<Article>> = articlesDao.getNews()
}
