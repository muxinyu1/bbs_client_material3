package com.mxy.bbs_client.entity.userinfo

import com.mxy.bbs_client.entity.user.UserResponseFailedReason
import com.mxy.bbs_client.entity.userinfo.UserInfo

data class UserInfoResponse(
    val success: Boolean?,
    val userResponseFailedReason: UserResponseFailedReason?,
    val userInfo: UserInfo?
)
