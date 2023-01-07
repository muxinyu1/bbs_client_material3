package com.mxy.bbs_client.program.state

import android.net.Uri

val DefaultUserInfoState = UserInfoState(
    "加载中...",
    true,
    "加载中...",
    "加载中",
    "http://nahida8848.xyz:8086/home/nginx_root/repo/server/./avatars/default.png",
    "",
    "",
    "",
    null,
    listOf("正在加载帖子"),
    listOf()
)

data class UserInfoState(
    var username: String,
    var alreadyLogin: Boolean,
    var password: String ,
    var nickname: String ,
    var avatarUrl: String,
    var personalSign: String,
    var newNickname: String,
    var newPersonalSign: String,
    var newAvatar :Uri?,
    var myPosts: List<String>,
    var myCollections: List<String>
)
