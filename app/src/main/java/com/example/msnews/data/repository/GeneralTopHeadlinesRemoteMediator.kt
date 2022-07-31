package com.example.msnews.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.msnews.data.api.NewsApiService
import com.example.msnews.data.db.ArticlesDatabase
import com.example.msnews.data.model.Article

@OptIn(ExperimentalPagingApi::class)
class GeneralTopHeadlinesRemoteMediator(
    private val newsApi: NewsApiService,
    private val newsDatabase: ArticlesDatabase,
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
        TODO("Not yet implemented")
    }
}
