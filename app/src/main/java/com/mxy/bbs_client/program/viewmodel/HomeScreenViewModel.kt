package com.mxy.bbs_client.program.viewmodel

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

class HomeScreenViewModel : ViewModel() {
    companion object {
        fun toPostStateList(postIds: List<String>): List<PostState> {
            val res = mutableListOf<PostState>()
            for (postId in postIds) {
                val postResponse = Client.getPost(postId)
                val userInfoResponse = Client.getUserInfo(postResponse.post!!.owner)
                val post = postResponse.post
                val userInfo = userInfoResponse.userInfo!!
                res.add(
                    PostState(
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
                        reviews = listOf()
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
            _homeScreenState.value =
                HomeScreenState(postList = toPostStateList(Client.getPostList().postIds))
            _isRefreshing.update { false }
        }
    }

    fun refreshPost() = with(Utility.IOCoroutineScope) {
        launch {
            _homeScreenState.value = HomeScreenState(
                _homeScreenState.value.postList,
                _homeScreenState.value.openedPost,
                1 - _homeScreenState.value.placeHolder,
                _homeScreenState.value.postState
            )
        }
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
                _homeScreenState.value =
                    HomeScreenState(postList = toPostStateList(Client.getPostList().postIds))
                _isLoading.update { false }
            }
        }
    }
}