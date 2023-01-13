package com.mxy.bbs_client.program.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.mxy.bbs_client.program.state.HomeScreenState
import com.mxy.bbs_client.program.state.PostState
import com.mxy.bbs_client.program.state.ReviewState
import com.mxy.bbs_client.utility.Client
import com.mxy.bbs_client.utility.Utility
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.log

class HomeScreenViewModel(app: Application) : AndroidViewModel(app) {
    companion object {
        fun toPostStateList(postIds: List<String>): List<PostState> {
            val res = mutableListOf<PostState>()
            for (postId in postIds) {
                res.add(toPostState(postId))
            }
            return res
        }
        fun toPostState(postId: String): PostState {
            val postResponse = Client.getPost(postId)
            val userInfoResponse = Client.getUserInfo(postResponse.post!!.owner)
            Log.d("MineScreenRefresh", "针对单个postId的请求完成")
            val post = postResponse.post
            val userInfo = userInfoResponse.userInfo!!
            val reviewStateList = toReviewStateList(post.reviews)
            return PostState(
                postId = postId,
                owner = post.owner!!,
                title = post.title!!,
                date = post.date!!,
                content = post.content!!,
                avatarUrl = userInfo.avatarUrl!!,
                images = post.images,
                likeNum = post.likeNum!!,
                reviewNum = post.reviews.size,
                nickname = userInfo.nickname!!,
                reviews = reviewStateList,
            )
        }
        private fun toReviewStateList(reviewIds: List<String>): List<ReviewState> {
            val res = mutableListOf<ReviewState>()
            for (reviewId in reviewIds) {
                val reviewResponse = Client.getReview(reviewId)
                val userInfoResponse = Client.getUserInfo(reviewResponse.review!!.username)
                val review = reviewResponse.review
                val userInfo = userInfoResponse.userInfo!!
                res.add(
                    ReviewState(
                        date = review.date!!,
                        nickname = userInfo.nickname!!,
                        content = review.content!!,
                        images = review.images!!,
                        likeNum = review.likeNum!!,
                        personalSign = userInfo.personalSign!!,
                        avatarUrl = userInfo.avatarUrl!!,
                        reviewOwner = review.username!!
                    )
                )
            }
            return res
        }
    }

    private val _homeScreenState = MutableStateFlow(HomeScreenState(postList = listOf()))
    val homeScreenState = _homeScreenState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isRefreshing.asStateFlow()

    fun refresh() = with(Utility.IOCoroutineScope) {
        launch {
            _isRefreshing.update { true }
            val postIds = Client.getPostList().postIds
            val newPostList = mutableListOf<PostState>()
            for (postId in postIds) {
                newPostList.add(toPostState(postId))
                _homeScreenState.update {
                    HomeScreenState(postList = newPostList)
                }
                if (_isRefreshing.value) _isRefreshing.update { false }
            }
        }
    }

    fun loadMore() = with(Utility.IOCoroutineScope) {
        launch {
            Log.d("mxy!!!", "正在加载更多")
            val previousList = _homeScreenState.value.postList.toMutableList()
            val newPostIds = Client.getPostList().postIds
            for (newPostId in newPostIds) {
                val newPostState = toPostState(newPostId)
                previousList.add(newPostState)
                _homeScreenState.update {
                    HomeScreenState(
                        postList = previousList,
                        openedPost = it.openedPost,
                        placeHolder = it.placeHolder,
                        postState = it.postState
                    )
                }
            }
        }
    }

    fun refreshPost() = with(Utility.IOCoroutineScope) {
        launch {
            if (_homeScreenState.value.openedPost == null) {
                return@launch
            }
            val newPostState = toPostState(_homeScreenState.value.openedPost!!)
            _homeScreenState.value = HomeScreenState(
                _homeScreenState.value.postList,
                _homeScreenState.value.openedPost,
                1 - _homeScreenState.value.placeHolder,
                newPostState
            )
        }
    }

    fun openPost(postId: String) {
        _homeScreenState.value = HomeScreenState(
            openedPost = postId,
            placeHolder = 1 - _homeScreenState.value.placeHolder,
            postList = _homeScreenState.value.postList
        )
        with(Utility.IOCoroutineScope) {
            launch {
                val postResponse = Client.getPost(postId)
                val post = postResponse.post!!
                val userInfo = Client.getUserInfo(post.owner).userInfo!!
                val reviewStates = toReviewStateList(post.reviews)
                _homeScreenState.value = HomeScreenState(
                    postList = _homeScreenState.value.postList,
                    openedPost = postId,
                    placeHolder = 1 - _homeScreenState.value.placeHolder,
                    postState = PostState(
                        owner = post.owner!!,
                        title = post.title!!,
                        date = post.date!!,
                        content = post.content!!,
                        images = post.images,
                        likeNum = post.likeNum!!,
                        reviews = reviewStates,
                        avatarUrl = userInfo.avatarUrl!!,
                        nickname = userInfo.nickname!!,
                        postId = postId,
                        reviewNum = post.reviews.size
                    )
                )
            }
        }
    }

    fun closePost() {
        _homeScreenState.value = HomeScreenState(homeScreenState.value.postList, null)
    }

    init {
        with(Utility.IOCoroutineScope) {
            launch {
                val postIds = Client.getPostList().postIds
                val postList = mutableListOf<PostState>()
                for (postId in postIds) {
                    postList.add(toPostState(postId))
                    _homeScreenState.update { HomeScreenState(postList = postList) }
                    if (_isLoading.value) _isLoading.update { false }
                }
            }
        }
    }
}