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

    fun refreshPost() = with(Utility.IOCoroutineScope) {
        launch {
            _homeScreenState.value = HomeScreenState(
                _homeScreenState.value.postList,
                _homeScreenState.value.openedPost,
                1 - _homeScreenState.value.placeHolder
            )
        }
    }

    fun openPost(postId: String) {
        _homeScreenState.value = HomeScreenState(homeScreenState.value.postList, postId)
    }

    fun closePost() {
        _homeScreenState.value = HomeScreenState(homeScreenState.value.postList, null)
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