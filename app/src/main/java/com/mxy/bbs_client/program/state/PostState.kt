package com.mxy.bbs_client.program.state

val DefaultPostState = PostState(
    owner = "加载中...",
    title = "加载中",
    content = "加载中",
    date = "",
    likeNum = 0,
    images = listOf(),
    reviews = listOf(),
    avatarUrl = "http://nahida8848.xyz:8086/home/nginx_root/repo/server/./avatars/default.png",
    nickname = "加载中",
    reviewNum = 0,
    postId = "正在加载帖子",
)

data class PostState(
    val postId: String,
    val owner: String,
    val title: String,
    val date: String,
    val content: String,
    val avatarUrl: Any,
    val images: List<Any>,
    val likeNum: Int,
    val reviewNum: Int,
    val nickname: String,
    val reviews: List<ReviewState>,
)
