package com.mxy.bbs_client.entity.post

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.mxy.bbs_client.program.converter.Converter

@Entity(tableName = "Post")
@TypeConverters(Converter::class)
data class Post(
    @PrimaryKey val id: String,
    val date: String?,
    val owner: String?,
    val title: String?,
    val content: String?,
    val images: List<Any>,
    val likeNum: Int?,
    val reviews: List<String>
)

