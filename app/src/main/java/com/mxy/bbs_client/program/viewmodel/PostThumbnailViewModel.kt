package com.mxy.bbs_client.program.viewmodel

import androidx.lifecycle.ViewModel
import com.mxy.bbs_client.ui.component.DefaultPost
import com.mxy.bbs_client.ui.component.DefaultUserInfo
import com.mxy.bbs_client.utility.Client
import com.mxy.bbs_client.utility.Utility
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PostThumbnailViewModel(private val postId: String): ViewModel() {
    private val _postState = MutableStateFlow(DefaultPost)
    private val _userInfoState = MutableStateFlow(DefaultUserInfo)
    val postState = _postState.asStateFlow()
    val userInfoState = _userInfoState.asStateFlow()
    init {
        with(Utility.IOCoroutineScope) {
            launch {
                val postResponse = Client.getPost(postId)
                _postState.value = postResponse.post!!
                val userInfoResponse = Client.getUserInfo(postResponse.post.owner)
                _userInfoState.value = userInfoResponse.userInfo!!
            }
        }
    }
}