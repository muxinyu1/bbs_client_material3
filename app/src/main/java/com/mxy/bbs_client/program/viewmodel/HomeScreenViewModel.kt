package com.mxy.bbs_client.program.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mxy.bbs_client.program.state.HomeScreenState
import com.mxy.bbs_client.utility.Client
import com.mxy.bbs_client.utility.Utility
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeScreenViewModel : ViewModel() {
    private val _homeScreenState = MutableStateFlow(HomeScreenState(postList = listOf()))
    val homeScreenState = _homeScreenState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isRefreshing.asStateFlow()

    fun refresh() = with(Utility.IOCoroutineScope) {
        launch {
            _isRefreshing.update { true }
            _homeScreenState.value = HomeScreenState(postList = Client.getPostList().postIds)
            _isRefreshing.update { false }
        }
    }


    init {
        with(Utility.IOCoroutineScope) {
            launch {
                _homeScreenState.value = HomeScreenState(postList = Client.getPostList().postIds)
                _isLoading.update { false }
            }
        }
    }
}