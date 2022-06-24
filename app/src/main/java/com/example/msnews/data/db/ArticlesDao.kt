package com.example.msnews.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.msnews.data.model.Article
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticlesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNews(articles: List<Article>)

    @Query("SELECT * from articles_table")
    fun getNews(): Flow<List<Article>>
}
