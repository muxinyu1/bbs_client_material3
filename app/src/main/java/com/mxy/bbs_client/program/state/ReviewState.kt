package com.mxy.bbs_client.program.state

data class ReviewState(
    val reviewOwner: String,
    val date: String,
    val nickname: String,
    val content: String,
    val images: List<Any>,
    val likeNum: Int,
    val personalSign: String,
    val avatarUrl: Any
)
