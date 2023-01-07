package com.mxy.bbs_client.program.viewmodel

import androidx.lifecycle.ViewModel
import com.mxy.bbs_client.program.state.MineScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MineScreenViewModel : ViewModel() {
    private val _mineScreenState = MutableStateFlow(MineScreenState(false, null, 0))
    val mineScreenState = _mineScreenState.asStateFlow()
    fun loginSuccessfully(username: String) {
        _mineScreenState.value = MineScreenState(true, username, 0)
    }

    fun logout() {
        _mineScreenState.value = MineScreenState(false, null, 0)
    }

    fun refresh(username: String) {
        _mineScreenState.value =
            MineScreenState(true, username, 1 - _mineScreenState.value.placeholder)
    }
}