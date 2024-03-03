package com.greildev.core.data.source.local.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.greildev.core.data.source.local.entities.GenresName

class GenresNameTypeConverter {
    @TypeConverter
    fun convertGenresNameToString(genresName: GenresName): String {
        return Gson().toJson(genresName)
    }
    @TypeConverter
    fun convertStringToGenresName(string: String): GenresName {
        return Gson().fromJson(string, GenresName::class.java)
    }
}