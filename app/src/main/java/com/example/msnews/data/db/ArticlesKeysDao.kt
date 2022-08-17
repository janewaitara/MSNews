package com.example.msnews.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ArticlesKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllKeys(articlesKeys: List<ArticlesKeys>)

    @Query("SELECT * FROM articles_keys WHERE url = :url")
    suspend fun getArticlesKeysByUrl(url: String): ArticlesKeys?

    @Query("DELETE FROM articles_keys")
    suspend fun clearArticlesKeys()
}
