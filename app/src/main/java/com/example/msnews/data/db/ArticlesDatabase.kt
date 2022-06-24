package com.example.msnews.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.msnews.data.model.Article

@Database(entities = [Article::class], version = 1, exportSchema = false)
abstract class ArticlesDatabase : RoomDatabase() {

    abstract fun articlesDao(): ArticlesDao
}
