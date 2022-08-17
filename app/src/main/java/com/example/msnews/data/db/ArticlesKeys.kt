package com.example.msnews.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * stores the next and previous page keys for each Article
 */
@Entity(tableName = "articles_keys")
data class ArticlesKeys(
    @PrimaryKey val url: String,
    val prevKey: Int?,
    val nextKey: Int?
)
