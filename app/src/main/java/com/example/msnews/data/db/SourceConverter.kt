package com.example.msnews.data.db

import androidx.room.TypeConverter
import com.example.msnews.data.model.Source

/**
 * Type converters are methods that tell Room how to convert custom types to and from known types
 * that Room can persist
 * */
class SourceConverter {
    @TypeConverter
    fun fromSource(source: Source): String {
        return "${source.id} : ${source.name}"
    }

    @TypeConverter
    fun toSource(string: String): Source {
        val source = string.split(":")
        return Source(id = source.first(), name = source.last())
    }
}
