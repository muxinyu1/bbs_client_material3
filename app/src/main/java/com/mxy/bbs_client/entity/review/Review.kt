package com.mxy.bbs_client.entity.review

data class Review(
    val id: String?,
    val targetPost: String?,
    val date: String?,
    val username: String?,
    val content: String?,
    val images: List<Any>?,
    val likeNum: Int?
)
