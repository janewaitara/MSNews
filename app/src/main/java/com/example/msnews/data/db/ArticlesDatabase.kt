package com.example.msnews.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.msnews.data.model.Article

@Database(entities = [Article::class], version = 1, exportSchema = false)
@TypeConverters(SourceConverter::class)
abstract class ArticlesDatabase : RoomDatabase() {

    abstract fun articlesDao(): ArticlesDao
}
