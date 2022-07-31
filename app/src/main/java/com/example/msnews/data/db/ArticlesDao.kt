package com.example.msnews.data.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.msnews.data.model.Article

@Dao
interface ArticlesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNews(articles: List<Article>)

    // That way, the articles table becomes the source of data for Paging.
    @Query("SELECT * from articles_table")
    fun getNews(): PagingSource<Int, Article>

    @Query("DELETE FROM articles_table")
    suspend fun clearNews()
}
