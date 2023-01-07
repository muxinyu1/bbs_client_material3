package com.mxy.bbs_client.entity.post

data class PostResponse(
    val success: Boolean?,
    val postResponseFailedReason: PostResponseFailedReason?,
    val post: Post?
)
