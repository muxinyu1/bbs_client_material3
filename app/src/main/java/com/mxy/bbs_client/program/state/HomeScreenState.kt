package com.mxy.bbs_client.program.state

data class HomeScreenState(
    val postList: List<String>,
    val openedPost: String? = null,
    val placeHolder: Int = 0
)
