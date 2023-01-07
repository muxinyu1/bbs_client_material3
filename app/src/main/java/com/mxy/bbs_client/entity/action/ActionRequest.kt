package com.mxy.bbs_client.entity.action

data class ActionRequest(
    val isTargetPost: Boolean?,
    val postId: String?,
    val reviewId: String?
)
