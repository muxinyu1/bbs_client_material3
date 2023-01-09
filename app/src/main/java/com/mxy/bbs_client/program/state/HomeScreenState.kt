package com.mxy.bbs_client.program.state

data class HomeScreenState(
    val postList: List<PostState>,
    val openedPost: String? = null,
    val placeHolder: Int = 0,
    val postState: PostState  = DefaultPostState
)
