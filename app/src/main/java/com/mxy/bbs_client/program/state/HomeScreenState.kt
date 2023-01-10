package com.mxy.bbs_client.program.state

data class HomeScreenState(
    var postList: List<PostState>,
    val openedPost: String? = null,
    val placeHolder: Int = 0,
    val postState: PostState  = DefaultPostState
)
