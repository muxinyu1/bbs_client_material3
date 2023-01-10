package com.mxy.bbs_client.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.mxy.bbs_client.program.state.PostState
import com.mxy.bbs_client.program.viewmodel.HomeScreenViewModel
import com.mxy.bbs_client.utility.rememberForeverLazyListState

private val PostCardPadding = PaddingValues(5.dp)

private const val EmptyPostId = "正在加载帖子"

@Composable
fun PostCardList(
    postList: List<PostState>,
    modifier: Modifier,
    homeScreenViewModel: HomeScreenViewModel
) {
    PostList(postList, modifier, homeScreenViewModel)
}

@Composable
private fun PostList(
    postStates: List<PostState>,
    modifier: Modifier,
    homeScreenViewModel: HomeScreenViewModel,
    lazyLoad: Boolean = true
) {
    val isRefreshing by homeScreenViewModel.isRefreshing.collectAsState()
    val isLoading by homeScreenViewModel.isLoading.collectAsState()
    val listState = rememberForeverLazyListState(key = "Home")
    listState.OnBottomReached(2) {
        homeScreenViewModel.loadMore()
    }
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = isRefreshing),
        modifier = modifier,
        onRefresh = { homeScreenViewModel.refresh() }
    )
    {
        if (lazyLoad) {
            LazyColumn(state = listState) {
                items(postStates.size) { i: Int ->
                    PostCard(
                        postState = postStates[i],
                        modifier = Modifier
                            .placeholder(isLoading, highlight = PlaceholderHighlight.fade())
                            .padding(2.dp),
                        homeScreenViewModel = homeScreenViewModel
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        } else {
            Column(modifier = modifier.verticalScroll(rememberScrollState())) {
                for (postId in postStates) {
                    PostCard(
                        postState = postId,
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

@Composable
fun LazyListState.OnBottomReached(
    buffer : Int = 0,
    loadMore : () -> Unit
) {
    require(buffer >= 0) { "${buffer}必须是正数" }

    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                ?:
                return@derivedStateOf true
            lastVisibleItem.index >=  layoutInfo.totalItemsCount - 1 - buffer
        }
    }

    LaunchedEffect(shouldLoadMore){
        snapshotFlow { shouldLoadMore.value }
            .collect { if (it) loadMore() }
    }
}

