package com.mxy.bbs_client.program

import android.net.Uri

private const val NotLogin = "未登录"

private const val OnlyTest = "真想"

private const val NotOpenPost = "未打开帖子"

private const val EmptyStr = ""

object ProgramState {
    object PostState {
        var openedPost: String = NotOpenPost
        var homePostList: List<String> = listOf()
    }
    object UserInfoState {
        var alreadyLogin: Boolean = false
        //TODO:默认状态应该是未登录
        var username: String = OnlyTest
        var password: String = EmptyStr
        var nickname: String = EmptyStr
        var newNickname:String = EmptyStr
        var newPersonalSign: String = EmptyStr
        var newAvatar: Uri? = null
    }
}

