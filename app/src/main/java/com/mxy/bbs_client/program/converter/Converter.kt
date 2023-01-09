package com.mxy.bbs_client.program.converter

import android.annotation.SuppressLint
import androidx.room.TypeConverter
import com.google.gson.Gson

class Converter {

    private val gson by lazy {
        Gson()
    }

    @TypeConverter
    fun fromListToStr(list: List<Any>): String? {
        return gson.toJson(list)
    }

    @TypeConverter
    @Suppress("UNCHECKED_CAST")
    fun fromStrToList(string: String): List<Any> {
        val lst = gson.fromJson(string, List::class.java)
        return lst as List<Any>
    }

    @TypeConverter
    fun fromListStrToStr(list: List<String>): String? {
        return gson.toJson(list)
    }

    @TypeConverter
    @Suppress("UNCHECKED_CAST")
    fun fromStrToListStr(str: String): List<String> {
        return gson.fromJson(str, List::class.java) as List<String>
    }

    @TypeConverter
    fun fromAnyToString(any: Any): String? {
        return gson.toJson(any)
    }
}