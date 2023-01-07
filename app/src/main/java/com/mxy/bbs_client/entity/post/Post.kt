package com.mxy.bbs_client.entity.post

data class Post(
    val id: String?,
    val date: String?,
    val owner: String?,
    val title: String?,
    val content: String?,
    val images: List<String>,
    val likeNum: Int?,
    val reviews: List<String>
)

