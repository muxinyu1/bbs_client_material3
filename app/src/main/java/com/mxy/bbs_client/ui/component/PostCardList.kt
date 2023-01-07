package com.mxy.bbs_client.ui.component

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.mxy.bbs_client.program.ProgramState
import com.mxy.bbs_client.utility.Client
import com.mxy.bbs_client.utility.Utility
import compose.icons.FeatherIcons
import compose.icons.feathericons.RefreshCcw
import de.charlex.compose.BottomDrawerScaffold
import kotlinx.coroutines.launch

private val PostCardPadding = PaddingValues(5.dp)

private const val EmptyPostId = "正在加载帖子"

@Composable
fun PostCardList() {
    val postIdsState = remember {
        mutableStateOf(ProgramState.PostState.homePostList)
    }
    PostList(postIdsState.value)
//    BottomDrawerScaffold(
//        drawerContent = {
//            AddContent(isPost = true, modifier = Modifier.padding(5.dp))
//                        },
//        floatingActionButton = {
//            FloatingActionButton(onClick = { /*TODO:刷新帖子列表*/ }, modifier = Modifier.size(70.dp)) {
//                Icon(FeatherIcons.RefreshCcw, contentDescription = "Refresh")
//            }
//        },
//        drawerPeekHeight = 20.dp
//    ) {
//
//    }
    with(Utility.IOCoroutineScope) {
        launch {
            val postListResponse = Client.getPostList()
            postIdsState.value = postListResponse.postIds
        }
    }
}

@Composable
private fun PostList(postIds: List<String>) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        for (postId in postIds) {
            if (postId != EmptyPostId) {
                PostCard(postId = postId, modifier = Modifier.padding(2.dp))
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}