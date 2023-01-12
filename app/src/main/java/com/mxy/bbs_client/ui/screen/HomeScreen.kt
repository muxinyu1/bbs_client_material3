package com.mxy.bbs_client.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mxy.bbs_client.program.viewmodel.HomeScreenViewModel
import com.mxy.bbs_client.program.viewmodel.MineScreenViewModel
import com.mxy.bbs_client.ui.component.Post
import com.mxy.bbs_client.ui.component.PostCardList

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun HomeScreen(homeScreenViewModel: HomeScreenViewModel, mineScreenViewModel: MineScreenViewModel) {
    val homeScreenState by homeScreenViewModel.homeScreenState.collectAsState()
    AnimatedVisibility(
        visible = homeScreenState.openedPost == null,
        enter = slideInHorizontally { -it },
        exit = slideOutHorizontally { -it }
    ) {
        PostCardList(
            homeScreenState.postList,
            modifier = Modifier.padding(5.dp),
            homeScreenViewModel
        )
    }
    AnimatedVisibility(
        visible = homeScreenState.openedPost != null,
        enter = slideInHorizontally { it },
        exit = slideOutHorizontally { it }
    ) {
        Post(
            postState = homeScreenState.postState,
            modifier = Modifier.padding(10.dp),
            mineScreenViewModel = mineScreenViewModel
        )
    }
}