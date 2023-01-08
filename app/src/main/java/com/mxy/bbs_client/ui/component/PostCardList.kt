package com.mxy.bbs_client.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.mxy.bbs_client.program.viewmodel.HomeScreenViewModel

private val PostCardPadding = PaddingValues(5.dp)

private const val EmptyPostId = "正在加载帖子"

@Composable
fun PostCardList(
    postList: List<String>,
    modifier: Modifier,
    homeScreenViewModel: HomeScreenViewModel
) {
    PostList(postList, modifier, homeScreenViewModel)
}

@Composable
private fun PostList(
    postIds: List<String>,
    modifier: Modifier,
    homeScreenViewModel: HomeScreenViewModel,
    lazyLoad: Boolean = false
) {
    val isRefreshing by homeScreenViewModel.isRefreshing.collectAsState()
    val isLoading by homeScreenViewModel.isLoading.collectAsState()
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = isRefreshing),
        modifier = modifier,
        onRefresh = { homeScreenViewModel.refresh() }
    )
    {
        if (lazyLoad) {
            LazyColumn {
                items(postIds.size) { i: Int ->
                    if (postIds[i] != EmptyPostId) {
                        PostCard(
                            postId = postIds[i],
                            modifier = Modifier
                                .placeholder(isLoading, highlight = PlaceholderHighlight.fade())
                                .padding(2.dp),
                            homeScreenViewModel = homeScreenViewModel
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        } else {
            Column(modifier = modifier.verticalScroll(rememberScrollState())) {
                for (postId in postIds) {
                    if (postId != EmptyPostId) {
                        PostCard(
                            postId = postId,
                            modifier = Modifier
                                .placeholder(
                                    isLoading,
                                    highlight = PlaceholderHighlight.fade()
                                )
                                .padding(2.dp),
                            homeScreenViewModel = homeScreenViewModel
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }

}