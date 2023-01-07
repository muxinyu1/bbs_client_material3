package com.mxy.bbs_client.entity.user

data class UserResponse(
    val success: Boolean?,
    val userResponseFailedReason: UserResponseFailedReason?,
    val user: User?
)
