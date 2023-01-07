package com.mxy.bbs_client.entity.review

data class ReviewResponse(
    val success: Boolean?,
    val reviewResponseFailedReason: ReviewResponseFailedReason?,
    val review: Review?
)
