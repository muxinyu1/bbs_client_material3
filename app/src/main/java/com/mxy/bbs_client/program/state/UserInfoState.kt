package com.mxy.bbs_client.program.state


data class UserInfoState(
    val username: String,
    val nickname: String ,
    val avatarUrl: Any,
    val personalSign: String,
    val myPosts: List<PostState>,
    val myCollections: List<PostState>
)

val DefaultUserInfoState = UserInfoState(
    username = "加载中...",
    nickname = "未登录",
    avatarUrl = "http://nahida8848.xyz:8086/home/nginx_root/repo/server/avatars/default.png",
    personalSign = "",
    myPosts = listOf(),
    myCollections = listOf()
)
