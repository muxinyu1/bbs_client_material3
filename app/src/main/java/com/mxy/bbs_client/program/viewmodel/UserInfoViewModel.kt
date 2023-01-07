package com.mxy.bbs_client.program.viewmodel

import androidx.lifecycle.ViewModel
import com.mxy.bbs_client.program.state.DefaultUserInfoState
import com.mxy.bbs_client.program.state.UserInfoState
import com.mxy.bbs_client.ui.component.DefaultUserInfo
import com.mxy.bbs_client.utility.Client
import com.mxy.bbs_client.utility.Utility
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserInfoViewModel(private var username: String): ViewModel() {
    private val _userInfoState = MutableStateFlow(DefaultUserInfo)
    val userInfoState = _userInfoState.asStateFlow()
    init {
        with(Utility.IOCoroutineScope) {
            launch {
                val userInfoResponse = Client.getUserInfo(username)
                _userInfoState.value = userInfoResponse.userInfo!!
            }
        }
    }
}