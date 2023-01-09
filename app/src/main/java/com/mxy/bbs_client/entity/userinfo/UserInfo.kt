package com.mxy.bbs_client.entity.userinfo

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.mxy.bbs_client.program.converter.Converter

@Entity(tableName = "UserInfo")
@TypeConverters(Converter::class)
data class UserInfo(
    @PrimaryKey val username: String,
    val nickname: String?,
    val personalSign: String?,
    val avatarUrl: Any?,
    val myPosts: List<String>,
    val myCollections: List<String>
)
