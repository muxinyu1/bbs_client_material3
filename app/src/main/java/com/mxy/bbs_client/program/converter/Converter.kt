package com.mxy.bbs_client.program.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.mxy.bbs_client.program.state.UserInfoState

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
    fun fromAnyToStr(any: Any): String? {
        return gson.toJson(any)
    }

    @TypeConverter
    fun fromUserInfoStateToStr(userInfoState: UserInfoState): String {
        return gson.toJson(userInfoState)
    }

    @TypeConverter
    fun fromStrToUserInfoState(str: String): UserInfoState {
        return gson.fromJson(str, UserInfoState::class.java)
    }
}