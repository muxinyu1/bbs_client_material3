package com.mxy.bbs_client.program.viewmodel

import androidx.lifecycle.ViewModel
import com.mxy.bbs_client.program.state.AppState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel: ViewModel() {
    private val _appState = MutableStateFlow(AppState(0))
    val appState = _appState.asStateFlow()
    fun changeScreenTo(index: Int) {
        _appState.value = AppState(index)
    }
}