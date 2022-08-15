package com.example.msnews.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.msnews.data.api.NewsApiService
import com.example.msnews.data.db.ArticlesDatabase
import com.example.msnews.data.model.Article
import com.example.msnews.data.utils.Constants.NETWORK_SEARCH_PAGE_SIZE
import kotlinx.coroutines.flow.Flow

class NewsRepositoryImpl(
    private val newsApi: NewsApiService,
    private val articlesDatabase: ArticlesDatabase,
) : NewsRepository {

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
}
