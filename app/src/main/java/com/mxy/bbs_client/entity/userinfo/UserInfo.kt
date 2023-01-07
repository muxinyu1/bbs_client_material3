package com.mxy.bbs_client.entity.userinfo

data class UserInfo(
    val username: String?,
    val nickname: String?,
    val personalSign: String?,
    val avatarUrl: String?,
    val myPosts: List<String>,
    val myCollections: List<String>
)
