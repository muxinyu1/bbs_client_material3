package com.mxy.bbs_client.entity.review

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.mxy.bbs_client.program.converter.Converter

@Entity(tableName = "Review")
@TypeConverters(Converter::class)
data class Review(
    @PrimaryKey val id: String,
    val targetPost: String?,
    val date: String?,
    val username: String?,
    val content: String?,
    val images: List<Any>?,
    val likeNum: Int?
)
